package de.otto.platform.gitactionboard.domain.workflow;

import java.time.Instant;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder
public class WorkflowRun {
  long id;

  @NonNull Integer runAttempt;

  @NonNull Long workflowId;

  @NonNull RunStatus status;

  RunConclusion conclusion;

  long runNumber;

  @NonNull Instant updatedAt;

  @NonNull Instant createdAt;

  @NonNull String triggeredEvent;
}
