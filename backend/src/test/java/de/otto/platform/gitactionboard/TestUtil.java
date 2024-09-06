package de.otto.platform.gitactionboard;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.http.HttpStatus.SC_OK;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.mockserver.verify.VerificationTimes.once;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import de.otto.platform.gitactionboard.adapters.repository.workflow.WorkflowRecord;
import de.otto.platform.gitactionboard.adapters.repository.workflow.job.WorkflowJobRecord;
import de.otto.platform.gitactionboard.adapters.repository.workflow.run.WorkflowRunRecord;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import lombok.SneakyThrows;
import org.mockserver.client.MockServerClient;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.Parameter;
import org.mockserver.verify.VerificationTimes;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

@SuppressWarnings("PMD.TestClassWithoutTestCases")
public class TestUtil {

  @SneakyThrows
  public static String readFile(String fileName) {
    return Files.readString(Path.of("./src/test/resources/%s".formatted(fileName)), UTF_8);
  }

  @SneakyThrows
  public static void invokeGetApiAndValidate(
      MockMvc mockMvc, String endPoint, String expectedContentType, ResultMatcher resultMatcher) {
    invokeGetApiAndValidate(mockMvc, endPoint, expectedContentType, resultMatcher, null);
  }

  @SneakyThrows
  public static void invokeGetApiAndValidate(
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

  public static void verifyRequest(
      MockServerClient mockServerClient, String url, String authToken) {
    verifyRequest(mockServerClient, url, authToken, once());
  }

  public static void verifyRequest(
      MockServerClient mockServerClient,
      String url,
      String authToken,
      VerificationTimes verificationTimes) {
    verifyRequest(
        mockServerClient,
        request().withPath(url).withHeader(AUTHORIZATION, authToken),
        verificationTimes);
  }

  public static void verifyRequest(
      MockServerClient mockServerClient,
      HttpRequest requestDefinition,
      VerificationTimes verificationTimes) {
    mockServerClient.verify(requestDefinition, verificationTimes);
  }

  public static void verifyRequestWithPageQueryParams(
      MockServerClient mockServerClient, String url, String authToken, Parameter... queryParams) {
    verifyRequest(
        mockServerClient,
        request()
            .withPath(url)
            .withQueryStringParameters(queryParams)
            .withHeader(AUTHORIZATION, authToken),
        once());
  }

  public static void mockRequest(
      MockServerClient mockServerClient, HttpRequest requestDefinition, String responseBody) {
    mockRequest(mockServerClient, requestDefinition, SC_OK, responseBody, APPLICATION_JSON_VALUE);
  }

  public static void mockRequest(
      MockServerClient mockServerClient,
      HttpRequest requestDefinition,
      int statusCode,
      String responseBody,
      String contentType) {
    mockServerClient
        .when(requestDefinition)
        .respond(
            response()
                .withStatusCode(statusCode)
                .withHeader(CONTENT_TYPE, contentType)
                .withBody(responseBody));
  }

  public static void mockGetRequest(
      MockServerClient mockServerClient, String url, String responseBody) {
    mockRequest(mockServerClient, request().withMethod("GET").withPath(url), responseBody);
  }

  public static void mockGetRequestWithPageQueryParam(
      MockServerClient mockServerClient, String url, String responseBody, String... queryParams) {

    mockRequest(
        mockServerClient,
        request()
            .withMethod("GET")
            .withPath(url)
            .withQueryStringParameters(Arrays.stream(queryParams).map(Parameter::param).toList()),
        responseBody);
  }

  public static void saveWorkflowRecords(
      JdbcTemplate jdbcTemplate, WorkflowRecord... workflowRecords) {
    saveWorkflowRecords(jdbcTemplate, Arrays.asList(workflowRecords));
  }

  public static void saveWorkflowRecords(
      JdbcTemplate jdbcTemplate, List<WorkflowRecord> workflowRecords) {
    jdbcTemplate.batchUpdate(
        "insert into workflows (id, name, repo_name) values (?, ?, ?)",
        workflowRecords,
        10,
        (ps, workflow) -> {
          ps.setLong(1, workflow.getId());
          ps.setString(2, workflow.getName());
          ps.setString(3, workflow.getRepoName());
        });
  }

  public static void persistWorkflowRunRecords(
      JdbcTemplate jdbcTemplate, WorkflowRunRecord... workflowRuns) {
    persistWorkflowRunRecords(jdbcTemplate, Arrays.asList(workflowRuns));
  }

  public static void persistWorkflowRunRecords(
      JdbcTemplate jdbcTemplate, List<WorkflowRunRecord> workflowRuns) {
    jdbcTemplate.batchUpdate(
        "insert or replace into workflow_runs (id, run_attempt, workflow_id, status, conclusion, run_number, updated_at, created_at, triggered_event) values (?, ?, ?,?, ?, ?,?, ?, ?)",
        workflowRuns,
        10,
        (ps, workflowRun) -> {
          ps.setLong(1, workflowRun.getId());
          ps.setLong(2, workflowRun.getRunAttempt());
          ps.setLong(3, workflowRun.getWorkflowId());
          ps.setString(4, workflowRun.getStatus());
          ps.setString(5, workflowRun.getConclusion());
          ps.setLong(6, workflowRun.getRunNumber());
          ps.setString(7, workflowRun.getUpdatedAt().toString());
          ps.setString(8, workflowRun.getCreatedAt().toString());
          ps.setString(9, workflowRun.getTriggeredEvent());
        });
  }

  public static void persistWorkflowJobRecords(
      JdbcTemplate jdbcTemplate, WorkflowJobRecord... workflowJobRecords) {
    persistWorkflowJobRecords(jdbcTemplate, Arrays.asList(workflowJobRecords));
  }

  public static void persistWorkflowJobRecords(
      JdbcTemplate jdbcTemplate, List<WorkflowJobRecord> workflowJobRecords) {
    jdbcTemplate.batchUpdate(
        "insert or replace into workflow_jobs (id, name, status, conclusion, started_at, completed_at, workflow_id,workflow_run_id, run_attempt, url) values (?, ?, ?,?, ?, ?,?, ?, ?, ?)",
        workflowJobRecords,
        10,
        (ps, workflowJob) -> {
          ps.setLong(1, workflowJob.getId());
          ps.setString(2, workflowJob.getName());
          ps.setString(3, workflowJob.getStatus());
          ps.setString(4, workflowJob.getConclusion());
          ps.setString(5, workflowJob.getStartedAt().toString());
          ps.setString(6, workflowJob.getCompletedAt().toString());
          ps.setLong(7, workflowJob.getWorkflowId());
          ps.setLong(8, workflowJob.getWorkflowRunId());
          ps.setLong(9, workflowJob.getRunAttempt());
          ps.setString(10, workflowJob.getUrl());
        });
  }
}
