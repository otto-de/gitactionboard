package de.otto.platform.gitactionboard.adapters.service.job;

import static de.otto.platform.gitactionboard.adapters.service.job.RunConclusion.FAILURE;
import static de.otto.platform.gitactionboard.adapters.service.job.RunConclusion.SKIPPED;
import static de.otto.platform.gitactionboard.adapters.service.job.RunConclusion.SUCCESS;
import static de.otto.platform.gitactionboard.adapters.service.job.RunStatus.COMPLETED;
import static de.otto.platform.gitactionboard.adapters.service.job.RunStatus.IN_PROGRESS;
import static de.otto.platform.gitactionboard.adapters.service.job.RunStatus.QUEUED;
import static de.otto.platform.gitactionboard.domain.Activity.BUILDING;
import static de.otto.platform.gitactionboard.domain.Activity.SLEEPING;
import static de.otto.platform.gitactionboard.fixtures.JobDetailsFixture.getJobDetailsBuilder;
import static de.otto.platform.gitactionboard.fixtures.WorkflowsFixture.REPO_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import de.otto.platform.gitactionboard.TestUtil;
import de.otto.platform.gitactionboard.adapters.service.job.WorkflowsJobDetailsResponse.WorkflowsJobDetails;
import de.otto.platform.gitactionboard.adapters.service.job.WorkflowsRunDetailsResponse.WorkflowRunDetails;
import de.otto.platform.gitactionboard.config.CodecConfig;
import de.otto.platform.gitactionboard.domain.JobDetails;
import de.otto.platform.gitactionboard.domain.Status;
import de.otto.platform.gitactionboard.domain.Workflow;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
class GithubJobDetailsServiceTest {
  private static final String WORKFLOW_NAME = "hello-world-checks";
  private static final JobDetails JOB_DETAILS_1132386127 = getJobDetailsBuilder().build();

  private static final JobDetails JOB_DETAILS_1132386046 =
      getJobDetailsBuilder()
          .id(1132386046)
          .name("talisman-checks")
          .url("https://github.com/johndoe/hello-world/runs/1132386046")
          .lastBuildTime(Instant.parse("2020-09-18T06:11:41.000Z"))
          .build();

  private static final ObjectMapper objectMapper = CodecConfig.OBJECT_MAPPER_BUILDER.build();

  private final Workflow workflow =
      Workflow.builder().id(2151835).name(WORKFLOW_NAME).repoName(REPO_NAME).build();

  @Mock private RestTemplate restTemplate;

  @Mock private Cache<String, List<WorkflowsJobDetails>> mockWorkflowJobDetailsCache;

  private GithubJobDetailsService githubJobDetailsService;
  private static WorkflowsJobDetailsResponse BASE_JOB_DETAILS_RESPONSE;
  private static WorkflowsRunDetailsResponse BASE_WORKFLOWS_RUN_DETAILS_RESPONSE;
  private static int RUN_ID_1;

  @BeforeAll
  @SneakyThrows
  static void beforeAll() {
    BASE_WORKFLOWS_RUN_DETAILS_RESPONSE =
        objectMapper.readValue(
            TestUtil.readFile("testData/workflowRuns2151835.json"),
            WorkflowsRunDetailsResponse.class);

    BASE_JOB_DETAILS_RESPONSE =
        objectMapper.readValue(
            TestUtil.readFile("testData/jobsDetails260614021.json"),
            WorkflowsJobDetailsResponse.class);

    RUN_ID_1 = BASE_WORKFLOWS_RUN_DETAILS_RESPONSE.getWorkflowRuns().get(0).getId();
  }

  private static Stream<Arguments> getPermutationOfWorkflowRunStatusAndPreviousConclusion() {

    final List<RunConclusion> runConclusions =
        Arrays.stream(RunConclusion.values())
            .filter(conclusion -> !conclusion.equals(SUCCESS) && !conclusion.equals(SKIPPED))
            .collect(Collectors.toUnmodifiableList());

    return Stream.of(QUEUED, IN_PROGRESS)
        .flatMap(
            status -> runConclusions.stream().map(conclusion -> Arguments.of(status, conclusion)));
  }

