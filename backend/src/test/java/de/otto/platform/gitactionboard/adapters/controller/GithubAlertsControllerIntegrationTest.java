package de.otto.platform.gitactionboard.adapters.controller;

import static de.otto.platform.gitactionboard.TestUtil.invokeGetApiAndValidate;
import static de.otto.platform.gitactionboard.TestUtil.mockGetRequestWithPageQueryParam;
import static de.otto.platform.gitactionboard.TestUtil.mockRequest;
import static de.otto.platform.gitactionboard.TestUtil.readFile;
import static de.otto.platform.gitactionboard.TestUtil.verifyRequest;
import static de.otto.platform.gitactionboard.TestUtil.verifyRequestWithPageQueryParams;
import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static jakarta.servlet.http.HttpServletResponse.SC_OK;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.verify.VerificationTimes.exactly;
import static org.mockserver.verify.VerificationTimes.once;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.otto.platform.gitactionboard.IntegrationTest;
import de.otto.platform.gitactionboard.domain.repository.NotificationRepository;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Objects;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.Parameter;
import org.mockserver.springtest.MockServerTest;
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
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
@SuppressFBWarnings("NP_UNWRITTEN_FIELD")
class GithubAlertsControllerIntegrationTest {
  private static final String API_BASE_PATH = "/repos/johndoe/hello-world";
  private static final String SECURITY_SCAN_ALERTS_ENDPOINT = "/v1/alerts/secrets";
  private static final String CODE_STANDARD_VIOLATION_ALERTS_ENDPOINT =
      "/v1/alerts/code-standard-violations";
  private static final String SECRETS_SCAN_ALERTS_URL =
      "%s/secret-scanning/alerts".formatted(API_BASE_PATH);
  private static final String CODE_SCAN_ALERTS_URL =
      "%s/code-scanning/alerts".formatted(API_BASE_PATH);
  private static final String SECRETS_SCAN_ALERTS_JSON = "testData/secretsScanAlerts.json";
  private static final String SECURITY_SCAN_ALERTS_JSON = "testData/securityScanAlerts.json";
  private static final String GITHUB_CODE_SCAN_ALERTS_JSON = "testData/githubCodeScanAlerts.json";
  private static final String CODE_STANDARD_VIOLATIONS_JSON =
      "testData/codeStandardViolations.json";

  private void stubSecretsScanApiRequests(MockServerClient mockServerClient) {
    mockGetRequestWithPageQueryParam(
        mockServerClient,
        SECRETS_SCAN_ALERTS_URL,
        readFile(SECRETS_SCAN_ALERTS_JSON),
        "state",
        "page",
        "per_page");
    mockServerClient
        .when(request().withMethod("POST").withPath("/webhook/notifications"))
        .respond(response().withStatusCode(SC_OK));
  }

  private void stubCodeScanApiRequests(MockServerClient mockServerClient) {
    mockGetRequestWithPageQueryParam(
        mockServerClient,
        CODE_SCAN_ALERTS_URL,
        readFile(GITHUB_CODE_SCAN_ALERTS_JSON),
        "state",
        "page",
        "per_page");

    mockServerClient
        .when(request().withMethod("POST").withPath("/webhook/notifications"))
        .respond(response().withStatusCode(SC_OK));
  }

  @Nested
  @AutoConfigureMockMvc
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
  @SpringBootTest(
      webEnvironment = RANDOM_PORT,
      properties = {"ENABLE_GITHUB_CODE_SCAN_ALERTS_MONITORING=false"})
  @SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC")
  class DisableCodeStandardViolationAlerts {

    @Autowired private MockMvc mockMvc;

