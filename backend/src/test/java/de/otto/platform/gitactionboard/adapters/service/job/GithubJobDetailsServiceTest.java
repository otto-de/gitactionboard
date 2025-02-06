package de.otto.platform.gitactionboard.adapters.service.job;

import static de.otto.platform.gitactionboard.TestUtil.readFile;
import static de.otto.platform.gitactionboard.domain.workflow.Activity.BUILDING;
import static de.otto.platform.gitactionboard.domain.workflow.Activity.SLEEPING;
import static de.otto.platform.gitactionboard.domain.workflow.RunConclusion.FAILURE;
import static de.otto.platform.gitactionboard.domain.workflow.RunConclusion.SKIPPED;
import static de.otto.platform.gitactionboard.domain.workflow.RunConclusion.SUCCESS;
import static de.otto.platform.gitactionboard.domain.workflow.RunStatus.COMPLETED;
import static de.otto.platform.gitactionboard.domain.workflow.RunStatus.IN_PROGRESS;
import static de.otto.platform.gitactionboard.domain.workflow.RunStatus.QUEUED;
import static de.otto.platform.gitactionboard.fixtures.JobFixture.getJobDetailsBuilder;
import static de.otto.platform.gitactionboard.fixtures.WorkflowsFixture.REPO_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Cache;
import de.otto.platform.gitactionboard.Sequential;
import de.otto.platform.gitactionboard.adapters.service.GithubApiService;
import de.otto.platform.gitactionboard.adapters.service.job.WorkflowsJobDetailsResponse.WorkflowsJobDetails;
import de.otto.platform.gitactionboard.adapters.service.job.WorkflowsRunDetailsResponse.WorkflowRunDetails;
import de.otto.platform.gitactionboard.config.CodecConfig;
import de.otto.platform.gitactionboard.domain.repository.WorkflowJobRepository;
import de.otto.platform.gitactionboard.domain.repository.WorkflowRunRepository;
import de.otto.platform.gitactionboard.domain.workflow.JobDetails;
import de.otto.platform.gitactionboard.domain.workflow.JobStatus;
import de.otto.platform.gitactionboard.domain.workflow.RunConclusion;
import de.otto.platform.gitactionboard.domain.workflow.RunStatus;
import de.otto.platform.gitactionboard.domain.workflow.Workflow;
import de.otto.platform.gitactionboard.domain.workflow.WorkflowJob;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
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

@ExtendWith(MockitoExtension.class)
@Sequential
class GithubJobDetailsServiceTest {
  private static final String WORKFLOW_NAME = "hello-world-checks";
  private static final JobDetails JOB_DETAILS_1132386127 =
      getJobDetailsBuilder().triggeredEvent("schedule").build();

  private static final JobDetails JOB_DETAILS_1132386046 =
      getJobDetailsBuilder()
          .id(1132386046)
          .name("talisman-checks")
          .url("https://github.com/johndoe/hello-world/runs/1132386046")
          .lastBuildTime(Instant.parse("2024-09-18T06:11:41.000Z"))
          .triggeredEvent("schedule")
          .build();

  private static final ObjectMapper objectMapper = CodecConfig.OBJECT_MAPPER_BUILDER.build();
  private static final String RUN_DETAILS_URL_TEMPLATE = "/%s/actions/workflows/%s/runs?per_page=2";
  private static final String JOB_DETAILS_URL_TEMPLATE = "/%s/actions/runs/%s/jobs";
  private static final String ACCESS_TOKEN = "accessToken";

  private final Workflow workflow =
      Workflow.builder().id(2151835).name(WORKFLOW_NAME).repoName(REPO_NAME).build();

  @Mock private GithubApiService apiService;

  @Mock private Cache<String, List<WorkflowJob>> mockWorkflowJobDetailsCache;

  @Mock private WorkflowRunRepository workflowRunRepository;
  @Mock private WorkflowJobRepository workflowJobRepository;

  private GithubJobDetailsService githubJobDetailsService;
  private static WorkflowsJobDetailsResponse BASE_JOB_DETAILS_RESPONSE;
  private static WorkflowsRunDetailsResponse BASE_WORKFLOWS_RUN_DETAILS_RESPONSE;
  private static long RUN_ID_1;
  private static int RUN_ATTEMPT_1;

  @BeforeAll
  @SneakyThrows
  static void beforeAll() {
    BASE_WORKFLOWS_RUN_DETAILS_RESPONSE =
        objectMapper.readValue(
            readFile("testData/workflowRuns2151835.json"), WorkflowsRunDetailsResponse.class);

    BASE_JOB_DETAILS_RESPONSE =
        objectMapper.readValue(
            readFile("testData/jobsDetails260614021.json"), WorkflowsJobDetailsResponse.class);

    final WorkflowRunDetails workflowRunDetails =
        BASE_WORKFLOWS_RUN_DETAILS_RESPONSE.getWorkflowRuns().getFirst();
    RUN_ID_1 = workflowRunDetails.getId();
    RUN_ATTEMPT_1 = workflowRunDetails.getRunAttempt();
  }

  private static Stream<Arguments> getPermutationOfWorkflowRunStatusAndPreviousConclusion() {

    final List<RunConclusion> runConclusions =
        Arrays.stream(RunConclusion.values())
            .filter(conclusion -> !conclusion.equals(SUCCESS) && !conclusion.equals(SKIPPED))
            .toList();

    return Stream.of(QUEUED, IN_PROGRESS)
        .flatMap(
            status ->
                runConclusions.stream().map(conclusion -> Arguments.of(status.name(), conclusion)));
  }