  private static Stream<Arguments> getSuccessPermutationArguments() {
    return Stream.of(QUEUED, IN_PROGRESS)
        .flatMap(
            status ->
                Stream.of(SUCCESS, SKIPPED).map(conclusion -> Arguments.of(status, conclusion)));
  }

  @BeforeEach
  void setUp() {
    githubJobDetailsService =
        new GithubJobDetailsService(restTemplate, mockWorkflowJobDetailsCache);
  }

  @Test
  @SneakyThrows
  void shouldReturnEmptyListAsJobDetailsIfWorkflowNeverRan() {
    when(restTemplate.getForObject(
            String.format(
                "/%s/actions/workflows/%s/runs?per_page=2",
                workflow.getRepoName(), workflow.getId()),
            WorkflowsRunDetailsResponse.class))
        .thenReturn(
            WorkflowsRunDetailsResponse.builder().workflowRuns(Collections.emptyList()).build());

    final List<JobDetails> jobDetails = githubJobDetailsService.fetchJobDetails(workflow).get();
    assertThat(jobDetails).isEmpty();
  }

  @Test
  @SneakyThrows
  void shouldFetchJobDetailsWhenWorkflowRanOnlyOnce() {
    final String runDetailUri =
        String.format(
            "/%s/actions/workflows/%s/runs?per_page=2", workflow.getRepoName(), workflow.getId());
    when(restTemplate.getForObject(runDetailUri, WorkflowsRunDetailsResponse.class))
        .thenReturn(
            WorkflowsRunDetailsResponse.builder()
                .workflowRuns(List.of(BASE_WORKFLOWS_RUN_DETAILS_RESPONSE.getWorkflowRuns().get(0)))
                .build());

    final String jobDetailsUri =
        String.format("/%s/actions/runs/%s/jobs", workflow.getRepoName(), RUN_ID_1);
    when(restTemplate.getForObject(jobDetailsUri, WorkflowsJobDetailsResponse.class))
        .thenReturn(BASE_JOB_DETAILS_RESPONSE);

    final List<JobDetails> jobDetails = githubJobDetailsService.fetchJobDetails(workflow).get();
    final List<JobDetails> expectedJobDetails =
        List.of(JOB_DETAILS_1132386046, JOB_DETAILS_1132386127);

    assertThat(jobDetails).hasSameSizeAs(expectedJobDetails).isEqualTo(expectedJobDetails);
    verify(restTemplate, times(1)).getForObject(runDetailUri, WorkflowsRunDetailsResponse.class);
    verify(restTemplate, times(1)).getForObject(jobDetailsUri, WorkflowsJobDetailsResponse.class);
  }

  @Test
  @SneakyThrows
  void shouldFetchJobDetailsForGivenWorkflowWhenLatestBuildIsSuccess() {
    when(restTemplate.getForObject(
            String.format(
                "/%s/actions/workflows/%s/runs?per_page=2",
                workflow.getRepoName(), workflow.getId()),
            WorkflowsRunDetailsResponse.class))
        .thenReturn(BASE_WORKFLOWS_RUN_DETAILS_RESPONSE);

    when(restTemplate.getForObject(
            String.format("/%s/actions/runs/%s/jobs", workflow.getRepoName(), RUN_ID_1),
            WorkflowsJobDetailsResponse.class))
        .thenReturn(BASE_JOB_DETAILS_RESPONSE);

    final List<JobDetails> jobDetails = githubJobDetailsService.fetchJobDetails(workflow).get();

    final List<JobDetails> expectedJobDetails =
        List.of(JOB_DETAILS_1132386046, JOB_DETAILS_1132386127);

    assertThat(jobDetails).hasSameSizeAs(expectedJobDetails).isEqualTo(expectedJobDetails);
  }