    @ParameterizedTest
    @NullSource
    @CsvSource(value = {"accessToken"})
    @SneakyThrows
    void shouldReturnNotFoundWhileFetchingCodeStandardViolationAlerts(String accessToken) {
      final MockHttpServletRequestBuilder requestBuilder =
          Objects.isNull(accessToken)
              ? get(CODE_STANDARD_VIOLATION_ALERTS_ENDPOINT)
              : get(CODE_STANDARD_VIOLATION_ALERTS_ENDPOINT).header(AUTHORIZATION, accessToken);

      mockMvc.perform(requestBuilder).andExpect(status().isNotFound());
    }
  }

  @Nested
  @AutoConfigureMockMvc
  @MockServerTest
  @SpringBootTest(webEnvironment = RANDOM_PORT)
  class WithoutCacheForSecurityAlerts {
    @SuppressFBWarnings("UWF_UNWRITTEN_FIELD")
    private MockServerClient mockServerClient;

    @Autowired private MockMvc mockMvc;
    @Autowired private NotificationRepository notificationRepository;

    @Value("${wiremock.webhook.url}")
    private String notificationWebHookUrl;

    @AfterEach
    void tearDown() {
      notificationRepository.deleteAll();
    }

    @Test
    @SneakyThrows
    void shouldFetchSecurityAlerts() {
      stubSecretsScanApiRequests(mockServerClient);

      invokeGetApiAndValidate(
          mockMvc,
          SECURITY_SCAN_ALERTS_ENDPOINT,
          APPLICATION_JSON_VALUE,
          content().json(readFile(SECURITY_SCAN_ALERTS_JSON)));

      mockServerClient.verify(request(), exactly(4));

      verifyRequestWithPageQueryParams(
          mockServerClient,
          SECRETS_SCAN_ALERTS_URL,
          "",
          Parameter.param("state", "open"),
          Parameter.param("page", "1"),
          Parameter.param("per_page", "100"));
      verifyRequest(mockServerClient, request().withPath(notificationWebHookUrl), exactly(3));
    }

    @Test
    @SneakyThrows
    void shouldFetchSecurityAlertsUsingAccessTokenFromCookie() {
      stubSecretsScanApiRequests(mockServerClient);

      final String dummyAccessToken = "dummy_access_token";

      invokeGetApiAndValidate(
          mockMvc,
          SECURITY_SCAN_ALERTS_ENDPOINT,
          APPLICATION_JSON_VALUE,
          content().json(readFile(SECURITY_SCAN_ALERTS_JSON)),
          dummyAccessToken);

      mockServerClient.verify(request(), exactly(4));

      verifyRequestWithPageQueryParams(
          mockServerClient,
          SECRETS_SCAN_ALERTS_URL,
          "",
          Parameter.param("state", "open"),
          Parameter.param("page", "1"),
          Parameter.param("per_page", "100"));
      verifyRequest(mockServerClient, request().withPath(notificationWebHookUrl), exactly(3));
    }

    @Test
    @SneakyThrows
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    void shouldNotThrowErrorIfWorkflowsApiCallFails() {

      mockRequest(
          mockServerClient,
          request().withMethod("GET").withPath(SECRETS_SCAN_ALERTS_URL),
          SC_NOT_FOUND,
          "Not found",
          APPLICATION_JSON_VALUE);

      invokeGetApiAndValidate(
          mockMvc, SECURITY_SCAN_ALERTS_ENDPOINT, APPLICATION_JSON_VALUE, content().json("[]"));
    }
  }

  @Nested
  @AutoConfigureMockMvc
  @MockServerTest
  @SpringBootTest(
      webEnvironment = RANDOM_PORT,
      properties = {
        "spring.cache.cache-names=securityScanAlerts,exposedSecrets",
        "spring.cache.type=caffeine"
      })
  class WithCacheSecurityScanAlerts {
    @SuppressFBWarnings("UWF_UNWRITTEN_FIELD")
    private MockServerClient mockServerClient;

    @Autowired private MockMvc mockMvc;

    @Value("${wiremock.webhook.url}")
    private String notificationWebHookUrl;

    @Autowired private CacheManager cacheManager;

    @Autowired private NotificationRepository notificationRepository;

