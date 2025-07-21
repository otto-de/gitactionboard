package de.otto.platform.gitactionboard.adapters.service.cruisecontrol;

import de.otto.platform.gitactionboard.domain.workflow.Activity;
import de.otto.platform.gitactionboard.domain.workflow.JobDetails;
import de.otto.platform.gitactionboard.domain.workflow.JobStatus;
import java.time.Instant;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class Project {
  @NonNull String name;

  @NonNull String activity;

  @NonNull String lastBuildStatus;

  @NonNull String lastBuildLabel;

  @NonNull Instant lastBuildTime;

  @NonNull String webUrl;

  @NonNull String triggeredEvent;

  @NonNull String branch;

  public static Project fromJobDetails(JobDetails job) {
    return Project.builder()
        .name(job.getFormattedName())
        .activity(fromActivity(job.getActivity()))
        .lastBuildStatus(fromJobStatus(job.getLastBuildStatus()))
        .lastBuildLabel(String.valueOf(job.getRunNumber()))
        .lastBuildTime(job.getLastBuildTime())
        .webUrl(job.getUrl())
        .triggeredEvent(job.getTriggeredEvent())
        .branch(job.getBranch())
        .build();
  }

  private static String fromJobStatus(JobStatus lastBuildJobStatus) {
    return switch (lastBuildJobStatus) {
      case FAILURE -> "Failure";
      case SUCCESS -> "Success";
      case UNKNOWN -> "Unknown";
      default -> "Exception";
    };
  }

  private static String fromActivity(Activity activity) {
    return switch (activity) {
      case BUILDING -> "Building";
      case SLEEPING -> "Sleeping";
      default -> "CheckingModifications";
    };
  }
}