  @Test
  @SneakyThrows
  void shouldFetchJobDetailsForGivenWorkflowWhenLatestBuildIsSuccessAndPreviousBuildIsFailed() {
    final WorkflowRunDetails latestWorkflowRunDetails =
        BASE_WORKFLOWS_RUN_DETAILS_RESPONSE.getWorkflowRuns().get(0).toBuilder()
            .status(COMPLETED)
            .conclusion(SUCCESS)
            .build();

    final WorkflowRunDetails previousWorkflowRunDetails =
        BASE_WORKFLOWS_RUN_DETAILS_RESPONSE.getWorkflowRuns().get(1).toBuilder()
            .status(COMPLETED)
            .conclusion(FAILURE)
            .build();

    final WorkflowsRunDetailsResponse workflowsRunDetailsResponse =
        WorkflowsRunDetailsResponse.builder()
            .workflowRuns(List.of(latestWorkflowRunDetails, previousWorkflowRunDetails))
            .build();

    when(restTemplate.getForObject(
            String.format(
                "/%s/actions/workflows/%s/runs?per_page=2",
                workflow.getRepoName(), workflow.getId()),
            WorkflowsRunDetailsResponse.class))
        .thenReturn(workflowsRunDetailsResponse);

    final WorkflowsJobDetailsResponse currentWorkflowsJobDetailsResponse =
        WorkflowsJobDetailsResponse.builder()
            .jobs(
                List.of(
                    BASE_JOB_DETAILS_RESPONSE.getJobs().get(0).toBuilder()
                        .status(COMPLETED)
                        .conclusion(SUCCESS)
                        .build(),
                    BASE_JOB_DETAILS_RESPONSE.getJobs().get(1).toBuilder()
                        .status(COMPLETED)
                        .conclusion(SUCCESS)
                        .build()))
            .build();

    final int currentRunId = workflowsRunDetailsResponse.getWorkflowRuns().get(0).getId();

    when(restTemplate.getForObject(
            String.format("/%s/actions/runs/%s/jobs", workflow.getRepoName(), currentRunId),
            WorkflowsJobDetailsResponse.class))
        .thenReturn(currentWorkflowsJobDetailsResponse);

    final List<JobDetails> jobDetails = githubJobDetailsService.fetchJobDetails(workflow).get();

    final List<JobDetails> expectedJobDetails =
        List.of(
            JOB_DETAILS_1132386046.toBuilder()
                .activity(SLEEPING)
                .lastBuildStatus(Status.SUCCESS)
                .build(),
            JOB_DETAILS_1132386127.toBuilder()
                .activity(SLEEPING)
                .lastBuildStatus(Status.SUCCESS)
                .build());

    assertThat(jobDetails).hasSameSizeAs(expectedJobDetails).isEqualTo(expectedJobDetails);
    final String cacheKey = createCacheKey(currentRunId, workflow.getRepoName(), workflow.getId());
    verify(mockWorkflowJobDetailsCache).getIfPresent(cacheKey);
    verify(mockWorkflowJobDetailsCache).put(cacheKey, currentWorkflowsJobDetailsResponse.getJobs());
  }

