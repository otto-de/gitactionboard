package de.otto.platform.gitactionboard.adapters.service;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.TOO_MANY_REQUESTS;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicReference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
public class GithubApiService {
  private static final String RATE_LIMIT_REMAINING_HEADER = "x-ratelimit-remaining";
  private static final String RATE_LIMIT_RESET_HEADER = "x-ratelimit-reset";

  private final String authToken;
  private final RestClient.Builder restClientBuilder;
  private final AtomicReference<Instant> rateLimitResetAt = new AtomicReference<>(Instant.EPOCH);

  public GithubApiService(
      @Qualifier("domainName") String baseUri,
      @Qualifier("ownerName") String ownerName,
      @Qualifier("authToken") String authToken,
      RestClient.Builder restClientBuilder) {
    this.restClientBuilder = restClientBuilder.baseUrl("%s/repos/%s".formatted(baseUri, ownerName));
    this.authToken = authToken;
  }

  private RestClient getRestClient(String accessToken) {
    return restClientBuilder
        .defaultHeader(
            AUTHORIZATION,
            Optional.ofNullable(accessToken)
                .filter(token -> !token.startsWith("Basic"))
                .orElse(authToken))
        .build();
  }

  public <T> T getForObject(String url, String accessToken, Class<T> responseType) {
    final RestClient restClient = getRestClient(accessToken);

    // Another request may already have tripped the GitHub rate limit; hold off until it resets.
    waitUntilRateLimitResets();

    try {
      return restClient.get().uri(url).retrieve().body(responseType);
    } catch (HttpClientErrorException exception) {
      if (!isRateLimited(exception)) {
        throw exception;
      }

      rememberRateLimitReset(exception);
      waitUntilRateLimitResets();

      // Retry once now that the window has reset; any further failure propagates to the caller.
      return restClient.get().uri(url).retrieve().body(responseType);
    }
  }

  private static boolean isRateLimited(HttpClientErrorException exception) {
    final boolean rateLimitStatus =
        FORBIDDEN.equals(exception.getStatusCode())
            || TOO_MANY_REQUESTS.equals(exception.getStatusCode());
    final HttpHeaders headers = exception.getResponseHeaders();
    return rateLimitStatus
        && headers != null
        && "0".equals(headers.getFirst(RATE_LIMIT_REMAINING_HEADER));
  }

  private void rememberRateLimitReset(HttpClientErrorException exception) {
    final HttpHeaders headers = exception.getResponseHeaders();
    final String resetEpochSeconds =
        headers == null ? null : headers.getFirst(RATE_LIMIT_RESET_HEADER);
    if (resetEpochSeconds == null) {
      return;
    }
    final Instant resetAt = Instant.ofEpochSecond(Long.parseLong(resetEpochSeconds));
    rateLimitResetAt.accumulateAndGet(
        resetAt, (current, candidate) -> candidate.isAfter(current) ? candidate : current);
  }

  private void waitUntilRateLimitResets() {
    final Duration waitFor = Duration.between(Instant.now(), rateLimitResetAt.get());
    if (waitFor.isZero() || waitFor.isNegative()) {
      return;
    }
    log.warn("GitHub rate limit hit; waiting {}s until reset", waitFor.toSeconds());
    try {
      Thread.sleep(waitFor.toMillis());
    } catch (InterruptedException interruptedException) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException(
          "Interrupted while waiting for GitHub rate limit to reset", interruptedException);
    }
  }
}