  private static Stream<Arguments> getSuccessPermutationArguments() {
    return Stream.of(QUEUED, IN_PROGRESS)
        .flatMap(
            status ->
                Stream.of(SUCCESS, SKIPPED)
                    .map(conclusion -> Arguments.of(status.name(), conclusion)));
  }

  @BeforeEach
  void setUp() {
    githubJobDetailsService =
        new GithubJobDetailsService(
            apiService, mockWorkflowJobDetailsCache, workflowRunRepository, workflowJobRepository);
  }

  @Test
  @SneakyThrows
  void shouldReturnEmptyListAsJobDetailsIfWorkflowNeverRan() {
    when(apiService.getForObject(
            RUN_DETAILS_URL_TEMPLATE.formatted(workflow.getRepoName(), workflow.getId()),
            ACCESS_TOKEN,
            WorkflowsRunDetailsResponse.class))
        .thenReturn(
            WorkflowsRunDetailsResponse.builder().workflowRuns(Collections.emptyList()).build());
    when(workflowRunRepository.save(anyList())).thenReturn(CompletableFuture.completedFuture(null));

    final List<JobDetails> jobDetails =
        githubJobDetailsService.fetchJobDetails(workflow, ACCESS_TOKEN).get();
    assertThat(jobDetails).isEmpty();
    verify(workflowRunRepository, times(1)).save(List.of());
  }

  @Test
  @SneakyThrows
  void shouldFetchWorkflowJobsWhenWorkflowRanOnlyOnce() {
    final long workflowId = workflow.getId();
    final String runDetailUri =
        RUN_DETAILS_URL_TEMPLATE.formatted(workflow.getRepoName(), workflowId);
    final WorkflowRunDetails workflowRunDetails =
        BASE_WORKFLOWS_RUN_DETAILS_RESPONSE.getWorkflowRuns().getFirst();
    when(workflowJobRepository.findByWorkflowRunIdAndRunAttempt(anyLong(), anyInt()))
        .thenReturn(CompletableFuture.completedFuture(List.of()));
    when(apiService.getForObject(runDetailUri, ACCESS_TOKEN, WorkflowsRunDetailsResponse.class))
        .thenReturn(
            WorkflowsRunDetailsResponse.builder()
                .workflowRuns(List.of(workflowRunDetails))
                .build());
    when(workflowRunRepository.save(anyList())).thenReturn(CompletableFuture.completedFuture(null));
    when(workflowJobRepository.save(anyList())).thenReturn(CompletableFuture.completedFuture(null));

    final String jobDetailsUri =
        JOB_DETAILS_URL_TEMPLATE.formatted(workflow.getRepoName(), RUN_ID_1);
    when(apiService.getForObject(jobDetailsUri, ACCESS_TOKEN, WorkflowsJobDetailsResponse.class))
        .thenReturn(BASE_JOB_DETAILS_RESPONSE);

    final List<JobDetails> jobDetails =
        githubJobDetailsService.fetchJobDetails(workflow, ACCESS_TOKEN).get();
    final List<JobDetails> expectedJobDetails =
        List.of(JOB_DETAILS_1132386046, JOB_DETAILS_1132386127);

    assertThat(jobDetails).hasSameSizeAs(expectedJobDetails).isEqualTo(expectedJobDetails);
    verify(apiService, times(1))
        .getForObject(runDetailUri, ACCESS_TOKEN, WorkflowsRunDetailsResponse.class);
    verify(apiService, times(1))
        .getForObject(jobDetailsUri, ACCESS_TOKEN, WorkflowsJobDetailsResponse.class);
    verify(workflowRunRepository, times(1)).save(List.of(workflowRunDetails.toWorkflowRun()));
    verify(workflowJobRepository, times(1))
        .save(
            BASE_JOB_DETAILS_RESPONSE.getJobs().stream()
                .map(jobDetails1 -> jobDetails1.toWorkflowJob(workflowId))
                .toList());
    verify(workflowJobRepository, times(1))
        .findByWorkflowRunIdAndRunAttempt(
            workflowRunDetails.getId(), workflowRunDetails.getRunAttempt());
  }