  @ParameterizedTest(
      name = "shouldFetchJobDetailsForGivenWorkflowWhenLatestBuildIs{0}AndPreviousBuildStatusIs{1}")
  @MethodSource(value = "getSuccessPermutationArguments")
  @SneakyThrows
  void
      shouldFetchJobDetailsForGivenWorkflowWhenLatestBuildIsNotCompletedAndLastBuildStatusIsNotFailure(
          RunStatus latestRunStatus, RunConclusion previousConclusion) {
    final WorkflowRunDetails latestWorkflowRunDetails =
        BASE_WORKFLOWS_RUN_DETAILS_RESPONSE.getWorkflowRuns().get(0).toBuilder()
            .status(latestRunStatus)
            .conclusion(null)
            .build();

    final WorkflowRunDetails previousWorkflowRunDetails =
        BASE_WORKFLOWS_RUN_DETAILS_RESPONSE.getWorkflowRuns().get(1).toBuilder()
            .conclusion(previousConclusion)
            .build();
    final WorkflowsRunDetailsResponse workflowsRunDetailsResponse =
        WorkflowsRunDetailsResponse.builder()
            .workflowRuns(List.of(latestWorkflowRunDetails, previousWorkflowRunDetails))
            .build();

    when(restTemplate.getForObject(
            String.format(
                "/%s/actions/workflows/%s/runs?per_page=2",
                workflow.getRepoName(), workflow.getId()),
            WorkflowsRunDetailsResponse.class))
        .thenReturn(workflowsRunDetailsResponse);

    final int runId = workflowsRunDetailsResponse.getWorkflowRuns().get(0).getId();

    final WorkflowsJobDetailsResponse workflowsJobDetailsResponse =
        WorkflowsJobDetailsResponse.builder()
            .jobs(
                List.of(
                    BASE_JOB_DETAILS_RESPONSE.getJobs().get(0).toBuilder()
                        .status(QUEUED)
                        .conclusion(null)
                        .completedAt(null)
                        .build(),
                    BASE_JOB_DETAILS_RESPONSE.getJobs().get(1).toBuilder()
                        .status(QUEUED)
                        .conclusion(null)
                        .completedAt(null)
                        .build()))
            .build();

    when(restTemplate.getForObject(
            String.format("/%s/actions/runs/%s/jobs", workflow.getRepoName(), runId),
            WorkflowsJobDetailsResponse.class))
        .thenReturn(workflowsJobDetailsResponse);

    final List<JobDetails> jobDetails = githubJobDetailsService.fetchJobDetails(workflow).get();

    final List<JobDetails> expectedJobDetails =
        List.of(
            JOB_DETAILS_1132386046.toBuilder()
                .activity(BUILDING)
                .lastBuildTime(Instant.parse("2020-09-17T06:14:21Z"))
                .build(),
            JOB_DETAILS_1132386127.toBuilder()
                .activity(BUILDING)
                .lastBuildTime(Instant.parse("2020-09-17T06:14:21Z"))
                .build());

    assertThat(jobDetails).hasSameSizeAs(expectedJobDetails).isEqualTo(expectedJobDetails);
    final String cacheKey = createCacheKey(runId, workflow.getRepoName(), workflow.getId());
    verify(mockWorkflowJobDetailsCache).getIfPresent(cacheKey);
    verify(mockWorkflowJobDetailsCache).put(cacheKey, workflowsJobDetailsResponse.getJobs());
  }

