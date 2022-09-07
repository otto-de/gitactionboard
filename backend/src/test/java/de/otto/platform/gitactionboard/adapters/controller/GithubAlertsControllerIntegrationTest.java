package de.otto.platform.gitactionboard.adapters.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static de.otto.platform.gitactionboard.TestUtil.invokeGetApiAndValidate;
import static de.otto.platform.gitactionboard.TestUtil.readFile;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import de.otto.platform.gitactionboard.IntegrationTest;
import de.otto.platform.gitactionboard.WireMockExtension;
import de.otto.platform.gitactionboard.domain.repository.NotificationRepository;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Objects;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@DirtiesContext
@IntegrationTest
class GithubAlertsControllerIntegrationTest {
  private static final String API_BASE_PATH = "/repos/johndoe/hello-world";
  private static final String SECURITY_SCAN_ALERTS_ENDPOINT = "/v1/alerts/secrets";
  private static final String SECRETS_SCAN_ALERTS_URL =
      String.format("%s/secret-scanning/alerts?state=open&page=1&per_page=100", API_BASE_PATH);
  private static final String SECRETS_SCAN_ALERTS_JSON = "testData/secretsScanAlerts.json";
  private static final String SECURITY_SCAN_ALERTS_JSON = "testData/securityScanAlerts.json";

  private void stubSecretsScanApiRequests() {
    stubFor(
        WireMock.get(SECRETS_SCAN_ALERTS_URL)
            .willReturn(
                aResponse()
                    .withStatus(SC_OK)
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .withBody(readFile(SECRETS_SCAN_ALERTS_JSON))));
    stubFor(WireMock.post("/webhook/notifications").willReturn(aResponse().withStatus(SC_OK)));
  }

  @BeforeEach
  void setUp() {
    WireMock.reset();
  }

  @AfterEach
  void tearDown() {
    WireMock.reset();
  }

  @Nested
  @AutoConfigureMockMvc
  @ExtendWith(WireMockExtension.class)
  @SpringBootTest(
      webEnvironment = RANDOM_PORT,
      properties = {"ENABLE_GITHUB_SECRETS_SCAN_ALERTS_MONITORING=false"})
  @SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC")
  class DisableSecurityAlerts {

    @Autowired private MockMvc mockMvc;

    @ParameterizedTest
    @NullSource
    @CsvSource(value = {"accessToken"})
    @SneakyThrows
    void shouldReturnNotFoundWhileFetchingSecurityAlerts(String accessToken) {
      final MockHttpServletRequestBuilder requestBuilder =
          Objects.isNull(accessToken)
              ? get(SECURITY_SCAN_ALERTS_ENDPOINT)
              : get(SECURITY_SCAN_ALERTS_ENDPOINT).header(AUTHORIZATION, accessToken);

      mockMvc.perform(requestBuilder).andExpect(status().isNotFound());
    }
  }

  @Nested
  @AutoConfigureMockMvc
  @ExtendWith(WireMockExtension.class)
  @SpringBootTest(webEnvironment = RANDOM_PORT)
  class WithoutCacheForSecurityAlerts {

    @Autowired private MockMvc mockMvc;

    @Value("${wiremock.webhook.url}")
    private String notificationWebHookUrl;

    @Test
    @SneakyThrows
    void shouldFetchSecurityAlerts() {
      stubSecretsScanApiRequests();

      invokeGetApiAndValidate(
          mockMvc,
          SECURITY_SCAN_ALERTS_ENDPOINT,
          APPLICATION_JSON_VALUE,
          content().json(readFile(SECURITY_SCAN_ALERTS_JSON)));

      assertThat(WireMock.getAllServeEvents()).hasSize(4);
      final EqualToPattern valuePattern = new EqualToPattern("");
      verify(
          getRequestedFor(urlEqualTo(SECRETS_SCAN_ALERTS_URL))
              .withHeader(AUTHORIZATION, valuePattern));
      verify(3, postRequestedFor(urlEqualTo(notificationWebHookUrl)));
    }

