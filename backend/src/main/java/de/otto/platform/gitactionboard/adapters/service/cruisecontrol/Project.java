package de.otto.platform.gitactionboard.adapters.service.cruisecontrol;

import de.otto.platform.gitactionboard.domain.workflow.Activity;
import de.otto.platform.gitactionboard.domain.workflow.JobDetails;
import de.otto.platform.gitactionboard.domain.workflow.Status;
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

  public static Project fromJobDetails(JobDetails job) {
    return Project.builder()
        .name(job.getFormattedName())
        .activity(fromActivity(job.getActivity()))
        .lastBuildStatus(fromStatus(job.getLastBuildStatus()))
        .lastBuildLabel(String.valueOf(job.getRunNumber()))
        .lastBuildTime(job.getLastBuildTime())
        .webUrl(job.getUrl())
        .triggeredEvent(job.getTriggeredEvent())
        .build();
  }

  private static String fromStatus(Status lastBuildStatus) {
    return switch (lastBuildStatus) {
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
