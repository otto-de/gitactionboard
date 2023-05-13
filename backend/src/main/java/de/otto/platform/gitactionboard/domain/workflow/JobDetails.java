package de.otto.platform.gitactionboard.domain.workflow;

import java.time.Instant;
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

  @NonNull Status lastBuildStatus;

  @NonNull Instant lastBuildTime;

  @NonNull String repoName;

  @NonNull String workflowName;

  @NonNull String triggeredEvent;

  public String getFormattedName() {
    return String.join(" :: ", repoName, workflowName, name);
  }
}
