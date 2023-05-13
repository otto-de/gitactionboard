package de.otto.platform.gitactionboard.adapters.service.job;

import static de.otto.platform.gitactionboard.adapters.service.job.RunStatus.COMPLETED;

import com.github.benmanes.caffeine.cache.Cache;
import de.otto.platform.gitactionboard.adapters.service.ApiService;
import de.otto.platform.gitactionboard.adapters.service.job.WorkflowsJobDetailsResponse.WorkflowsJobDetails;
import de.otto.platform.gitactionboard.adapters.service.job.WorkflowsRunDetailsResponse.WorkflowRunDetails;
import de.otto.platform.gitactionboard.domain.service.JobDetailsService;
import de.otto.platform.gitactionboard.domain.workflow.JobDetails;
import de.otto.platform.gitactionboard.domain.workflow.Status;
import de.otto.platform.gitactionboard.domain.workflow.Workflow;
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
  private final ApiService apiService;
  private final Cache<String, List<WorkflowsJobDetails>> workflowJobDetailsCache;

  @Override
  @Async
  public CompletableFuture<List<JobDetails>> fetchJobDetails(
      Workflow workflow, String accessToken) {
    return CompletableFuture.supplyAsync(
            () -> {
              final List<WorkflowRunDetails> workflowsRunDetails =
                  fetchWorkflowDetails(workflow, accessToken);
              if (workflowsRunDetails.isEmpty()) return Collections.<JobDetails>emptyList();

              final WorkflowRunDetails currentWorkflowRun = workflowsRunDetails.get(0);
              final List<WorkflowsJobDetails> currentJobs =
                  fetchWorkflowJobDetails(currentWorkflowRun, workflow, accessToken);

              final long runNumber = currentWorkflowRun.getRunNumber();
              final String triggeredEvent = currentWorkflowRun.getTriggeredEvent();
              if (workflowsRunDetails.size() == 1) {
                return convertToJobDetails(runNumber, currentJobs, workflow, triggeredEvent);
              }

              final WorkflowRunDetails previousWorkflowRun = workflowsRunDetails.get(1);

              final List<WorkflowsJobDetails> previousJobs =
                  getPreviousJobDetails(
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

  private List<WorkflowsJobDetails> getPreviousJobDetails(
      Workflow workflow,
      List<WorkflowsJobDetails> currentJobs,
      WorkflowRunDetails currentWorkflowRun,
      WorkflowRunDetails previousWorkflowRun,
      String accessToken) {

    if (COMPLETED.equals(currentWorkflowRun.getStatus())) {
      return currentJobs;
    }
    return fetchPreviousJobs(workflow, currentJobs, previousWorkflowRun, accessToken);
  }

  private List<WorkflowsJobDetails> fetchPreviousJobs(
      Workflow workflow,
      List<WorkflowsJobDetails> currentJobs,
      WorkflowRunDetails previousWorkflowRun,
      String accessToken) {
    log.info("Getting previous job details for {}", workflow);

    if (isFailureConclusion(previousWorkflowRun.getConclusion())) {
      return fetchWorkflowJobDetails(previousWorkflowRun, workflow, accessToken);
    }

    return currentJobs.stream()
        .map(
            workflowsJobDetails ->
                workflowsJobDetails.toBuilder()
                    .status(COMPLETED)
                    .conclusion(RunConclusion.SUCCESS)
                    .completedAt(previousWorkflowRun.getUpdatedAt())
                    .startedAt(previousWorkflowRun.getCreatedAt())
                    .build())
        .toList();
  }

  private boolean isFailureConclusion(RunConclusion conclusion) {
    return !Status.SUCCESS.equals(RunConclusion.getStatus(conclusion));
  }

  private List<JobDetails> convertToJobDetails(
      long runNumber,
      List<WorkflowsJobDetails> currentJobs,
      Workflow workflow,
      String triggeredEvent) {
    return convertToJobDetails(runNumber, currentJobs, workflow, currentJobs, triggeredEvent);
  }

  private List<JobDetails> convertToJobDetails(
      long runNumber,
      List<WorkflowsJobDetails> currentJobs,
      Workflow workflow,
      List<WorkflowsJobDetails> previousJobs,
      String triggeredEvent) {
    log.info("Creating job details for {} for run number {}", workflow, runNumber);

    return currentJobs.stream()
        .map(
            currentJob -> {
              final WorkflowsJobDetails previousJob =
                  findJob(previousJobs, currentJob.getName(), currentJob);
              return createJobDetails(runNumber, workflow, currentJob, previousJob, triggeredEvent);
            })
        .toList();
  }

  private JobDetails createJobDetails(
      long runNumber,
      Workflow workflow,
      WorkflowsJobDetails currentJob,
      WorkflowsJobDetails previousJob,
      String triggeredEvent) {
    return JobDetails.builder()
        .id(currentJob.getId())
        .runNumber(runNumber)
        .url(currentJob.getUrl())
        .name(currentJob.getName())
        .repoName(workflow.getRepoName())
        .workflowName(workflow.getName())
        .lastBuildStatus(RunConclusion.getStatus(previousJob.getConclusion()))
        .activity(RunStatus.getActivity(currentJob.getStatus()))
        .lastBuildTime(
            Optional.ofNullable(previousJob.getCompletedAt()).orElse(previousJob.getStartedAt()))
        .triggeredEvent(triggeredEvent)
        .build();
  }

  private WorkflowsJobDetails findJob(
      List<WorkflowsJobDetails> jobs, String jobName, WorkflowsJobDetails defaultValue) {

    return jobs.stream()
        .filter(workflowsJobDetails -> jobName.equals(workflowsJobDetails.getName()))
        .findFirst()
        .orElse(defaultValue);
  }

  private List<WorkflowRunDetails> fetchWorkflowDetails(Workflow workflow, String accessToken) {
    log.info("Fetching run details for {}", workflow);

    final WorkflowsRunDetailsResponse runDetailsResponse =
        apiService.getForObject(
            String.format(
                "/%s/actions/workflows/%s/runs?per_page=2",
                workflow.getRepoName(), workflow.getId()),
            accessToken,
            WorkflowsRunDetailsResponse.class);

    return Optional.ofNullable(runDetailsResponse)
        .map(WorkflowsRunDetailsResponse::getWorkflowRuns)
        .orElse(Collections.emptyList());
  }

  private List<WorkflowsJobDetails> fetchWorkflowJobDetails(
      WorkflowRunDetails workflowRunDetails, Workflow workflow, String accessToken) {
    final String cacheKey = getCacheKey(workflow, workflowRunDetails);
    final List<WorkflowsJobDetails> cachedJobDetails =
        workflowJobDetailsCache.getIfPresent(cacheKey);

    if (shouldFetchJobDetails(cachedJobDetails)) {
      final List<WorkflowsJobDetails> jobDetails =
          getWorkflowsJobDetails(workflowRunDetails, workflow, accessToken);
      workflowJobDetailsCache.put(cacheKey, jobDetails);
      return jobDetails;
    }

    return cachedJobDetails;
  }

  private boolean shouldFetchJobDetails(List<WorkflowsJobDetails> workflowsJobDetails) {
    return Objects.isNull(workflowsJobDetails)
        || workflowsJobDetails.isEmpty()
        || anyJobNotCompleted(workflowsJobDetails);
  }

  private String getCacheKey(Workflow workflow, WorkflowRunDetails workflowRunDetails) {
    return String.format(
        "%s_%d_%d_%s",
        workflow.getRepoName(),
        workflow.getId(),
        workflowRunDetails.getId(),
        workflowRunDetails.getUpdatedAt().getEpochSecond());
  }

  private boolean anyJobNotCompleted(List<WorkflowsJobDetails> workflowsJobDetails) {
    return workflowsJobDetails.stream()
        .map(WorkflowsJobDetails::getStatus)
        .anyMatch(runStatus -> !COMPLETED.equals(runStatus));
  }

  private List<WorkflowsJobDetails> getWorkflowsJobDetails(
      WorkflowRunDetails workflowRunDetails, Workflow workflow, String accessToken) {
    log.info(
        "Fetching job details for {} with run id {} and run number {}",
        workflow,
        workflowRunDetails.getId(),
        workflowRunDetails.getRunNumber());

    final WorkflowsJobDetailsResponse jobDetailsResponse =
        apiService.getForObject(
            String.format(
                "/%s/actions/runs/%s/jobs", workflow.getRepoName(), workflowRunDetails.getId()),
            accessToken,
            WorkflowsJobDetailsResponse.class);

    return Optional.ofNullable(jobDetailsResponse)
        .map(WorkflowsJobDetailsResponse::getJobs)
        .orElse(Collections.emptyList());
  }
}
