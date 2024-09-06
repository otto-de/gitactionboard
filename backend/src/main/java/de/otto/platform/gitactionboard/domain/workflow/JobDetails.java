package de.otto.platform.gitactionboard.domain.workflow;

import java.time.Instant;
import java.util.Optional;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class JobDetails {
  long id;

  @NonNull String name;

  long runNumber;

  @NonNull String url;

  @NonNull Activity activity;

  @NonNull JobStatus lastBuildStatus;

  @NonNull Instant lastBuildTime;

  @NonNull String repoName;

  @NonNull String workflowName;

  @NonNull String triggeredEvent;

  @NonNull Integer runAttempt;

  public static JobDetails from(
      long runNumber,
      Workflow workflow,
      WorkflowJob currentJob,
      WorkflowJob previousJob,
      String triggeredEvent) {
    return JobDetails.builder()
        .id(currentJob.getId())
        .runNumber(runNumber)
        .url(currentJob.getUrl())
        .name(currentJob.getName())
        .repoName(workflow.getRepoName())
        .workflowName(workflow.getName())
        .lastBuildStatus(RunConclusion.getStatus(previousJob.getConclusion()))
        .activity(currentJob.getStatus().getActivity())
        .lastBuildTime(
            Optional.ofNullable(previousJob.getCompletedAt()).orElse(previousJob.getStartedAt()))
        .triggeredEvent(triggeredEvent)
        .runAttempt(currentJob.getRunAttempt())
        .build();
  }

  public String getFormattedName() {
    return String.join(" :: ", repoName, workflowName, name);
  }
}