    @Test
    @SneakyThrows
    void shouldFetchSecurityAlertsUsingAccessTokenFromCookie() {
      stubSecretsScanApiRequests();

      final String dummyAccessToken = "dummy_access_token";

      invokeGetApiAndValidate(
          mockMvc,
          SECURITY_SCAN_ALERTS_ENDPOINT,
          APPLICATION_JSON_VALUE,
          content().json(readFile(SECURITY_SCAN_ALERTS_JSON)),
          dummyAccessToken);

      final EqualToPattern valuePattern = new EqualToPattern(dummyAccessToken);
      assertThat(WireMock.getAllServeEvents()).hasSize(1);
      verify(
          getRequestedFor(urlEqualTo(SECRETS_SCAN_ALERTS_URL))
              .withHeader(AUTHORIZATION, valuePattern));
    }

    @Test
    @SneakyThrows
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    void shouldNotThrowErrorIfWorkflowsApiCallFails() {
      stubFor(
          WireMock.get(SECRETS_SCAN_ALERTS_URL)
              .willReturn(
                  aResponse()
                      .withStatus(SC_NOT_FOUND)
                      .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)));

      invokeGetApiAndValidate(
          mockMvc, SECURITY_SCAN_ALERTS_ENDPOINT, APPLICATION_JSON_VALUE, content().json("[]"));
    }
  }

  @Nested
  @AutoConfigureMockMvc
  @ExtendWith(WireMockExtension.class)
  @SpringBootTest(
      webEnvironment = RANDOM_PORT,
      properties = {"spring.cache.cache-names=securityScanAlerts", "spring.cache.type=caffeine"})
  class WithCacheSecurityScanAlerts {
    @Autowired private MockMvc mockMvc;

    @Value("${wiremock.webhook.url}")
    private String notificationWebHookUrl;

    @Autowired private CacheManager cacheManager;

    @Autowired private NotificationRepository notificationRepository;

    @BeforeEach
    void setUp() {
      stubSecretsScanApiRequests();
    }

    @AfterEach
    void tearDown() {
      notificationRepository.deleteAll();
      cacheManager.getCacheNames().stream()
          .map(cacheManager::getCache)
          .filter(Objects::nonNull)
          .forEach(org.springframework.cache.Cache::clear);
    }

    @Test
    @SneakyThrows
    void shouldFetchSecurityScanAlerts() {
      final ResultMatcher resultMatcher = content().json(readFile(SECURITY_SCAN_ALERTS_JSON));

      invokeGetApiAndValidate(
          mockMvc, SECURITY_SCAN_ALERTS_ENDPOINT, APPLICATION_JSON_VALUE, resultMatcher);

      assertThat(WireMock.getAllServeEvents()).hasSize(4);
      final EqualToPattern valuePattern = new EqualToPattern("");
      verify(
          getRequestedFor(urlEqualTo(SECRETS_SCAN_ALERTS_URL))
              .withHeader(AUTHORIZATION, valuePattern));
      verify(3, postRequestedFor(urlEqualTo(notificationWebHookUrl)));

      WireMock.resetAllRequests();

      invokeGetApiAndValidate(
          mockMvc, SECURITY_SCAN_ALERTS_ENDPOINT, APPLICATION_JSON_VALUE, resultMatcher);
      assertThat(WireMock.getAllServeEvents()).isEmpty();
    }

    @Test
    @SneakyThrows
    void shouldShareCacheWithOtherUsersWhileFetchingSecurityScanAlerts() {
      final ResultMatcher resultMatcher = content().json(readFile(SECURITY_SCAN_ALERTS_JSON));

      final String dummyToken1 = "dummyToken1";

      invokeGetApiAndValidate(
          mockMvc,
          SECURITY_SCAN_ALERTS_ENDPOINT,
          APPLICATION_JSON_VALUE,
          resultMatcher,
          dummyToken1);

      assertThat(WireMock.getAllServeEvents()).hasSize(4);
      final EqualToPattern valuePattern = new EqualToPattern(dummyToken1);
      verify(
          getRequestedFor(urlEqualTo(SECRETS_SCAN_ALERTS_URL))
              .withHeader(AUTHORIZATION, valuePattern));
      verify(3, postRequestedFor(urlEqualTo(notificationWebHookUrl)));

      WireMock.resetAllRequests();

      invokeGetApiAndValidate(
          mockMvc,
          SECURITY_SCAN_ALERTS_ENDPOINT,
          APPLICATION_JSON_VALUE,
          resultMatcher,
          "dummyToken2");
      assertThat(WireMock.getAllServeEvents()).isEmpty();
    }
  }
}
