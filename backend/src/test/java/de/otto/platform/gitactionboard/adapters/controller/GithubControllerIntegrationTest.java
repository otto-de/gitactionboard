package de.otto.platform.gitactionboard.adapters.controller;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static de.otto.platform.gitactionboard.TestUtil.readFile;
import static org.apache.http.HttpStatus.SC_NOT_FOUND;
import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import de.otto.platform.gitactionboard.IntegrationTest;
import de.otto.platform.gitactionboard.WireMockExtension;
import de.otto.platform.gitactionboard.adapters.service.job.WorkflowsJobDetailsResponse.WorkflowsJobDetails;
import java.util.List;
import java.util.Objects;
import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@DirtiesContext
@IntegrationTest
class GithubControllerIntegrationTest {
  private static final String API_BASE_PATH = "/repos/johndoe/hello-world";

  private static final String WORKFLOWS_URL = String.format("%s/actions/workflows", API_BASE_PATH);
  private static final String RUNS_URL_1 =
      String.format("%s/actions/workflows/2151835/runs?per_page=2", API_BASE_PATH);
  private static final String RUN_URL_2 =
      String.format("%s/actions/workflows/2057656/runs?per_page=2", API_BASE_PATH);
  private static final String JOBS_URL_1 =
      String.format("%s/actions/runs/%s/jobs", API_BASE_PATH, 260614021);
  private static final String JOBS_URL_2 =
      String.format("%s/actions/runs/%s/jobs", API_BASE_PATH, 260614024);
  private static final String CCTRAY_XML_ENDPOINT = "/v1/cctray.xml";
  private static final String APPLICATION_XML_CONTENT_TYPE = "application/xml;charset=UTF-8";
  private static final String CCTRAY_ENDPOINT = "/v1/cctray";
  private static final String CCTRAY_XML_FILE_NAME = "testData/cctray.xml";
  private static final String CCTRAY_JSON_FILE_NAME = "testData/cctray.json";

  private void stubApiRequests() {
    stubFor(
        WireMock.get(WORKFLOWS_URL)
            .willReturn(
                aResponse()
                    .withStatus(SC_OK)
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .withBody(readFile("testData/workflows.json"))));

    stubFor(
        WireMock.get(RUNS_URL_1)
            .willReturn(
                aResponse()
                    .withStatus(SC_OK)
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .withBody(readFile("testData/workflowRuns2151835.json"))));

    stubFor(
        WireMock.get(RUN_URL_2)
            .willReturn(
                aResponse()
                    .withStatus(SC_OK)
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .withBody(readFile("testData/workflowRuns2057656.json"))));

    stubFor(
        WireMock.get(JOBS_URL_1)
            .willReturn(
                aResponse()
                    .withStatus(SC_OK)
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .withBody(readFile("testData/jobsDetails260614021.json"))));

    stubFor(
        WireMock.get(JOBS_URL_2)
            .willReturn(
                aResponse()
                    .withStatus(SC_OK)
                    .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                    .withBody(readFile("testData/jobsDetails260614024.json"))));
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
      properties = {
        "spring.cache.cache-names=cctray,cctrayXml,jobDetails",
        "spring.cache.type=caffeine"
      })
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class WithCacheCctrayXml {
    @Autowired private MockMvc mockMvc;

    @Autowired private Cache<String, List<WorkflowsJobDetails>> workflowJobDetailsCache;

    @BeforeEach
    void setUp() {
      stubApiRequests();
    }

    @AfterEach
    void tearDown() {
      workflowJobDetailsCache.invalidateAll();
    }

    @Test
    @Order(value = 0)
    @SneakyThrows
    void shouldFetchCctrayXml() {
      final ResultMatcher resultMatcher = content().xml(readFile(CCTRAY_XML_FILE_NAME));

      invokeGetApiAndValidate(
          mockMvc, CCTRAY_XML_ENDPOINT, APPLICATION_XML_CONTENT_TYPE, resultMatcher);

      invokeGetApiAndValidate(
          mockMvc, CCTRAY_XML_ENDPOINT, APPLICATION_XML_CONTENT_TYPE, resultMatcher);

      assertThat(WireMock.getAllServeEvents()).hasSize(5);
      final EqualToPattern valuePattern = new EqualToPattern("");
      verify(getRequestedFor(urlEqualTo(WORKFLOWS_URL)).withHeader(AUTHORIZATION, valuePattern));
      verify(getRequestedFor(urlEqualTo(RUNS_URL_1)).withHeader(AUTHORIZATION, valuePattern));
      verify(getRequestedFor(urlEqualTo(RUN_URL_2)).withHeader(AUTHORIZATION, valuePattern));
      verify(getRequestedFor(urlEqualTo(JOBS_URL_1)).withHeader(AUTHORIZATION, valuePattern));
      verify(getRequestedFor(urlEqualTo(JOBS_URL_2)).withHeader(AUTHORIZATION, valuePattern));
    }

    @Test
    @Order(value = 1)
    @SneakyThrows
    void shouldFetchCctrayJsonWithoutCallingApis() {
      final ResultMatcher resultMatcher = content().json(readFile(CCTRAY_JSON_FILE_NAME));

      invokeGetApiAndValidate(mockMvc, CCTRAY_ENDPOINT, APPLICATION_JSON_VALUE, resultMatcher);

      invokeGetApiAndValidate(mockMvc, CCTRAY_ENDPOINT, APPLICATION_JSON_VALUE, resultMatcher);

      assertThat(WireMock.getAllServeEvents()).isEmpty();
    }
  }