  @Test
  @SneakyThrows
  void shouldSkipAPICallWhenDataIsPresentInDB() {
    final long workflowId = workflow.getId();
    final String runDetailUri =
        RUN_DETAILS_URL_TEMPLATE.formatted(workflow.getRepoName(), workflowId);
    final WorkflowRunDetails workflowRunDetails =
        BASE_WORKFLOWS_RUN_DETAILS_RESPONSE.getWorkflowRuns().getFirst();

    when(apiService.getForObject(runDetailUri, ACCESS_TOKEN, WorkflowsRunDetailsResponse.class))
        .thenReturn(
            WorkflowsRunDetailsResponse.builder()
                .workflowRuns(List.of(workflowRunDetails))
                .build());
    when(workflowRunRepository.save(anyList())).thenReturn(CompletableFuture.completedFuture(null));

    when(workflowJobRepository.findByWorkflowRunIdAndRunAttempt(anyLong(), anyInt()))
        .thenReturn(
            CompletableFuture.completedFuture(
                BASE_JOB_DETAILS_RESPONSE.getJobs().stream()
                    .map(jobDetails -> jobDetails.toWorkflowJob(workflowId))
                    .toList()));

    final List<JobDetails> jobDetails =
        githubJobDetailsService.fetchJobDetails(workflow, ACCESS_TOKEN).get();
    final List<JobDetails> expectedJobDetails =
        List.of(JOB_DETAILS_1132386046, JOB_DETAILS_1132386127);

    assertThat(jobDetails).hasSameSizeAs(expectedJobDetails).isEqualTo(expectedJobDetails);
    verify(apiService, times(1))
        .getForObject(runDetailUri, ACCESS_TOKEN, WorkflowsRunDetailsResponse.class);

    verify(workflowRunRepository, times(1)).save(List.of(workflowRunDetails.toWorkflowRun()));
    verify(workflowJobRepository, times(1))
        .findByWorkflowRunIdAndRunAttempt(
            workflowRunDetails.getId(), workflowRunDetails.getRunAttempt());
    verifyNoMoreInteractions(apiService, workflowJobRepository, workflowRunRepository);
  }

  @Test
  @SneakyThrows
  void shouldFetchWorkflowJobsForGivenWorkflowWhenLatestBuildIsSuccess() {
    when(apiService.getForObject(
            RUN_DETAILS_URL_TEMPLATE.formatted(workflow.getRepoName(), workflow.getId()),
            ACCESS_TOKEN,
            WorkflowsRunDetailsResponse.class))
        .thenReturn(BASE_WORKFLOWS_RUN_DETAILS_RESPONSE);
    when(workflowJobRepository.findByWorkflowRunIdAndRunAttempt(anyLong(), anyInt()))
        .thenReturn(CompletableFuture.completedFuture(List.of()));

    when(apiService.getForObject(
            JOB_DETAILS_URL_TEMPLATE.formatted(workflow.getRepoName(), RUN_ID_1),
            ACCESS_TOKEN,
            WorkflowsJobDetailsResponse.class))
        .thenReturn(BASE_JOB_DETAILS_RESPONSE);
    when(workflowRunRepository.save(anyList())).thenReturn(CompletableFuture.completedFuture(null));
    when(workflowJobRepository.save(anyList())).thenReturn(CompletableFuture.completedFuture(null));

    final List<JobDetails> jobDetails =
        githubJobDetailsService.fetchJobDetails(workflow, ACCESS_TOKEN).get();

    final List<JobDetails> expectedJobDetails =
        List.of(JOB_DETAILS_1132386046, JOB_DETAILS_1132386127);

    assertThat(jobDetails).hasSameSizeAs(expectedJobDetails).isEqualTo(expectedJobDetails);
    verify(workflowJobRepository, times(1))
        .findByWorkflowRunIdAndRunAttempt(RUN_ID_1, RUN_ATTEMPT_1);
    verifyNoMoreInteractions(workflowJobRepository);
  }

  @Test
  @SneakyThrows
  void shouldFetchWorkflowJobsForGivenWorkflowWhenLatestBuildIsSuccessAndPreviousBuildIsFailed() {
    final WorkflowRunDetails latestWorkflowRunDetails =
        BASE_WORKFLOWS_RUN_DETAILS_RESPONSE.getWorkflowRuns().getFirst().toBuilder()
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

    when(apiService.getForObject(
            RUN_DETAILS_URL_TEMPLATE.formatted(workflow.getRepoName(), workflow.getId()),
            ACCESS_TOKEN,
            WorkflowsRunDetailsResponse.class))
        .thenReturn(workflowsRunDetailsResponse);

    when(workflowJobRepository.findByWorkflowRunIdAndRunAttempt(anyLong(), anyInt()))
        .thenReturn(CompletableFuture.completedFuture(List.of()));

    final WorkflowsJobDetailsResponse currentWorkflowsJobDetailsResponse =
        WorkflowsJobDetailsResponse.builder()
            .jobs(
                List.of(
                    BASE_JOB_DETAILS_RESPONSE.getJobs().getFirst().toBuilder()
                        .status(COMPLETED)
                        .conclusion(SUCCESS)
                        .build(),
                    BASE_JOB_DETAILS_RESPONSE.getJobs().get(1).toBuilder()
                        .status(COMPLETED)
                        .conclusion(SUCCESS)
                        .build()))
            .build();

    final long currentRunId = latestWorkflowRunDetails.getId();

    when(apiService.getForObject(
            JOB_DETAILS_URL_TEMPLATE.formatted(workflow.getRepoName(), currentRunId),
            ACCESS_TOKEN,
            WorkflowsJobDetailsResponse.class))
        .thenReturn(currentWorkflowsJobDetailsResponse);
    when(workflowRunRepository.save(anyList())).thenReturn(CompletableFuture.completedFuture(null));
    when(workflowJobRepository.save(anyList())).thenReturn(CompletableFuture.completedFuture(null));

    final List<JobDetails> jobDetails =
        githubJobDetailsService.fetchJobDetails(workflow, ACCESS_TOKEN).get();

    final List<JobDetails> expectedJobDetails =
        List.of(
            JOB_DETAILS_1132386046.toBuilder()
                .activity(SLEEPING)
                .lastBuildStatus(JobStatus.SUCCESS)
                .build(),
            JOB_DETAILS_1132386127.toBuilder()
                .activity(SLEEPING)
                .lastBuildStatus(JobStatus.SUCCESS)
                .build());

    assertThat(jobDetails).hasSameSizeAs(expectedJobDetails).isEqualTo(expectedJobDetails);
    final String cacheKey =
        createCacheKey(
            currentRunId,
            workflow.getRepoName(),
            workflow.getId(),
            latestWorkflowRunDetails.getRunAttempt(),
            latestWorkflowRunDetails.getUpdatedAt());
    verify(mockWorkflowJobDetailsCache).getIfPresent(cacheKey);
    verify(mockWorkflowJobDetailsCache)
        .put(
            cacheKey,
            currentWorkflowsJobDetailsResponse.getJobs().stream()
                .map(workflowsJobDetails -> workflowsJobDetails.toWorkflowJob(workflow.getId()))
                .toList());
    verify(workflowRunRepository, times(1))
        .save(
            List.of(
                latestWorkflowRunDetails.toWorkflowRun(),
                previousWorkflowRunDetails.toWorkflowRun()));
    verify(workflowJobRepository, times(1))
        .save(
            currentWorkflowsJobDetailsResponse.getJobs().stream()
                .map(jobDetails1 -> jobDetails1.toWorkflowJob(workflow.getId()))
                .toList());
    verify(workflowJobRepository, times(1))
        .findByWorkflowRunIdAndRunAttempt(currentRunId, latestWorkflowRunDetails.getRunAttempt());
    verifyNoMoreInteractions(workflowJobRepository, workflowRunRepository);
  }

