package de.otto.platform.gitactionboard.adapters.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockserver.matchers.Times.once;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.verify.VerificationTimes.exactly;

import de.otto.platform.gitactionboard.IntegrationTest;
import de.otto.platform.gitactionboard.Sequential;
import java.time.Instant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.integration.ClientAndServer;
import org.springframework.web.client.RestClient;

@IntegrationTest
@Sequential
class GithubApiServiceTest {
  private static final String OWNER = "johndoe";
  private static final String PATH = "/hello-world/branches";
  private static final String FULL_PATH = "/repos/%s%s".formatted(OWNER, PATH);
  private static final String ACCESS_TOKEN = "accessToken";
  private static final String RESPONSE_BODY = "{\"ok\":true}";

  private ClientAndServer mockServer;
  private GithubApiService githubApiService;

  @BeforeEach
  void setUp() {
    mockServer = ClientAndServer.startClientAndServer();
    githubApiService =
        new GithubApiService(
            "http://localhost:%d".formatted(mockServer.getPort()),
            OWNER,
            "defaultToken",
            RestClient.builder());
  }

  @AfterEach
  void tearDown() {
    mockServer.stop();
  }

  @Test
  void shouldWaitForRateLimitToResetAndThenRetryTheRequest() {
    // First call is rate limited; reset ~1s in the future.
    mockServer
        .when(request().withMethod("GET").withPath(FULL_PATH), once())
        .respond(
            response()
                .withStatusCode(403)
                .withHeader("x-ratelimit-remaining", "0")
                .withHeader("x-ratelimit-reset", String.valueOf(Instant.now().getEpochSecond() + 1)));

    // Second call (after the wait) succeeds.
    mockServer
        .when(request().withMethod("GET").withPath(FULL_PATH))
        .respond(response().withStatusCode(200).withBody(RESPONSE_BODY));

    final String body = githubApiService.getForObject(PATH, ACCESS_TOKEN, String.class);

    assertThat(body).isEqualTo(RESPONSE_BODY);
    mockServer.verify(request().withMethod("GET").withPath(FULL_PATH), exactly(2));
  }
}
