package de.otto.platform.gitactionboard.adapters.service.job;

import static de.otto.platform.gitactionboard.domain.workflow.RunConclusion.SUCCESS;
import static de.otto.platform.gitactionboard.domain.workflow.RunStatus.COMPLETED;

import com.github.benmanes.caffeine.cache.Cache;
import de.otto.platform.gitactionboard.adapters.service.GithubApiService;
import de.otto.platform.gitactionboard.adapters.service.job.WorkflowsRunDetailsResponse.WorkflowRunDetails;
import de.otto.platform.gitactionboard.domain.repository.WorkflowJobRepository;
import de.otto.platform.gitactionboard.domain.repository.WorkflowRunRepository;
import de.otto.platform.gitactionboard.domain.service.JobDetailsService;
import de.otto.platform.gitactionboard.domain.workflow.JobDetails;
import de.otto.platform.gitactionboard.domain.workflow.JobStatus;
import de.otto.platform.gitactionboard.domain.workflow.RunConclusion;
import de.otto.platform.gitactionboard.domain.workflow.Workflow;
import de.otto.platform.gitactionboard.domain.workflow.WorkflowJob;
import de.otto.platform.gitactionboard.domain.workflow.WorkflowRun;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class GithubJobDetailsService implements JobDetailsService {
  private final GithubApiService apiService;
  private final Cache<String, List<WorkflowJob>> workflowJobDetailsCache;
  private final WorkflowRunRepository workflowRunRepository;
  private final WorkflowJobRepository workflowJobRepository;

  @Override
  @Async
  public CompletableFuture<List<JobDetails>> fetchJobDetails(
      Workflow workflow, String accessToken) {
    return CompletableFuture.supplyAsync(
            () -> {
              final List<WorkflowRun> workflowsRunDetails =
                  fetchWorkflowRuns(workflow, accessToken);
              if (workflowsRunDetails.isEmpty()) return Collections.<JobDetails>emptyList();

              final WorkflowRun currentWorkflowRun = workflowsRunDetails.getFirst();
              final List<WorkflowJob> currentJobs =
                  fetchWorkflowJobs(currentWorkflowRun, workflow, accessToken);

              final long runNumber = currentWorkflowRun.getRunNumber();
              final String triggeredEvent = currentWorkflowRun.getTriggeredEvent();
              if (workflowsRunDetails.size() == 1) {
                return convertToJobDetails(runNumber, currentJobs, workflow, triggeredEvent);
              }

              final WorkflowRun previousWorkflowRun = workflowsRunDetails.get(1);

              final List<WorkflowJob> previousJobs =
                  getPreviousJobs(
                      workflow, currentJobs, currentWorkflowRun, previousWorkflowRun, accessToken);

              return convertToJobDetails(
                  runNumber, currentJobs, workflow, previousJobs, triggeredEvent);
            })
        .exceptionally(
            throwable -> {
              log.error(throwable.getMessage(), throwable);
              return Collections.emptyList();
            });
  }

  private List<WorkflowJob> getPreviousJobs(
      Workflow workflow,
      List<WorkflowJob> currentJobs,
      WorkflowRun currentWorkflowRun,
      WorkflowRun previousWorkflowRun,
      String accessToken) {

    if (COMPLETED.equals(currentWorkflowRun.getStatus())) {
      return currentJobs;
    }
    return fetchPreviousJobs(workflow, currentJobs, previousWorkflowRun, accessToken);
  }

  private List<WorkflowJob> fetchPreviousJobs(
      Workflow workflow,
      List<WorkflowJob> currentJobs,
      WorkflowRun previousWorkflowRun,
      String accessToken) {
    log.info("Getting previous job details for {}", workflow);

    if (isFailureConclusion(previousWorkflowRun.getConclusion())) {
      return fetchWorkflowJobs(previousWorkflowRun, workflow, accessToken);
    }

    return currentJobs.stream()
        .map(
            workflowsJobDetails ->
                workflowsJobDetails.toBuilder()
                    .status(COMPLETED)
                    .conclusion(SUCCESS)
                    .completedAt(previousWorkflowRun.getUpdatedAt())
                    .startedAt(previousWorkflowRun.getCreatedAt())
                    .build())
        .toList();
  }

  private boolean isFailureConclusion(RunConclusion conclusion) {
    return !JobStatus.SUCCESS.equals(RunConclusion.getStatus(conclusion));
  }

  private List<JobDetails> convertToJobDetails(
      long runNumber, List<WorkflowJob> currentJobs, Workflow workflow, String triggeredEvent) {
    return convertToJobDetails(runNumber, currentJobs, workflow, currentJobs, triggeredEvent);
  }

  private List<JobDetails> convertToJobDetails(
      long runNumber,
      List<WorkflowJob> currentJobs,
      Workflow workflow,
      List<WorkflowJob> previousJobs,
      String triggeredEvent) {
    log.info("Creating job details for {} for run number {}", workflow, runNumber);

    return currentJobs.stream()
        .map(
            currentJob -> {
              final WorkflowJob previousJob =
                  findJob(previousJobs, currentJob.getName(), currentJob);
              return JobDetails.from(runNumber, workflow, currentJob, previousJob, triggeredEvent);
            })
        .toList();
  }

  private WorkflowJob findJob(List<WorkflowJob> jobs, String jobName, WorkflowJob defaultValue) {
    return jobs.stream()
        .filter(workflowsJobDetails -> jobName.equals(workflowsJobDetails.getName()))
        .findFirst()
        .orElse(defaultValue);
  }

  private List<WorkflowRun> fetchWorkflowRuns(Workflow workflow, String accessToken) {
    log.info("Fetching run details for {}", workflow);

    final WorkflowsRunDetailsResponse runDetailsResponse =
        apiService.getForObject(
            "/%s/actions/workflows/%s/runs?per_page=2"
                .formatted(workflow.getRepoName(), workflow.getId()),
            accessToken,
            WorkflowsRunDetailsResponse.class);

    return Optional.ofNullable(runDetailsResponse)
        .map(WorkflowsRunDetailsResponse::getWorkflowRuns)
        .map(
            workflowRunDetails ->
                workflowRunDetails.stream().map(WorkflowRunDetails::toWorkflowRun).toList())
        .map(this::persistWorkflowRuns)
        .map(CompletableFuture::join)
        .orElse(Collections.emptyList());
  }

  private CompletableFuture<List<WorkflowRun>> persistWorkflowRuns(List<WorkflowRun> workflowRuns) {
    return workflowRunRepository.save(workflowRuns).thenApply(unused -> workflowRuns);
  }

  private List<WorkflowJob> fetchWorkflowJobs(
      WorkflowRun workflowRunDetails, Workflow workflow, String accessToken) {
    final String cacheKey = getCacheKey(workflow, workflowRunDetails);
    final List<WorkflowJob> cachedWorkflowJobs =
        getCachedWorkflowJobs(cacheKey, workflowRunDetails);

    if (shouldFetchWorkflowJobs(cachedWorkflowJobs)) {
      final List<WorkflowJob> workflowJobs =
          getWorkflowsJobs(workflowRunDetails, workflow, accessToken);
      workflowJobDetailsCache.put(cacheKey, workflowJobs);
      return workflowJobs;
    }

    return cachedWorkflowJobs;
  }

  private List<WorkflowJob> getCachedWorkflowJobs(String cacheKey, WorkflowRun workflowRunDetails) {
    final List<WorkflowJob> inMemoryCachedWorkflowJobs =
        workflowJobDetailsCache.getIfPresent(cacheKey);
    if (Objects.isNull(inMemoryCachedWorkflowJobs) || inMemoryCachedWorkflowJobs.isEmpty()) {
      return workflowJobRepository
          .findByWorkflowRunIdAndRunAttempt(
              workflowRunDetails.getId(), workflowRunDetails.getRunAttempt())
          .join();
    }

    return inMemoryCachedWorkflowJobs;
  }

  private boolean shouldFetchWorkflowJobs(List<WorkflowJob> workflowJobs) {
    return Objects.isNull(workflowJobs)
        || workflowJobs.isEmpty()
        || anyJobNotCompleted(workflowJobs);
  }

  private String getCacheKey(Workflow workflow, WorkflowRun workflowRun) {
    return "%s_%d_%d_%d_%s"
        .formatted(
            workflow.getRepoName(),
            workflow.getId(),
            workflowRun.getId(),
            workflowRun.getRunAttempt(),
            workflowRun.getUpdatedAt().getEpochSecond());
  }

  private boolean anyJobNotCompleted(List<WorkflowJob> workflowJobs) {
    return workflowJobs.stream()
        .map(WorkflowJob::getStatus)
        .anyMatch(runStatus -> !COMPLETED.equals(runStatus));
  }

  private List<WorkflowJob> getWorkflowsJobs(
      WorkflowRun workflowRun, Workflow workflow, String accessToken) {
    log.info(
        "Fetching job details for {} with run id {} and run number {}",
        workflow,
        workflowRun.getId(),
        workflowRun.getRunNumber());

    final WorkflowsJobDetailsResponse jobDetailsResponse =
        apiService.getForObject(
            "/%s/actions/runs/%s/jobs".formatted(workflow.getRepoName(), workflowRun.getId()),
            accessToken,
            WorkflowsJobDetailsResponse.class);

    return Optional.ofNullable(jobDetailsResponse)
        .map(WorkflowsJobDetailsResponse::getJobs)
        .map(
            workflowsJobDetails ->
                workflowsJobDetails.stream()
                    .map(jobDetails -> jobDetails.toWorkflowJob(workflow.getId()))
                    .toList())
        .map(this::persistWorkflowJobs)
        .map(CompletableFuture::join)
        .orElse(Collections.emptyList());
  }

  private CompletableFuture<List<WorkflowJob>> persistWorkflowJobs(List<WorkflowJob> workflowJobs) {
    return workflowJobRepository.save(workflowJobs).thenApply(unused -> workflowJobs);
  }
}