  @ParameterizedTest(
      name =
          "shouldFetchJobDetailsForGivenWorkflowWhenLatestBuildIs{0}AndPreviousRunConclusionIs{1}")
  @MethodSource(value = "getPermutationOfWorkflowRunStatusAndPreviousConclusion")
  @SneakyThrows
  void shouldFetchJobDetailsForGivenWorkflowWhenLatestAndPreviousBothBuildsAreNotSuccessOrSkipped(
      RunStatus latestRunStatus, RunConclusion previousConclusion) {
    final WorkflowRunDetails latestWorkflowRunDetails =
        BASE_WORKFLOWS_RUN_DETAILS_RESPONSE.getWorkflowRuns().get(0).toBuilder()
            .status(latestRunStatus)
            .conclusion(null)
            .build();

    final WorkflowRunDetails previousWorkflowRunDetails =
        BASE_WORKFLOWS_RUN_DETAILS_RESPONSE.getWorkflowRuns().get(1).toBuilder()
            .status(COMPLETED)
            .conclusion(previousConclusion)
            .build();

    final WorkflowsRunDetailsResponse workflowsRunDetailsResponse =
        WorkflowsRunDetailsResponse.builder()
            .workflowRuns(List.of(latestWorkflowRunDetails, previousWorkflowRunDetails))
            .build();

    when(restTemplate.getForObject(
            String.format(
                "/%s/actions/workflows/%s/runs?per_page=2",
                workflow.getRepoName(), workflow.getId()),
            WorkflowsRunDetailsResponse.class))
        .thenReturn(workflowsRunDetailsResponse);

    final WorkflowsJobDetailsResponse currentWorkflowsJobDetailsResponse =
        WorkflowsJobDetailsResponse.builder()
            .jobs(
                List.of(
                    BASE_JOB_DETAILS_RESPONSE.getJobs().get(0).toBuilder()
                        .status(QUEUED)
                        .conclusion(null)
                        .completedAt(null)
                        .build(),
                    BASE_JOB_DETAILS_RESPONSE.getJobs().get(1).toBuilder()
                        .status(QUEUED)
                        .conclusion(null)
                        .completedAt(null)
                        .build()))
            .build();

    final int currentRunId = workflowsRunDetailsResponse.getWorkflowRuns().get(0).getId();

    when(restTemplate.getForObject(
            String.format("/%s/actions/runs/%s/jobs", workflow.getRepoName(), currentRunId),
            WorkflowsJobDetailsResponse.class))
        .thenReturn(currentWorkflowsJobDetailsResponse);

    final WorkflowsJobDetailsResponse previousWorkflowsJobDetailsResponse =
        WorkflowsJobDetailsResponse.builder()
            .jobs(
                List.of(
                    BASE_JOB_DETAILS_RESPONSE.getJobs().get(0).toBuilder()
                        .status(COMPLETED)
                        .startedAt(Instant.parse("2020-09-17T06:11:04Z"))
                        .completedAt(Instant.parse("2020-09-17T06:13:21Z"))
                        .conclusion(previousConclusion)
                        .build(),
                    BASE_JOB_DETAILS_RESPONSE.getJobs().get(1).toBuilder()
                        .status(COMPLETED)
                        .startedAt(Instant.parse("2020-09-17T06:11:04Z"))
                        .completedAt(Instant.parse("2020-09-17T06:14:20Z"))
                        .conclusion(SUCCESS)
                        .build()))
            .build();

    final int previousRunId = workflowsRunDetailsResponse.getWorkflowRuns().get(1).getId();

    when(restTemplate.getForObject(
            String.format("/%s/actions/runs/%s/jobs", workflow.getRepoName(), previousRunId),
            WorkflowsJobDetailsResponse.class))
        .thenReturn(previousWorkflowsJobDetailsResponse);

    final List<JobDetails> jobDetails = githubJobDetailsService.fetchJobDetails(workflow).get();

    final List<JobDetails> expectedJobDetails =
        List.of(
            JOB_DETAILS_1132386046.toBuilder()
                .activity(BUILDING)
                .lastBuildStatus(RunConclusion.getStatus(previousConclusion))
                .lastBuildTime(Instant.parse("2020-09-17T06:13:21Z"))
                .build(),
            JOB_DETAILS_1132386127.toBuilder()
                .activity(BUILDING)
                .lastBuildTime(Instant.parse("2020-09-17T06:14:20Z"))
                .build());

    assertThat(jobDetails).hasSameSizeAs(expectedJobDetails).isEqualTo(expectedJobDetails);
    final String cacheKey1 = createCacheKey(currentRunId, workflow.getRepoName(), workflow.getId());
    final String cacheKey2 =
        createCacheKey(previousRunId, workflow.getRepoName(), workflow.getId());
    verify(mockWorkflowJobDetailsCache).getIfPresent(cacheKey1);
    verify(mockWorkflowJobDetailsCache).getIfPresent(cacheKey2);
    verify(mockWorkflowJobDetailsCache)
        .put(cacheKey1, currentWorkflowsJobDetailsResponse.getJobs());
    verify(mockWorkflowJobDetailsCache)
        .put(cacheKey2, previousWorkflowsJobDetailsResponse.getJobs());
  }

