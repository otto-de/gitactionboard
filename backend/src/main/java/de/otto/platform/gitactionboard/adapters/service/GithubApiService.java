package de.otto.platform.gitactionboard.adapters.service;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Slf4j
@Service
public class GithubApiService {
  private final String authToken;
  private final RestClient.Builder restClientBuilder;

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
    return getRestClient(accessToken).get().uri(url).retrieve().body(responseType);
  }
}