    @BeforeEach
    void setUp() {
      stubSecretsScanApiRequests(mockServerClient);
    }

    @AfterEach
    void tearDown() {
      notificationRepository.deleteAll();
      cacheManager.getCacheNames().stream()
          .map(cacheManager::getCache)
          .filter(Objects::nonNull)
          .forEach(org.springframework.cache.Cache::clear);
      mockServerClient.reset();
    }

    @Test
    @SneakyThrows
    void shouldFetchSecurityScanAlerts() {
      final ResultMatcher resultMatcher = content().json(readFile(SECURITY_SCAN_ALERTS_JSON));

      invokeGetApiAndValidate(
          mockMvc, SECURITY_SCAN_ALERTS_ENDPOINT, APPLICATION_JSON_VALUE, resultMatcher);

      mockServerClient.verify(request(), exactly(4));
      verifyRequest(mockServerClient, SECRETS_SCAN_ALERTS_URL, "");
      verifyRequest(mockServerClient, request().withPath(notificationWebHookUrl), exactly(3));
      mockServerClient.reset();

      invokeGetApiAndValidate(
          mockMvc, SECURITY_SCAN_ALERTS_ENDPOINT, APPLICATION_JSON_VALUE, resultMatcher);
      mockServerClient.verifyZeroInteractions();
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

      mockServerClient.verify(request(), exactly(4));
      verifyRequest(mockServerClient, SECRETS_SCAN_ALERTS_URL, dummyToken1);
      verifyRequest(mockServerClient, request().withPath(notificationWebHookUrl), exactly(3));

      mockServerClient.reset();

      invokeGetApiAndValidate(
          mockMvc,
          SECURITY_SCAN_ALERTS_ENDPOINT,
          APPLICATION_JSON_VALUE,
          resultMatcher,
          "dummyToken2");
      mockServerClient.verifyZeroInteractions();
    }
  }

  @Nested
  @AutoConfigureMockMvc
  @MockServerTest
  @SpringBootTest(webEnvironment = RANDOM_PORT)
  class WithoutCacheForCodeScanAlerts {

    @Autowired private MockMvc mockMvc;
    @Autowired private NotificationRepository notificationRepository;

    @SuppressFBWarnings("UWF_UNWRITTEN_FIELD")
    private MockServerClient mockServerClient;

    @Value("${wiremock.webhook.url}")
    private String notificationWebHookUrl;

    @AfterEach
    void tearDown() {
      notificationRepository.deleteAll();
    }

    @Test
    @SneakyThrows
    void shouldFetchCodeStandardViolationAlerts() {
      stubCodeScanApiRequests(mockServerClient);

      invokeGetApiAndValidate(
          mockMvc,
          CODE_STANDARD_VIOLATION_ALERTS_ENDPOINT,
          APPLICATION_JSON_VALUE,
          content().json(readFile(CODE_STANDARD_VIOLATIONS_JSON)));

      mockServerClient.verify(request(), exactly(3));
      verifyRequest(mockServerClient, CODE_SCAN_ALERTS_URL, "");
      verifyRequest(mockServerClient, request().withPath(notificationWebHookUrl), exactly(2));
    }

    @Test
    @SneakyThrows
    void shouldFetchCodeStandardViolationAlertsUsingAccessTokenFromCookie() {
      stubCodeScanApiRequests(mockServerClient);

      final String dummyAccessToken = "dummy_access_token";

      invokeGetApiAndValidate(
          mockMvc,
          CODE_STANDARD_VIOLATION_ALERTS_ENDPOINT,
          APPLICATION_JSON_VALUE,
          content().json(readFile(CODE_STANDARD_VIOLATIONS_JSON)),
          dummyAccessToken);

      mockServerClient.verify(request(), exactly(3));
      verifyRequest(mockServerClient, CODE_SCAN_ALERTS_URL, dummyAccessToken);
      verifyRequest(mockServerClient, request().withPath(notificationWebHookUrl), exactly(2));
    }