  @Test
  @SneakyThrows
  void shouldFetchJobDetailsWhenLatestBuildIsRunningAndItHasANewJobAddedAndLastJobIsSuccess() {
    final WorkflowRunDetails latestWorkflowRunDetails =
        BASE_WORKFLOWS_RUN_DETAILS_RESPONSE.getWorkflowRuns().get(0).toBuilder()
            .status(IN_PROGRESS)
            .conclusion(null)
            .build();

    final WorkflowRunDetails previousWorkflowRunDetails =
        BASE_WORKFLOWS_RUN_DETAILS_RESPONSE.getWorkflowRuns().get(1);

    final WorkflowsRunDetailsResponse workflowsRunDetailsResponse =
        WorkflowsRunDetailsResponse.builder()
            .workflowRuns(List.of(latestWorkflowRunDetails, previousWorkflowRunDetails))
            .build();

    when(restTemplate.getForObject(
            String.format(
                "/%s/actions/workflows/%s/runs?per_page=2",
                workflow.getRepoName(), workflow.getId()),
            WorkflowsRunDetailsResponse.class))
        .thenReturn(workflowsRunDetailsResponse);

    final WorkflowsJobDetailsResponse currentWorkflowsJobDetailsResponse =
        WorkflowsJobDetailsResponse.builder()
            .jobs(
                List.of(
                    BASE_JOB_DETAILS_RESPONSE.getJobs().get(0).toBuilder()
                        .status(QUEUED)
                        .conclusion(null)
                        .completedAt(null)
                        .build(),
                    BASE_JOB_DETAILS_RESPONSE.getJobs().get(1).toBuilder()
                        .status(QUEUED)
                        .conclusion(null)
                        .completedAt(null)
                        .build()))
            .build();

    final int currentRunId = workflowsRunDetailsResponse.getWorkflowRuns().get(0).getId();

    when(restTemplate.getForObject(
            String.format("/%s/actions/runs/%s/jobs", workflow.getRepoName(), currentRunId),
            WorkflowsJobDetailsResponse.class))
        .thenReturn(currentWorkflowsJobDetailsResponse);

    final List<JobDetails> jobDetails = githubJobDetailsService.fetchJobDetails(workflow).get();

    final List<JobDetails> expectedJobDetails =
        List.of(
            JOB_DETAILS_1132386046.toBuilder()
                .activity(BUILDING)
                .lastBuildTime(Instant.parse("2020-09-17T06:14:21Z"))
                .build(),
            JOB_DETAILS_1132386127.toBuilder()
                .activity(BUILDING)
                .lastBuildTime(Instant.parse("2020-09-17T06:14:21Z"))
                .build());

    assertThat(jobDetails).hasSameSizeAs(expectedJobDetails).isEqualTo(expectedJobDetails);
    final String cacheKey1 = createCacheKey(currentRunId, workflow.getRepoName(), workflow.getId());
    verify(mockWorkflowJobDetailsCache).getIfPresent(cacheKey1);
    verify(mockWorkflowJobDetailsCache)
        .put(cacheKey1, currentWorkflowsJobDetailsResponse.getJobs());
  }

