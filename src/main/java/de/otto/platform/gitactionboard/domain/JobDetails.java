package de.otto.platform.gitactionboard.domain;

import java.time.Instant;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class JobDetails {
  int id;

  @NonNull String name;

  int runNumber;

  @NonNull String url;

  @NonNull Activity activity;

  @NonNull Status lastBuildStatus;

  @NonNull Instant lastBuildTime;

  @NonNull String repoName;

  @NonNull String workflowName;
}