    @Test
    @SneakyThrows
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    void shouldNotThrowErrorIfCodeScanApiCallFails() {

      mockRequest(
          mockServerClient,
          request().withMethod("GET").withPath(CODE_SCAN_ALERTS_URL),
          SC_NOT_FOUND,
          "Not found",
          APPLICATION_JSON_VALUE);

      invokeGetApiAndValidate(
          mockMvc,
          CODE_STANDARD_VIOLATION_ALERTS_ENDPOINT,
          APPLICATION_JSON_VALUE,
          content().json("[]"));

      mockServerClient.verify(request().withPath(CODE_SCAN_ALERTS_URL), once());
    }
  }

  @Nested
  @AutoConfigureMockMvc
  @MockServerTest
  @SpringBootTest(
      webEnvironment = RANDOM_PORT,
      properties = {
        "spring.cache.cache-names=codeScanAlerts,codeStandardViolations",
        "spring.cache.type=caffeine"
      })
  class WithCacheCodeScanAlerts {
    @SuppressFBWarnings("UWF_UNWRITTEN_FIELD")
    private MockServerClient mockServerClient;

    @Autowired private MockMvc mockMvc;

    @Value("${wiremock.webhook.url}")
    private String notificationWebHookUrl;

    @Autowired private CacheManager cacheManager;

    @Autowired private NotificationRepository notificationRepository;

    @BeforeEach
    void setUp() {
      stubCodeScanApiRequests(mockServerClient);
    }

    @AfterEach
    void tearDown() {
      notificationRepository.deleteAll();
      cacheManager.getCacheNames().stream()
          .map(cacheManager::getCache)
          .filter(Objects::nonNull)
          .forEach(org.springframework.cache.Cache::clear);
      mockServerClient.reset();
    }

    @Test
    @SneakyThrows
    void shouldFetchCodeStandardViolationAlerts() {
      final ResultMatcher resultMatcher = content().json(readFile(CODE_STANDARD_VIOLATIONS_JSON));

      invokeGetApiAndValidate(
          mockMvc, CODE_STANDARD_VIOLATION_ALERTS_ENDPOINT, APPLICATION_JSON_VALUE, resultMatcher);

      mockServerClient.verify(request(), exactly(3));
      verifyRequest(mockServerClient, CODE_SCAN_ALERTS_URL, "");
      verifyRequest(mockServerClient, request().withPath(notificationWebHookUrl), exactly(2));
      mockServerClient.reset();

      invokeGetApiAndValidate(
          mockMvc, CODE_STANDARD_VIOLATION_ALERTS_ENDPOINT, APPLICATION_JSON_VALUE, resultMatcher);
      mockServerClient.verifyZeroInteractions();
    }

    @Test
    @SneakyThrows
    void shouldShareCacheWithOtherUsersWhileFetchingCodeStandardViolationAlerts() {
      final ResultMatcher resultMatcher = content().json(readFile(CODE_STANDARD_VIOLATIONS_JSON));

      final String dummyToken1 = "dummyToken1";

      invokeGetApiAndValidate(
          mockMvc,
          CODE_STANDARD_VIOLATION_ALERTS_ENDPOINT,
          APPLICATION_JSON_VALUE,
          resultMatcher,
          dummyToken1);

      mockServerClient.verify(request(), exactly(3));
      verifyRequest(mockServerClient, CODE_SCAN_ALERTS_URL, dummyToken1);
      verifyRequest(mockServerClient, request().withPath(notificationWebHookUrl), exactly(2));
      mockServerClient.reset();

      invokeGetApiAndValidate(
          mockMvc,
          CODE_STANDARD_VIOLATION_ALERTS_ENDPOINT,
          APPLICATION_JSON_VALUE,
          resultMatcher,
          "dummyToken2");
      mockServerClient.verifyZeroInteractions();
    }
  }
}