  @ParameterizedTest(
      name =
          "shouldFetchJobDetailsForGivenWorkflowWhenLatestBuildIs{0}AndItHasANewJobAddedAndPreviousRunConclusionIs{1}")
  @MethodSource(value = "getPermutationOfWorkflowRunStatusAndPreviousConclusion")
  @SneakyThrows
  void
      shouldFetchJobDetailsWhenLatestBuildIsRunningAndItHasANewJobAddedAndLastJobIsFailureConclusion(
          RunStatus latestRunStatus, RunConclusion previousConclusion) {
    final WorkflowRunDetails latestWorkflowRunDetails =
        BASE_WORKFLOWS_RUN_DETAILS_RESPONSE.getWorkflowRuns().get(0).toBuilder()
            .status(latestRunStatus)
            .conclusion(null)
            .build();

    final WorkflowRunDetails previousWorkflowRunDetails =
        BASE_WORKFLOWS_RUN_DETAILS_RESPONSE.getWorkflowRuns().get(1).toBuilder()
            .status(COMPLETED)
            .conclusion(previousConclusion)
            .build();

    final WorkflowsRunDetailsResponse workflowsRunDetailsResponse =
        WorkflowsRunDetailsResponse.builder()
            .workflowRuns(List.of(latestWorkflowRunDetails, previousWorkflowRunDetails))
            .build();

    when(restTemplate.getForObject(
            String.format(
                "/%s/actions/workflows/%s/runs?per_page=2",
                workflow.getRepoName(), workflow.getId()),
            WorkflowsRunDetailsResponse.class))
        .thenReturn(workflowsRunDetailsResponse);

    final WorkflowsJobDetailsResponse currentWorkflowsJobDetailsResponse =
        WorkflowsJobDetailsResponse.builder()
            .jobs(
                List.of(
                    BASE_JOB_DETAILS_RESPONSE.getJobs().get(0).toBuilder()
                        .status(QUEUED)
                        .conclusion(null)
                        .completedAt(null)
                        .build(),
                    BASE_JOB_DETAILS_RESPONSE.getJobs().get(1).toBuilder()
                        .status(QUEUED)
                        .conclusion(null)
                        .completedAt(null)
                        .build()))
            .build();

    final int currentRunId = workflowsRunDetailsResponse.getWorkflowRuns().get(0).getId();

    when(restTemplate.getForObject(
            String.format("/%s/actions/runs/%s/jobs", workflow.getRepoName(), currentRunId),
            WorkflowsJobDetailsResponse.class))
        .thenReturn(currentWorkflowsJobDetailsResponse);

    final WorkflowsJobDetailsResponse previousWorkflowsJobDetailsResponse =
        WorkflowsJobDetailsResponse.builder()
            .jobs(
                List.of(
                    BASE_JOB_DETAILS_RESPONSE.getJobs().get(0).toBuilder()
                        .status(COMPLETED)
                        .startedAt(Instant.parse("2020-09-17T06:11:04Z"))
                        .completedAt(Instant.parse("2020-09-17T06:13:21Z"))
                        .conclusion(previousConclusion)
                        .build()))
            .build();

    final int previousRunId = workflowsRunDetailsResponse.getWorkflowRuns().get(1).getId();

    when(restTemplate.getForObject(
            String.format("/%s/actions/runs/%s/jobs", workflow.getRepoName(), previousRunId),
            WorkflowsJobDetailsResponse.class))
        .thenReturn(previousWorkflowsJobDetailsResponse);

    final List<JobDetails> jobDetails = githubJobDetailsService.fetchJobDetails(workflow).get();

    final List<JobDetails> expectedJobDetails =
        List.of(
            JOB_DETAILS_1132386046.toBuilder()
                .activity(BUILDING)
                .lastBuildStatus(RunConclusion.getStatus(previousConclusion))
                .lastBuildTime(Instant.parse("2020-09-17T06:13:21Z"))
                .build(),
            JOB_DETAILS_1132386127.toBuilder()
                .activity(BUILDING)
                .lastBuildStatus(Status.UNKNOWN)
                .lastBuildTime(Instant.parse("2020-09-18T06:11:37Z"))
                .build());

    assertThat(jobDetails).hasSameSizeAs(expectedJobDetails).isEqualTo(expectedJobDetails);
    final String cacheKey1 = createCacheKey(currentRunId, workflow.getRepoName(), workflow.getId());
    final String cacheKey2 =
        createCacheKey(previousRunId, workflow.getRepoName(), workflow.getId());
    verify(mockWorkflowJobDetailsCache).getIfPresent(cacheKey1);
    verify(mockWorkflowJobDetailsCache).getIfPresent(cacheKey2);
    verify(mockWorkflowJobDetailsCache)
        .put(cacheKey1, currentWorkflowsJobDetailsResponse.getJobs());
    verify(mockWorkflowJobDetailsCache)
        .put(cacheKey2, previousWorkflowsJobDetailsResponse.getJobs());
  }

  private String createCacheKey(int runId, String repoName, int workflowId) {
    return String.format("%s_%d_%d", repoName, workflowId, runId);
  }

