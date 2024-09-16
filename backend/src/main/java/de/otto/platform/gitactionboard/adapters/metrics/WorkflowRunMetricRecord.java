package de.otto.platform.gitactionboard.adapters.metrics;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.otto.platform.gitactionboard.domain.metrics.WorkflowRunMetric;
import de.otto.platform.gitactionboard.domain.workflow.RunConclusion;
import java.time.Instant;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class WorkflowRunMetricRecord {
  @JsonProperty("workflow_name")
  String workflowName;

  @JsonProperty("repo_name")
  String repoName;

  RunConclusion conclusion;

  @JsonProperty("run_attempt")
  Integer runAttempt;

  @JsonProperty("started_at")
  Instant startedAt;

  @JsonProperty("completed_at")
  Instant completedAt;

  @JsonProperty("triggered_event")
  String triggeredEvent;

  public WorkflowRunMetric toWorkflowRunMetric() {
    return WorkflowRunMetric.builder()
        .workflowName(workflowName)
        .repoName(repoName)
        .conclusion(conclusion)
        .runAttempt(runAttempt)
        .startedAt(startedAt)
        .completedAt(completedAt)
        .triggeredEvent(triggeredEvent)
        .build();
  }
}