  @Nested
  @AutoConfigureMockMvc
  @ExtendWith(WireMockExtension.class)
  @SpringBootTest(
      webEnvironment = RANDOM_PORT,
      properties = {
        "spring.cache.cache-names=cctray,cctrayXml,jobDetails",
        "spring.cache.type=caffeine"
      })
  @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
  class WithCacheCctrayJson {
    @Autowired private MockMvc mockMvc;

    @Autowired private Cache<String, List<WorkflowsJobDetails>> workflowJobDetailsCache;

    @BeforeEach
    void setUp() {
      stubApiRequests();
    }

    @AfterEach
    void tearDown() {
      workflowJobDetailsCache.invalidateAll();
    }

    @Test
    @Order(value = 1)
    @SneakyThrows
    void shouldFetchCctrayXmlWithoutCallingApis() {
      final ResultMatcher resultMatcher = content().xml(readFile(CCTRAY_XML_FILE_NAME));

      invokeGetApiAndValidate(
          mockMvc, CCTRAY_XML_ENDPOINT, APPLICATION_XML_CONTENT_TYPE, resultMatcher);

      invokeGetApiAndValidate(
          mockMvc, CCTRAY_XML_ENDPOINT, APPLICATION_XML_CONTENT_TYPE, resultMatcher);

      assertThat(WireMock.getAllServeEvents()).isEmpty();
    }

    @Test
    @Order(value = 0)
    @SneakyThrows
    void shouldFetchCctrayJson() {
      final ResultMatcher resultMatcher = content().json(readFile(CCTRAY_JSON_FILE_NAME));

      invokeGetApiAndValidate(mockMvc, CCTRAY_ENDPOINT, APPLICATION_JSON_VALUE, resultMatcher);

      invokeGetApiAndValidate(mockMvc, CCTRAY_ENDPOINT, APPLICATION_JSON_VALUE, resultMatcher);

      assertThat(WireMock.getAllServeEvents()).hasSize(5);
      final EqualToPattern valuePattern = new EqualToPattern("");
      verify(getRequestedFor(urlEqualTo(WORKFLOWS_URL)).withHeader(AUTHORIZATION, valuePattern));
      verify(getRequestedFor(urlEqualTo(RUNS_URL_1)).withHeader(AUTHORIZATION, valuePattern));
      verify(getRequestedFor(urlEqualTo(RUN_URL_2)).withHeader(AUTHORIZATION, valuePattern));
      verify(getRequestedFor(urlEqualTo(JOBS_URL_1)).withHeader(AUTHORIZATION, valuePattern));
      verify(getRequestedFor(urlEqualTo(JOBS_URL_2)).withHeader(AUTHORIZATION, valuePattern));
    }
  }

  @Nested
  @AutoConfigureMockMvc
  @ExtendWith(WireMockExtension.class)
  @SpringBootTest(webEnvironment = RANDOM_PORT)
  class WithoutCache {

    @Autowired private MockMvc mockMvc;

    @Autowired private Cache<String, List<WorkflowsJobDetails>> workflowJobDetailsCache;

    @AfterEach
    void tearDown() {
      workflowJobDetailsCache.invalidateAll();
    }

    @Test
    @SneakyThrows
    void shouldFetchCctrayXml() {
      stubApiRequests();

      invokeGetApiAndValidate(
          mockMvc,
          CCTRAY_XML_ENDPOINT,
          APPLICATION_XML_CONTENT_TYPE,
          content().xml(readFile(CCTRAY_XML_FILE_NAME)));

      assertThat(WireMock.getAllServeEvents()).hasSize(5);
      final EqualToPattern valuePattern = new EqualToPattern("");
      verify(getRequestedFor(urlEqualTo(WORKFLOWS_URL)).withHeader(AUTHORIZATION, valuePattern));
      verify(getRequestedFor(urlEqualTo(RUNS_URL_1)).withHeader(AUTHORIZATION, valuePattern));
      verify(getRequestedFor(urlEqualTo(RUN_URL_2)).withHeader(AUTHORIZATION, valuePattern));
      verify(getRequestedFor(urlEqualTo(JOBS_URL_1)).withHeader(AUTHORIZATION, valuePattern));
      verify(getRequestedFor(urlEqualTo(JOBS_URL_2)).withHeader(AUTHORIZATION, valuePattern));
    }