  @Test
  @SneakyThrows
  void shouldNotThrowErrorIfTheFetchWorkflowsRunsCallFails() {
    when(restTemplate.getForObject(
            String.format(
                "/%s/actions/workflows/%s/runs?per_page=2",
                workflow.getRepoName(), workflow.getId()),
            WorkflowsRunDetailsResponse.class))
        .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));
    assertThat(githubJobDetailsService.fetchJobDetails(workflow).get()).isEmpty();
  }

  @Test
  @SneakyThrows
  void shouldNotThrowErrorIfTheFetchJobDetailsRunsCallFails() {
    when(restTemplate.getForObject(
            String.format(
                "/%s/actions/workflows/%s/runs?per_page=2",
                workflow.getRepoName(), workflow.getId()),
            WorkflowsRunDetailsResponse.class))
        .thenReturn(BASE_WORKFLOWS_RUN_DETAILS_RESPONSE);

    when(restTemplate.getForObject(
            String.format(
                "/%s/actions/runs/%s/jobs",
                workflow.getRepoName(),
                BASE_WORKFLOWS_RUN_DETAILS_RESPONSE.getWorkflowRuns().get(0).getId()),
            WorkflowsJobDetailsResponse.class))
        .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

    assertThat(githubJobDetailsService.fetchJobDetails(workflow).get()).isEmpty();
  }

  @Test
  @SneakyThrows
  void shouldNotFetchJobDetailsForGivenWorkflowWhenTheDetailsIsPresentInCacheWithCompletedStatus() {
    final WorkflowRunDetails latestWorkflowRunDetails =
        BASE_WORKFLOWS_RUN_DETAILS_RESPONSE.getWorkflowRuns().get(0).toBuilder()
            .status(COMPLETED)
            .conclusion(SUCCESS)
            .build();

    final WorkflowRunDetails previousWorkflowRunDetails =
        BASE_WORKFLOWS_RUN_DETAILS_RESPONSE.getWorkflowRuns().get(1).toBuilder()
            .status(COMPLETED)
            .conclusion(FAILURE)
            .build();

    final WorkflowsRunDetailsResponse workflowsRunDetailsResponse =
        WorkflowsRunDetailsResponse.builder()
            .workflowRuns(List.of(latestWorkflowRunDetails, previousWorkflowRunDetails))
            .build();

    when(restTemplate.getForObject(
            String.format(
                "/%s/actions/workflows/%s/runs?per_page=2",
                workflow.getRepoName(), workflow.getId()),
            WorkflowsRunDetailsResponse.class))
        .thenReturn(workflowsRunDetailsResponse);

    final int currentRunId = workflowsRunDetailsResponse.getWorkflowRuns().get(0).getId();
    final String cacheKey = createCacheKey(currentRunId, workflow.getRepoName(), workflow.getId());

    final List<WorkflowsJobDetails> workflowsJobDetails =
        List.of(
            BASE_JOB_DETAILS_RESPONSE.getJobs().get(0).toBuilder()
                .status(COMPLETED)
                .conclusion(SUCCESS)
                .build());

    when(mockWorkflowJobDetailsCache.getIfPresent(cacheKey)).thenReturn(workflowsJobDetails);

    final List<JobDetails> jobDetails = githubJobDetailsService.fetchJobDetails(workflow).get();

    final List<JobDetails> expectedJobDetails =
        List.of(
            JOB_DETAILS_1132386046.toBuilder()
                .activity(SLEEPING)
                .lastBuildStatus(Status.SUCCESS)
                .build());

    assertThat(jobDetails).hasSameSizeAs(expectedJobDetails).isEqualTo(expectedJobDetails);

    verify(mockWorkflowJobDetailsCache).getIfPresent(cacheKey);
    verify(mockWorkflowJobDetailsCache, never()).put(cacheKey, workflowsJobDetails);
    verify(restTemplate, never())
        .getForObject(
            String.format("/%s/actions/runs/%s/jobs", workflow.getRepoName(), currentRunId),
            WorkflowsJobDetailsResponse.class);
  }
}
