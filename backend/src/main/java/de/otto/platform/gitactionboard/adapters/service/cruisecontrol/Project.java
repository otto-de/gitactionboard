package de.otto.platform.gitactionboard.adapters.service.cruisecontrol;

import de.otto.platform.gitactionboard.domain.Activity;
import de.otto.platform.gitactionboard.domain.JobDetails;
import de.otto.platform.gitactionboard.domain.Status;
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

  public static Project fromJobDetails(JobDetails job) {
    return Project.builder()
        .name(getName(job))
        .activity(fromActivity(job.getActivity()))
        .lastBuildStatus(fromStatus(job.getLastBuildStatus()))
        .lastBuildLabel(String.valueOf(job.getRunNumber()))
        .lastBuildTime(job.getLastBuildTime())
        .webUrl(job.getUrl())
        .build();
  }

  private static String fromStatus(Status lastBuildStatus) {
    switch (lastBuildStatus) {
      case FAILURE:
        return "Failure";
      case SUCCESS:
        return "Success";
      case UNKNOWN:
        return "Unknown";
      default:
        return "Exception";
    }
  }

  private static String fromActivity(Activity activity) {
    switch (activity) {
      case BUILDING:
        return "Building";
      case SLEEPING:
        return "Sleeping";
      default:
        return "CheckingModifications";
    }
  }

  private static String getName(JobDetails job) {
    return String.join(" :: ", job.getRepoName(), job.getWorkflowName(), job.getName());
  }
}