    @Test
    @SneakyThrows
    void shouldFetchCctrayJson() {
      stubApiRequests();

      final ResultMatcher resultMatcher = content().json(readFile(CCTRAY_JSON_FILE_NAME));

      invokeGetApiAndValidate(mockMvc, CCTRAY_ENDPOINT, APPLICATION_JSON_VALUE, resultMatcher);

      assertThat(WireMock.getAllServeEvents()).hasSize(5);
      final EqualToPattern valuePattern = new EqualToPattern("");
      verify(getRequestedFor(urlEqualTo(WORKFLOWS_URL)).withHeader(AUTHORIZATION, valuePattern));
      verify(getRequestedFor(urlEqualTo(RUNS_URL_1)).withHeader(AUTHORIZATION, valuePattern));
      verify(getRequestedFor(urlEqualTo(RUN_URL_2)).withHeader(AUTHORIZATION, valuePattern));
      verify(getRequestedFor(urlEqualTo(JOBS_URL_1)).withHeader(AUTHORIZATION, valuePattern));
      verify(getRequestedFor(urlEqualTo(JOBS_URL_2)).withHeader(AUTHORIZATION, valuePattern));
    }

    @Test
    @SneakyThrows
    void shouldFetchCctrayXmlUsingAccessTokenFromCookie() {
      stubApiRequests();

      final String dummyAccessToken = "dummy_access_token";

      invokeGetApiAndValidate(
          mockMvc,
          CCTRAY_XML_ENDPOINT,
          APPLICATION_XML_CONTENT_TYPE,
          content().xml(readFile(CCTRAY_XML_FILE_NAME)),
          dummyAccessToken);

      assertThat(WireMock.getAllServeEvents()).hasSize(5);
      final EqualToPattern valuePattern = new EqualToPattern(dummyAccessToken);
      verify(getRequestedFor(urlEqualTo(WORKFLOWS_URL)).withHeader(AUTHORIZATION, valuePattern));
      verify(getRequestedFor(urlEqualTo(RUNS_URL_1)).withHeader(AUTHORIZATION, valuePattern));
      verify(getRequestedFor(urlEqualTo(RUN_URL_2)).withHeader(AUTHORIZATION, valuePattern));
      verify(getRequestedFor(urlEqualTo(JOBS_URL_1)).withHeader(AUTHORIZATION, valuePattern));
      verify(getRequestedFor(urlEqualTo(JOBS_URL_2)).withHeader(AUTHORIZATION, valuePattern));
    }

    @Test
    @SneakyThrows
    void shouldFetchCctrayJsonUsingAccessTokenFromCookie() {
      stubApiRequests();

      final ResultMatcher resultMatcher = content().json(readFile(CCTRAY_JSON_FILE_NAME));

      final String dummyAccessToken = "dummy_access_token";

      invokeGetApiAndValidate(
          mockMvc, CCTRAY_ENDPOINT, APPLICATION_JSON_VALUE, resultMatcher, dummyAccessToken);

      assertThat(WireMock.getAllServeEvents()).hasSize(5);
      final EqualToPattern valuePattern = new EqualToPattern(dummyAccessToken);
      verify(getRequestedFor(urlEqualTo(WORKFLOWS_URL)).withHeader(AUTHORIZATION, valuePattern));
      verify(getRequestedFor(urlEqualTo(RUNS_URL_1)).withHeader(AUTHORIZATION, valuePattern));
      verify(getRequestedFor(urlEqualTo(RUN_URL_2)).withHeader(AUTHORIZATION, valuePattern));
      verify(getRequestedFor(urlEqualTo(JOBS_URL_1)).withHeader(AUTHORIZATION, valuePattern));
      verify(getRequestedFor(urlEqualTo(JOBS_URL_2)).withHeader(AUTHORIZATION, valuePattern));
    }

    @Test
    @SneakyThrows
    @SuppressWarnings("PMD.JUnitTestsShouldIncludeAssert")
    void shouldNotThrowErrorIfWorkflowsApiCallFails() {
      stubFor(
          WireMock.get(WORKFLOWS_URL)
              .willReturn(
                  aResponse()
                      .withStatus(SC_NOT_FOUND)
                      .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE)));

      invokeGetApiAndValidate(
          mockMvc,
          CCTRAY_XML_ENDPOINT,
          APPLICATION_XML_CONTENT_TYPE,
          content().xml("<Projects></Projects>"));
    }
  }

  @SneakyThrows
  private void invokeGetApiAndValidate(
      MockMvc mockMvc, String endPoint, String expectedContentType, ResultMatcher resultMatcher) {
    invokeGetApiAndValidate(mockMvc, endPoint, expectedContentType, resultMatcher, null);
  }

  @SneakyThrows
  private void invokeGetApiAndValidate(
      MockMvc mockMvc,
      String endPoint,
      String expectedContentType,
      ResultMatcher resultMatcher,
      String accessToken) {
    final MockHttpServletRequestBuilder requestBuilder =
        Objects.isNull(accessToken)
            ? get(endPoint)
            : get(endPoint).header(AUTHORIZATION, accessToken);
    mockMvc
        .perform(requestBuilder)
        .andExpect(status().isOk())
        .andExpect(header().stringValues(ACCESS_CONTROL_ALLOW_ORIGIN, "*"))
        .andExpect(header().stringValues(CONTENT_TYPE, expectedContentType))
        .andExpect(resultMatcher);
  }
}
