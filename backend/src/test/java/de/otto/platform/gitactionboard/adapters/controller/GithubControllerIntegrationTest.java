package de.otto.platform.gitactionboard.adapters.controller;

import static de.otto.platform.gitactionboard.TestUtil.invokeGetApiAndValidate;
import static de.otto.platform.gitactionboard.TestUtil.mockGetRequest;
import static de.otto.platform.gitactionboard.TestUtil.mockGetRequestWithPageQueryParam;
import static de.otto.platform.gitactionboard.TestUtil.readFile;
import static de.otto.platform.gitactionboard.TestUtil.verifyRequest;
import static jakarta.servlet.http.HttpServletResponse.SC_NOT_FOUND;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.verify.VerificationTimes.exactly;
import static org.mockserver.verify.VerificationTimes.once;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import com.github.benmanes.caffeine.cache.Cache;
import de.otto.platform.gitactionboard.IntegrationTest;
import de.otto.platform.gitactionboard.TestUtil;
import de.otto.platform.gitactionboard.adapters.service.job.WorkflowsJobDetailsResponse.WorkflowsJobDetails;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import java.util.Objects;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.Parameter;
import org.mockserver.springtest.MockServerTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

@DirtiesContext
@IntegrationTest
@SuppressFBWarnings("NP_UNWRITTEN_FIELD")
class GithubControllerIntegrationTest {
  private static final String API_BASE_PATH = "/repos/johndoe/hello-world";

  private static final String WORKFLOWS_URL = String.format("%s/actions/workflows", API_BASE_PATH);
  private static final String RUNS_URL_1 =
      String.format("%s/actions/workflows/2151835/runs", API_BASE_PATH);
  private static final String RUN_URL_2 =
      String.format("%s/actions/workflows/2057656/runs", API_BASE_PATH);
  private static final String JOBS_URL_1 =
      String.format("%s/actions/runs/%s/jobs", API_BASE_PATH, 260614021);
  private static final String JOBS_URL_2 =
      String.format("%s/actions/runs/%s/jobs", API_BASE_PATH, 260614024);
  private static final String CCTRAY_XML_ENDPOINT = "/v1/cctray.xml";
  private static final String APPLICATION_XML_CONTENT_TYPE = "application/xml;charset=UTF-8";
  private static final String CCTRAY_ENDPOINT = "/v1/cctray";
  private static final String CCTRAY_XML_FILE_NAME = "testData/cctray.xml";
  private static final String CCTRAY_JSON_FILE_NAME = "testData/cctray.json";

  private void verifyMockServerRequest(MockServerClient mockServerClient, String authToken) {
    final Parameter perPageParameter = Parameter.param("per_page", "2");
    mockServerClient.verify(request(), exactly(5));
    verifyRequest(mockServerClient, WORKFLOWS_URL, authToken);
    TestUtil.verifyRequestWithPageQueryParams(
        mockServerClient, RUNS_URL_1, authToken, perPageParameter);
    TestUtil.verifyRequestWithPageQueryParams(
        mockServerClient, RUN_URL_2, authToken, perPageParameter);
    verifyRequest(mockServerClient, JOBS_URL_1, authToken);
    verifyRequest(mockServerClient, JOBS_URL_2, authToken);
  }

  private void stubWorkflowApiRequests(MockServerClient mockServerClient) {
    mockGetRequest(mockServerClient, WORKFLOWS_URL, readFile("testData/workflows.json"));

    mockGetRequest(mockServerClient, JOBS_URL_1, readFile("testData/jobsDetails260614021.json"));

    mockGetRequest(mockServerClient, JOBS_URL_2, readFile("testData/jobsDetails260614024.json"));

    mockGetRequestWithPageQueryParam(
        mockServerClient, RUNS_URL_1, readFile("testData/workflowRuns2151835.json"), "per_page");

    mockGetRequestWithPageQueryParam(
        mockServerClient, RUN_URL_2, readFile("testData/workflowRuns2057656.json"), "per_page");
  }

  @Nested
  @AutoConfigureMockMvc
  @SpringBootTest(
      webEnvironment = RANDOM_PORT,
      properties = {
        "spring.cache.cache-names=cctray,cctrayXml,jobDetails",
        "spring.cache.type=caffeine"
      })
  @MockServerTest
  class WithCache {
    @Autowired private MockMvc mockMvc;

    @Autowired private Cache<String, List<WorkflowsJobDetails>> workflowJobDetailsCache;

    @Autowired private CacheManager cacheManager;

    @SuppressFBWarnings("UWF_UNWRITTEN_FIELD")
    private MockServerClient mockServerClient;

    @BeforeEach
    void setUp() {
      stubWorkflowApiRequests(mockServerClient);
    }

    @AfterEach
    void tearDown() {
      workflowJobDetailsCache.invalidateAll();
      cacheManager.getCacheNames().stream()
          .map(cacheManager::getCache)
          .filter(Objects::nonNull)
          .forEach(org.springframework.cache.Cache::clear);
      mockServerClient.reset();
    }

    @Test
    @SneakyThrows
    void shouldFetchCctrayXml() {
      final ResultMatcher resultMatcher = content().xml(readFile(CCTRAY_XML_FILE_NAME));

      invokeGetApiAndValidate(
          mockMvc, CCTRAY_XML_ENDPOINT, APPLICATION_XML_CONTENT_TYPE, resultMatcher);

      verifyMockServerRequest(mockServerClient, "");
      mockServerClient.reset();

      invokeGetApiAndValidate(
          mockMvc, CCTRAY_XML_ENDPOINT, APPLICATION_XML_CONTENT_TYPE, resultMatcher);

      mockServerClient.verifyZeroInteractions();
    }

