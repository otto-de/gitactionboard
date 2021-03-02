package de.otto.platform.gitactionboard.adapters.service.job;

import de.otto.platform.gitactionboard.adapters.service.job.WorkflowsJobDetailsResponse.WorkflowsJobDetails;
import de.otto.platform.gitactionboard.adapters.service.job.WorkflowsRunDetailsResponse.WorkflowRunDetails;
import de.otto.platform.gitactionboard.domain.JobDetails;
import de.otto.platform.gitactionboard.domain.Status;
import de.otto.platform.gitactionboard.domain.Workflow;
import de.otto.platform.gitactionboard.domain.service.JobDetailsService;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class GithubJobDetailsService implements JobDetailsService {
  private final RestTemplate restTemplate;

  @Override
  @Async
  public CompletableFuture<List<JobDetails>> fetchJobDetails(Workflow workflow) {
    return CompletableFuture.supplyAsync(
            () -> {
              final List<WorkflowRunDetails> workflowsRunDetails = fetchWorkflowDetails(workflow);
              if (workflowsRunDetails.size() == 0) return Collections.<JobDetails>emptyList();

              final WorkflowRunDetails currentWorkflowRun = workflowsRunDetails.get(0);
              final List<WorkflowsJobDetails> currentJobs =
                  fetchWorkflowJobDetails(currentWorkflowRun, workflow);

              final int runNumber = currentWorkflowRun.getRunNumber();
              if (workflowsRunDetails.size() == 1) {
                return convertToJobDetails(runNumber, currentJobs, workflow);
              }

              final WorkflowRunDetails previousWorkflowRun = workflowsRunDetails.get(1);

              final List<WorkflowsJobDetails> previousJobs =
                  getPreviousJobDetails(
                      workflow, currentJobs, currentWorkflowRun, previousWorkflowRun);

              return convertToJobDetails(runNumber, currentJobs, workflow, previousJobs);
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
      WorkflowRunDetails previousWorkflowRun) {

    if (RunStatus.COMPLETED.equals(currentWorkflowRun.getStatus())) {
      return currentJobs;
    }
    return fetchPreviousJobs(workflow, currentJobs, previousWorkflowRun);
  }

  private List<WorkflowsJobDetails> fetchPreviousJobs(
      Workflow workflow,
      List<WorkflowsJobDetails> currentJobs,
      WorkflowRunDetails previousWorkflowRun) {
    log.info("Getting previous job details for {}", workflow);

    if (isFailureConclusion(previousWorkflowRun.getConclusion())) {
      return fetchWorkflowJobDetails(previousWorkflowRun, workflow);
    }

    return currentJobs.stream()
        .map(
            workflowsJobDetails ->
                workflowsJobDetails.toBuilder()
                    .status(RunStatus.COMPLETED)
                    .conclusion(RunConclusion.SUCCESS)
                    .completedAt(previousWorkflowRun.getUpdatedAt())
                    .startedAt(previousWorkflowRun.getCreatedAt())
                    .build())
        .collect(Collectors.toUnmodifiableList());
  }

  private boolean isFailureConclusion(RunConclusion conclusion) {
    return !Status.SUCCESS.equals(RunConclusion.getStatus(conclusion));
  }

  private List<JobDetails> convertToJobDetails(
      int runNumber, List<WorkflowsJobDetails> currentJobs, Workflow workflow) {
    return convertToJobDetails(runNumber, currentJobs, workflow, currentJobs);
  }

  private List<JobDetails> convertToJobDetails(
      int runNumber,
      List<WorkflowsJobDetails> currentJobs,
      Workflow workflow,
      List<WorkflowsJobDetails> previousJobs) {
    log.info("Creating job details for {} for run number {}", workflow, runNumber);

    return currentJobs.stream()
        .map(
            currentJob -> {
              final WorkflowsJobDetails previousJob =
                  findJob(previousJobs, currentJob.getName(), currentJob);
              return createJobDetails(runNumber, workflow, currentJob, previousJob);
            })
        .collect(Collectors.toUnmodifiableList());
  }

  private JobDetails createJobDetails(
      int runNumber,
      Workflow workflow,
      WorkflowsJobDetails currentJob,
      WorkflowsJobDetails previousJob) {
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
        .build();
  }

  private WorkflowsJobDetails findJob(
      List<WorkflowsJobDetails> jobs, String jobName, WorkflowsJobDetails defaultValue) {

    return jobs.stream()
        .filter(workflowsJobDetails -> jobName.equals(workflowsJobDetails.getName()))
        .findFirst()
        .orElse(defaultValue);
  }

  private List<WorkflowRunDetails> fetchWorkflowDetails(Workflow workflow) {
    log.info("Fetching run details for {}", workflow);

    final WorkflowsRunDetailsResponse runDetailsResponse =
        restTemplate.getForObject(
            String.format(
                "/%s/actions/workflows/%s/runs?per_page=2",
                workflow.getRepoName(), workflow.getId()),
            WorkflowsRunDetailsResponse.class);

    return Optional.ofNullable(runDetailsResponse)
        .map(WorkflowsRunDetailsResponse::getWorkflowRuns)
        .orElse(Collections.emptyList());
  }

  private List<WorkflowsJobDetails> fetchWorkflowJobDetails(
      WorkflowRunDetails workflowRunDetails, Workflow workflow) {
    log.info(
        "Fetching job details for {} with run id {} and run number {}",
        workflow,
        workflowRunDetails.getId(),
        workflowRunDetails.getRunNumber());

    final WorkflowsJobDetailsResponse jobDetailsResponse =
        restTemplate.getForObject(
            String.format(
                "/%s/actions/runs/%s/jobs", workflow.getRepoName(), workflowRunDetails.getId()),
            WorkflowsJobDetailsResponse.class);

    return Optional.ofNullable(jobDetailsResponse)
        .map(WorkflowsJobDetailsResponse::getJobs)
        .orElse(Collections.emptyList());
  }
}
