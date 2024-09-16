package de.otto.platform.gitactionboard.domain.metrics;

import de.otto.platform.gitactionboard.domain.workflow.RunConclusion;
import java.time.Instant;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class WorkflowRunMetric {
  String workflowName;
  String repoName;
  RunConclusion conclusion;
  Integer runAttempt;
  Instant startedAt;
  Instant completedAt;
  String triggeredEvent;
}
