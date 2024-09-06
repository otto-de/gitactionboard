package de.otto.platform.gitactionboard.domain.workflow;

import java.time.Instant;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class WorkflowJob {
  long id;

  @NonNull String name;

  @NonNull RunStatus status;

  RunConclusion conclusion;

  @NonNull Instant startedAt;

  Instant completedAt;

  @NonNull Long workflowId;

  @NonNull Long workflowRunId;

  @NonNull String url;

  @NonNull Integer runAttempt;
}