    @Test
    @SneakyThrows
    void shouldShareCacheWithOtherUsersWhileFetchingCctrayXml() {
      final ResultMatcher resultMatcher = content().xml(readFile(CCTRAY_XML_FILE_NAME));
      final String dummyToken1 = "dummyToken1";

      invokeGetApiAndValidate(
          mockMvc, CCTRAY_XML_ENDPOINT, APPLICATION_XML_CONTENT_TYPE, resultMatcher, dummyToken1);

      verifyMockServerRequest(mockServerClient, dummyToken1);
      mockServerClient.reset();

      invokeGetApiAndValidate(
          mockMvc, CCTRAY_XML_ENDPOINT, APPLICATION_XML_CONTENT_TYPE, resultMatcher, "dummyToken2");

      mockServerClient.verifyZeroInteractions();
    }

    @Test
    @SneakyThrows
    void shouldFetchCctrayJson() {
      final ResultMatcher resultMatcher = content().json(readFile(CCTRAY_JSON_FILE_NAME), true);

      invokeGetApiAndValidate(mockMvc, CCTRAY_ENDPOINT, APPLICATION_JSON_VALUE, resultMatcher);

      verifyMockServerRequest(mockServerClient, "");
      mockServerClient.reset();

      invokeGetApiAndValidate(mockMvc, CCTRAY_ENDPOINT, APPLICATION_JSON_VALUE, resultMatcher);

      mockServerClient.verifyZeroInteractions();
    }

    @Test
    void shouldShareCacheWithOtherUsersWhileFetchingCctrayJson() {
      final ResultMatcher resultMatcher = content().json(readFile(CCTRAY_JSON_FILE_NAME), true);

      final String dummyToken1 = "dummyToken1";
      invokeGetApiAndValidate(
          mockMvc, CCTRAY_ENDPOINT, APPLICATION_JSON_VALUE, resultMatcher, dummyToken1);

      verifyMockServerRequest(mockServerClient, dummyToken1);
      mockServerClient.reset();

      invokeGetApiAndValidate(
          mockMvc, CCTRAY_ENDPOINT, APPLICATION_JSON_VALUE, resultMatcher, "dummyToken2");

      mockServerClient.verifyZeroInteractions();
    }
  }

  @Nested
  @AutoConfigureMockMvc
  @MockServerTest
  @SpringBootTest(webEnvironment = RANDOM_PORT)
  class WithoutCache {
    @SuppressFBWarnings("UWF_UNWRITTEN_FIELD")
    private MockServerClient mockServerClient;

    @Autowired private MockMvc mockMvc;

    @Autowired private Cache<String, List<WorkflowsJobDetails>> workflowJobDetailsCache;

    @AfterEach
    void tearDown() {
      workflowJobDetailsCache.invalidateAll();
      mockServerClient.reset();
    }

    @Test
    @SneakyThrows
    void shouldFetchCctrayXml() {
      stubWorkflowApiRequests(mockServerClient);

      invokeGetApiAndValidate(
          mockMvc,
          CCTRAY_XML_ENDPOINT,
          APPLICATION_XML_CONTENT_TYPE,
          content().xml(readFile(CCTRAY_XML_FILE_NAME)));

      verifyMockServerRequest(mockServerClient, "");
    }

    @Test
    @SneakyThrows
    void shouldFetchCctrayJson() {
      stubWorkflowApiRequests(mockServerClient);

      final ResultMatcher resultMatcher = content().json(readFile(CCTRAY_JSON_FILE_NAME), true);

      invokeGetApiAndValidate(mockMvc, CCTRAY_ENDPOINT, APPLICATION_JSON_VALUE, resultMatcher);

      verifyMockServerRequest(mockServerClient, "");
    }

    @Test
    @SneakyThrows
    void shouldFetchCctrayXmlUsingAccessTokenFromCookie() {
      stubWorkflowApiRequests(mockServerClient);

      final String dummyAccessToken = "dummy_access_token";

      invokeGetApiAndValidate(
          mockMvc,
          CCTRAY_XML_ENDPOINT,
          APPLICATION_XML_CONTENT_TYPE,
          content().xml(readFile(CCTRAY_XML_FILE_NAME)),
          dummyAccessToken);

      verifyMockServerRequest(mockServerClient, dummyAccessToken);
    }

    @Test
    @SneakyThrows
    void shouldFetchCctrayJsonUsingAccessTokenFromCookie() {
      stubWorkflowApiRequests(mockServerClient);

      final ResultMatcher resultMatcher = content().json(readFile(CCTRAY_JSON_FILE_NAME), true);

      final String dummyAccessToken = "dummy_access_token";

      invokeGetApiAndValidate(
          mockMvc, CCTRAY_ENDPOINT, APPLICATION_JSON_VALUE, resultMatcher, dummyAccessToken);

      verifyMockServerRequest(mockServerClient, "");
    }

    @Test
    @SneakyThrows
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    void shouldNotThrowErrorIfWorkflowsApiCallFails() {
      mockServerClient
          .when(request().withMethod("GET").withPath(WORKFLOWS_URL))
          .respond(
              response()
                  .withStatusCode(SC_NOT_FOUND)
                  .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE));

      invokeGetApiAndValidate(
          mockMvc, CCTRAY_XML_ENDPOINT, APPLICATION_XML_CONTENT_TYPE, content().xml("<Projects/>"));

      mockServerClient.verify(request(), once());
    }
  }
}