  @ParameterizedTest(
      name = "shouldFetchJobDetailsForGivenWorkflowWhenLatestBuildIs{0}AndPreviousBuildStatusIs{1}")
  @MethodSource(value = "getSuccessPermutationArguments")
  @SneakyThrows
  void
      shouldFetchWorkflowJobsForGivenWorkflowWhenLatestBuildIsNotCompletedAndLastBuildStatusIsNotFailure(
          RunStatus latestRunStatus, RunConclusion previousConclusion) {
    final WorkflowRunDetails latestWorkflowRunDetails =
        BASE_WORKFLOWS_RUN_DETAILS_RESPONSE.getWorkflowRuns().getFirst().toBuilder()
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

    when(apiService.getForObject(
            RUN_DETAILS_URL_TEMPLATE.formatted(workflow.getRepoName(), workflow.getId()),
            ACCESS_TOKEN,
            WorkflowsRunDetailsResponse.class))
        .thenReturn(workflowsRunDetailsResponse);

    final long runId = latestWorkflowRunDetails.getId();

    final WorkflowsJobDetailsResponse workflowsJobDetailsResponse =
        WorkflowsJobDetailsResponse.builder()
            .jobs(
                List.of(
                    BASE_JOB_DETAILS_RESPONSE.getJobs().getFirst().toBuilder()
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

    when(workflowJobRepository.findByWorkflowRunIdAndRunAttempt(anyLong(), anyInt()))
        .thenReturn(CompletableFuture.completedFuture(List.of()));

    when(apiService.getForObject(
            JOB_DETAILS_URL_TEMPLATE.formatted(workflow.getRepoName(), runId),
            ACCESS_TOKEN,
            WorkflowsJobDetailsResponse.class))
        .thenReturn(workflowsJobDetailsResponse);
    when(workflowRunRepository.save(anyList())).thenReturn(CompletableFuture.completedFuture(null));
    when(workflowJobRepository.save(anyList())).thenReturn(CompletableFuture.completedFuture(null));

    final List<JobDetails> jobDetails =
        githubJobDetailsService.fetchJobDetails(workflow, ACCESS_TOKEN).get();

    final Instant lastBuildTime = Instant.parse("2024-09-17T06:14:21Z");
    final List<JobDetails> expectedJobDetails =
        List.of(
            JOB_DETAILS_1132386046.toBuilder()
                .activity(BUILDING)
                .lastBuildTime(lastBuildTime)
                .build(),
            JOB_DETAILS_1132386127.toBuilder()
                .activity(BUILDING)
                .lastBuildTime(lastBuildTime)
                .build());

    assertThat(jobDetails).hasSameSizeAs(expectedJobDetails).isEqualTo(expectedJobDetails);
    final String cacheKey =
        createCacheKey(
            runId,
            workflow.getRepoName(),
            workflow.getId(),
            latestWorkflowRunDetails.getRunAttempt(),
            latestWorkflowRunDetails.getUpdatedAt());
    verify(mockWorkflowJobDetailsCache).getIfPresent(cacheKey);
    verify(mockWorkflowJobDetailsCache)
        .put(
            cacheKey,
            workflowsJobDetailsResponse.getJobs().stream()
                .map(workflowsJobDetails -> workflowsJobDetails.toWorkflowJob(workflow.getId()))
                .toList());
    verify(workflowRunRepository, times(1))
        .save(
            List.of(
                latestWorkflowRunDetails.toWorkflowRun(),
                previousWorkflowRunDetails.toWorkflowRun()));
    verify(workflowJobRepository, times(1))
        .save(
            workflowsJobDetailsResponse.getJobs().stream()
                .map(jobDetails1 -> jobDetails1.toWorkflowJob(workflow.getId()))
                .toList());
    verify(workflowJobRepository, times(1)).findByWorkflowRunIdAndRunAttempt(runId, RUN_ATTEMPT_1);
    verifyNoMoreInteractions(workflowJobRepository, workflowRunRepository);
  }

  @ParameterizedTest(
      name =
          "shouldFetchJobDetailsForGivenWorkflowWhenLatestBuildIs{0}AndPreviousWorkflowRunConclusionIs{1}")
  @MethodSource(value = "getPermutationOfWorkflowRunStatusAndPreviousConclusion")
  @SneakyThrows
  void shouldFetchWorkflowJobsForGivenWorkflowWhenLatestAndPreviousBothBuildsAreNotSuccessOrSkipped(
      RunStatus latestRunStatus, RunConclusion previousConclusion) {
    final WorkflowRunDetails latestWorkflowRunDetails =
        BASE_WORKFLOWS_RUN_DETAILS_RESPONSE.getWorkflowRuns().getFirst().toBuilder()
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
    when(workflowJobRepository.findByWorkflowRunIdAndRunAttempt(anyLong(), anyInt()))
        .thenReturn(CompletableFuture.completedFuture(List.of()));
    when(apiService.getForObject(
            RUN_DETAILS_URL_TEMPLATE.formatted(workflow.getRepoName(), workflow.getId()),
            ACCESS_TOKEN,
            WorkflowsRunDetailsResponse.class))
        .thenReturn(workflowsRunDetailsResponse);

    final WorkflowsJobDetailsResponse currentWorkflowsJobDetailsResponse =
        WorkflowsJobDetailsResponse.builder()
            .jobs(
                List.of(
                    BASE_JOB_DETAILS_RESPONSE.getJobs().getFirst().toBuilder()
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

    when(apiService.getForObject(
            JOB_DETAILS_URL_TEMPLATE.formatted(
                workflow.getRepoName(), latestWorkflowRunDetails.getId()),
            ACCESS_TOKEN,
            WorkflowsJobDetailsResponse.class))
        .thenReturn(currentWorkflowsJobDetailsResponse);

    final Instant jobCompletedAt = Instant.parse("2024-09-17T06:13:21Z");
    final WorkflowsJobDetailsResponse previousWorkflowsJobDetailsResponse =
        WorkflowsJobDetailsResponse.builder()
            .jobs(
                List.of(
                    BASE_JOB_DETAILS_RESPONSE.getJobs().getFirst().toBuilder()
                        .status(COMPLETED)
                        .startedAt(Instant.parse("2024-09-17T06:11:04Z"))
                        .completedAt(jobCompletedAt)
                        .conclusion(previousConclusion)
                        .build(),
                    BASE_JOB_DETAILS_RESPONSE.getJobs().get(1).toBuilder()
                        .status(COMPLETED)
                        .startedAt(Instant.parse("2024-09-17T06:11:04Z"))
                        .completedAt(Instant.parse("2024-09-17T06:14:20Z"))
                        .conclusion(SUCCESS)
                        .build()))
            .build();

    when(apiService.getForObject(
            JOB_DETAILS_URL_TEMPLATE.formatted(
                workflow.getRepoName(), previousWorkflowRunDetails.getId()),
            ACCESS_TOKEN,
            WorkflowsJobDetailsResponse.class))
        .thenReturn(previousWorkflowsJobDetailsResponse);

    when(workflowRunRepository.save(anyList())).thenReturn(CompletableFuture.completedFuture(null));
    when(workflowJobRepository.save(anyList())).thenReturn(CompletableFuture.completedFuture(null));

    final List<JobDetails> jobDetails =
        githubJobDetailsService.fetchJobDetails(workflow, ACCESS_TOKEN).get();

    final List<JobDetails> expectedJobDetails =
        List.of(
            JOB_DETAILS_1132386046.toBuilder()
                .activity(BUILDING)
                .lastBuildStatus(RunConclusion.getStatus(previousConclusion))
                .lastBuildTime(jobCompletedAt)
                .build(),
            JOB_DETAILS_1132386127.toBuilder()
                .activity(BUILDING)
                .lastBuildTime(Instant.parse("2024-09-17T06:14:20Z"))
                .build());

    assertThat(jobDetails).hasSameSizeAs(expectedJobDetails).isEqualTo(expectedJobDetails);
    final String cacheKey1 =
        createCacheKey(
            latestWorkflowRunDetails.getId(),
            workflow.getRepoName(),
            workflow.getId(),
            latestWorkflowRunDetails.getRunAttempt(),
            latestWorkflowRunDetails.getUpdatedAt());
    final String cacheKey2 =
        createCacheKey(
            previousWorkflowRunDetails.getId(),
            workflow.getRepoName(),
            workflow.getId(),
            previousWorkflowRunDetails.getRunAttempt(),
            previousWorkflowRunDetails.getUpdatedAt());
    verify(mockWorkflowJobDetailsCache).getIfPresent(cacheKey1);
    verify(mockWorkflowJobDetailsCache).getIfPresent(cacheKey2);
    verify(mockWorkflowJobDetailsCache)
        .put(
            cacheKey1,
            currentWorkflowsJobDetailsResponse.getJobs().stream()
                .map(workflowsJobDetails -> workflowsJobDetails.toWorkflowJob(workflow.getId()))
                .toList());
    verify(mockWorkflowJobDetailsCache)
        .put(
            cacheKey2,
            previousWorkflowsJobDetailsResponse.getJobs().stream()
                .map(workflowsJobDetails -> workflowsJobDetails.toWorkflowJob(workflow.getId()))
                .toList());
    verify(workflowRunRepository, times(1))
        .save(
            List.of(
                latestWorkflowRunDetails.toWorkflowRun(),
                previousWorkflowRunDetails.toWorkflowRun()));
    verify(workflowJobRepository, times(1))
        .save(
            currentWorkflowsJobDetailsResponse.getJobs().stream()
                .map(jobDetails1 -> jobDetails1.toWorkflowJob(workflow.getId()))
                .toList());
    verify(workflowJobRepository, times(1))
        .findByWorkflowRunIdAndRunAttempt(
            latestWorkflowRunDetails.getId(), latestWorkflowRunDetails.getRunAttempt());
    verify(workflowJobRepository, times(1))
        .findByWorkflowRunIdAndRunAttempt(
            previousWorkflowRunDetails.getId(), previousWorkflowRunDetails.getRunAttempt());
    verifyNoMoreInteractions(workflowJobRepository, workflowRunRepository);
  }

  @Test
  @SneakyThrows
  void shouldFetchJobDetailsWhenLatestBuildIsRunningAndItHasANewJobAddedAndLastJobIsSuccess() {
    final WorkflowRunDetails latestWorkflowRunDetails =
        BASE_WORKFLOWS_RUN_DETAILS_RESPONSE.getWorkflowRuns().getFirst().toBuilder()
            .status(IN_PROGRESS)
            .conclusion(null)
            .build();

    final WorkflowRunDetails previousWorkflowRunDetails =
        BASE_WORKFLOWS_RUN_DETAILS_RESPONSE.getWorkflowRuns().get(1);

    final WorkflowsRunDetailsResponse workflowsRunDetailsResponse =
        WorkflowsRunDetailsResponse.builder()
            .workflowRuns(List.of(latestWorkflowRunDetails, previousWorkflowRunDetails))
            .build();

    when(apiService.getForObject(
            RUN_DETAILS_URL_TEMPLATE.formatted(workflow.getRepoName(), workflow.getId()),
            ACCESS_TOKEN,
            WorkflowsRunDetailsResponse.class))
        .thenReturn(workflowsRunDetailsResponse);

    final WorkflowsJobDetailsResponse currentWorkflowsJobDetailsResponse =
        WorkflowsJobDetailsResponse.builder()
            .jobs(
                List.of(
                    BASE_JOB_DETAILS_RESPONSE.getJobs().getFirst().toBuilder()
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
    when(workflowJobRepository.findByWorkflowRunIdAndRunAttempt(anyLong(), anyInt()))
        .thenReturn(CompletableFuture.completedFuture(List.of()));
    when(apiService.getForObject(
            JOB_DETAILS_URL_TEMPLATE.formatted(
                workflow.getRepoName(), latestWorkflowRunDetails.getId()),
            ACCESS_TOKEN,
            WorkflowsJobDetailsResponse.class))
        .thenReturn(currentWorkflowsJobDetailsResponse);
    when(workflowRunRepository.save(anyList())).thenReturn(CompletableFuture.completedFuture(null));
    when(workflowJobRepository.save(anyList())).thenReturn(CompletableFuture.completedFuture(null));

    final List<JobDetails> jobDetails =
        githubJobDetailsService.fetchJobDetails(workflow, ACCESS_TOKEN).get();

    final Instant lastBuildTime = Instant.parse("2024-09-17T06:14:21Z");
    final List<JobDetails> expectedJobDetails =
        List.of(
            JOB_DETAILS_1132386046.toBuilder()
                .activity(BUILDING)
                .lastBuildTime(lastBuildTime)
                .build(),
            JOB_DETAILS_1132386127.toBuilder()
                .activity(BUILDING)
                .lastBuildTime(lastBuildTime)
                .build());

    assertThat(jobDetails).hasSameSizeAs(expectedJobDetails).isEqualTo(expectedJobDetails);
    final String cacheKey1 =
        createCacheKey(
            latestWorkflowRunDetails.getId(),
            workflow.getRepoName(),
            workflow.getId(),
            latestWorkflowRunDetails.getRunAttempt(),
            latestWorkflowRunDetails.getUpdatedAt());
    verify(mockWorkflowJobDetailsCache).getIfPresent(cacheKey1);
    verify(mockWorkflowJobDetailsCache)
        .put(
            cacheKey1,
            currentWorkflowsJobDetailsResponse.getJobs().stream()
                .map(workflowsJobDetails -> workflowsJobDetails.toWorkflowJob(workflow.getId()))
                .toList());
    verify(workflowRunRepository, times(1))
        .save(
            List.of(
                latestWorkflowRunDetails.toWorkflowRun(),
                previousWorkflowRunDetails.toWorkflowRun()));
    verify(workflowJobRepository, times(1))
        .save(
            currentWorkflowsJobDetailsResponse.getJobs().stream()
                .map(jobDetails1 -> jobDetails1.toWorkflowJob(workflow.getId()))
                .toList());
    verify(workflowJobRepository, times(1))
        .findByWorkflowRunIdAndRunAttempt(
            latestWorkflowRunDetails.getId(), latestWorkflowRunDetails.getRunAttempt());
    verifyNoMoreInteractions(workflowJobRepository, workflowRunRepository);
  }

  @ParameterizedTest(
      name =
          "shouldFetchJobDetailsForGivenWorkflowWhenLatestBuildIs{0}AndItHasANewJobAddedAndPreviousWorkflowRunConclusionIs{1}")
  @MethodSource(value = "getPermutationOfWorkflowRunStatusAndPreviousConclusion")
  @SneakyThrows
  void
      shouldFetchJobDetailsWhenLatestBuildIsRunningAndItHasANewJobAddedAndLastJobIsFailureConclusion(
          RunStatus latestRunStatus, RunConclusion previousConclusion) {
    final WorkflowRunDetails latestWorkflowRunDetails =
        BASE_WORKFLOWS_RUN_DETAILS_RESPONSE.getWorkflowRuns().getFirst().toBuilder()
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

    when(apiService.getForObject(
            RUN_DETAILS_URL_TEMPLATE.formatted(workflow.getRepoName(), workflow.getId()),
            ACCESS_TOKEN,
            WorkflowsRunDetailsResponse.class))
        .thenReturn(workflowsRunDetailsResponse);

    final WorkflowsJobDetailsResponse currentWorkflowsJobDetailsResponse =
        WorkflowsJobDetailsResponse.builder()
            .jobs(
                List.of(
                    BASE_JOB_DETAILS_RESPONSE.getJobs().getFirst().toBuilder()
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

    when(workflowJobRepository.findByWorkflowRunIdAndRunAttempt(anyLong(), anyInt()))
        .thenReturn(CompletableFuture.completedFuture(List.of()));
    when(apiService.getForObject(
            JOB_DETAILS_URL_TEMPLATE.formatted(
                workflow.getRepoName(), latestWorkflowRunDetails.getId()),
            ACCESS_TOKEN,
            WorkflowsJobDetailsResponse.class))
        .thenReturn(currentWorkflowsJobDetailsResponse);

    final Instant completedAt = Instant.parse("2024-09-17T06:13:21Z");

    final WorkflowsJobDetailsResponse previousWorkflowsJobDetailsResponse =
        WorkflowsJobDetailsResponse.builder()
            .jobs(
                List.of(
                    BASE_JOB_DETAILS_RESPONSE.getJobs().getFirst().toBuilder()
                        .status(COMPLETED)
                        .startedAt(Instant.parse("2024-09-17T06:11:04Z"))
                        .completedAt(completedAt)
                        .conclusion(previousConclusion)
                        .build()))
            .build();

    when(apiService.getForObject(
            JOB_DETAILS_URL_TEMPLATE.formatted(
                workflow.getRepoName(), previousWorkflowRunDetails.getId()),
            ACCESS_TOKEN,
            WorkflowsJobDetailsResponse.class))
        .thenReturn(previousWorkflowsJobDetailsResponse);
    when(workflowRunRepository.save(anyList())).thenReturn(CompletableFuture.completedFuture(null));
    when(workflowJobRepository.save(anyList())).thenReturn(CompletableFuture.completedFuture(null));

    final List<JobDetails> jobDetails =
        githubJobDetailsService.fetchJobDetails(workflow, ACCESS_TOKEN).get();

    final List<JobDetails> expectedJobDetails =
        List.of(
            JOB_DETAILS_1132386046.toBuilder()
                .activity(BUILDING)
                .lastBuildStatus(RunConclusion.getStatus(previousConclusion))
                .lastBuildTime(completedAt)
                .build(),
            JOB_DETAILS_1132386127.toBuilder()
                .activity(BUILDING)
                .lastBuildStatus(JobStatus.UNKNOWN)
                .lastBuildTime(Instant.parse("2024-09-18T06:11:37Z"))
                .build());

    assertThat(jobDetails).hasSameSizeAs(expectedJobDetails).isEqualTo(expectedJobDetails);
    final String cacheKey1 =
        createCacheKey(
            latestWorkflowRunDetails.getId(),
            workflow.getRepoName(),
            workflow.getId(),
            latestWorkflowRunDetails.getRunAttempt(),
            latestWorkflowRunDetails.getUpdatedAt());
    final String cacheKey2 =
        createCacheKey(
            previousWorkflowRunDetails.getId(),
            workflow.getRepoName(),
            workflow.getId(),
            previousWorkflowRunDetails.getRunAttempt(),
            previousWorkflowRunDetails.getUpdatedAt());
    verify(mockWorkflowJobDetailsCache).getIfPresent(cacheKey1);
    verify(mockWorkflowJobDetailsCache).getIfPresent(cacheKey2);
    verify(mockWorkflowJobDetailsCache)
        .put(
            cacheKey1,
            currentWorkflowsJobDetailsResponse.getJobs().stream()
                .map(workflowsJobDetails -> workflowsJobDetails.toWorkflowJob(workflow.getId()))
                .toList());
    verify(mockWorkflowJobDetailsCache)
        .put(
            cacheKey2,
            previousWorkflowsJobDetailsResponse.getJobs().stream()
                .map(workflowsJobDetails -> workflowsJobDetails.toWorkflowJob(workflow.getId()))
                .toList());
    verify(workflowRunRepository, times(1))
        .save(
            List.of(
                latestWorkflowRunDetails.toWorkflowRun(),
                previousWorkflowRunDetails.toWorkflowRun()));
    verify(workflowJobRepository, times(1))
        .save(
            currentWorkflowsJobDetailsResponse.getJobs().stream()
                .map(jobDetails1 -> jobDetails1.toWorkflowJob(workflow.getId()))
                .toList());
    verify(workflowJobRepository, times(1))
        .findByWorkflowRunIdAndRunAttempt(
            previousWorkflowRunDetails.getId(), previousWorkflowRunDetails.getRunAttempt());
    verifyNoMoreInteractions(workflowJobRepository, workflowRunRepository);
  }

  private String createCacheKey(
      long runId, String repoName, long workflowId, int runAttempt, Instant updatedAt) {
    return "%s_%d_%d_%d_%s"
        .formatted(repoName, workflowId, runId, runAttempt, updatedAt.getEpochSecond());
  }

  @Test
  @SneakyThrows
  void shouldNotThrowErrorIfTheFetchWorkflowsRunsCallFails() {
    when(apiService.getForObject(
            RUN_DETAILS_URL_TEMPLATE.formatted(workflow.getRepoName(), workflow.getId()),
            ACCESS_TOKEN,
            WorkflowsRunDetailsResponse.class))
        .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));
    assertThat(githubJobDetailsService.fetchJobDetails(workflow, ACCESS_TOKEN).get()).isEmpty();
    verifyNoInteractions(workflowRunRepository, workflowJobRepository);
  }

  @Test
  @SneakyThrows
  void shouldNotThrowErrorIfTheFetchJobDetailsRunsCallFails() {
    when(apiService.getForObject(
            RUN_DETAILS_URL_TEMPLATE.formatted(workflow.getRepoName(), workflow.getId()),
            ACCESS_TOKEN,
            WorkflowsRunDetailsResponse.class))
        .thenReturn(BASE_WORKFLOWS_RUN_DETAILS_RESPONSE);

    final WorkflowRunDetails workflowRunDetails =
        BASE_WORKFLOWS_RUN_DETAILS_RESPONSE.getWorkflowRuns().getFirst();
    when(workflowJobRepository.findByWorkflowRunIdAndRunAttempt(anyLong(), anyInt()))
        .thenReturn(CompletableFuture.completedFuture(List.of()));
    when(apiService.getForObject(
            JOB_DETAILS_URL_TEMPLATE.formatted(workflow.getRepoName(), workflowRunDetails.getId()),
            ACCESS_TOKEN,
            WorkflowsJobDetailsResponse.class))
        .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));
    when(workflowRunRepository.save(anyList())).thenReturn(CompletableFuture.completedFuture(null));

    assertThat(githubJobDetailsService.fetchJobDetails(workflow, ACCESS_TOKEN).get()).isEmpty();
    verify(workflowJobRepository, times(1))
        .findByWorkflowRunIdAndRunAttempt(
            workflowRunDetails.getId(), workflowRunDetails.getRunAttempt());
    verifyNoMoreInteractions(workflowJobRepository);
  }

  @Test
  @SneakyThrows
  void shouldNotFetchJobDetailsForGivenWorkflowWhenTheDetailsIsPresentInCacheWithCompletedStatus() {
    final WorkflowRunDetails latestWorkflowRunDetails =
        BASE_WORKFLOWS_RUN_DETAILS_RESPONSE.getWorkflowRuns().getFirst().toBuilder()
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

    when(apiService.getForObject(
            RUN_DETAILS_URL_TEMPLATE.formatted(workflow.getRepoName(), workflow.getId()),
            ACCESS_TOKEN,
            WorkflowsRunDetailsResponse.class))
        .thenReturn(workflowsRunDetailsResponse);

    final String cacheKey =
        createCacheKey(
            latestWorkflowRunDetails.getId(),
            workflow.getRepoName(),
            workflow.getId(),
            latestWorkflowRunDetails.getRunAttempt(),
            latestWorkflowRunDetails.getUpdatedAt());

    final List<WorkflowsJobDetails> workflowsJobDetails =
        List.of(
            BASE_JOB_DETAILS_RESPONSE.getJobs().getFirst().toBuilder()
                .status(COMPLETED)
                .conclusion(SUCCESS)
                .build());

    when(mockWorkflowJobDetailsCache.getIfPresent(cacheKey))
        .thenReturn(
            workflowsJobDetails.stream()
                .map(jobDetails -> jobDetails.toWorkflowJob(workflow.getId()))
                .toList());
    when(workflowRunRepository.save(anyList())).thenReturn(CompletableFuture.completedFuture(null));

    final List<JobDetails> jobDetails =
        githubJobDetailsService.fetchJobDetails(workflow, ACCESS_TOKEN).get();

    final List<JobDetails> expectedJobDetails =
        List.of(
            JOB_DETAILS_1132386046.toBuilder()
                .activity(SLEEPING)
                .lastBuildStatus(JobStatus.SUCCESS)
                .build());

    assertThat(jobDetails).hasSameSizeAs(expectedJobDetails).isEqualTo(expectedJobDetails);

    verify(mockWorkflowJobDetailsCache).getIfPresent(cacheKey);
    verify(mockWorkflowJobDetailsCache, never()).put(eq(cacheKey), anyList());
    verify(apiService, never())
        .getForObject(
            JOB_DETAILS_URL_TEMPLATE.formatted(
                workflow.getRepoName(), latestWorkflowRunDetails.getId()),
            ACCESS_TOKEN,
            WorkflowsJobDetailsResponse.class);
    verifyNoInteractions(workflowJobRepository);
  }
}
