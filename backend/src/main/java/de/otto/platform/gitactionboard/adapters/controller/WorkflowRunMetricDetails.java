package de.otto.platform.gitactionboard.adapters.controller;

import de.otto.platform.gitactionboard.domain.metrics.WorkflowRunMetric;
import de.otto.platform.gitactionboard.domain.workflow.RunConclusion;
import java.time.Instant;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class WorkflowRunMetricDetails {
  String workflowName;
  String repoName;
  String conclusion;
  Integer runAttempt;
  Instant startedAt;
  Instant completedAt;
  String triggeredEvent;

  public static WorkflowRunMetricDetails from(WorkflowRunMetric metric) {
    return WorkflowRunMetricDetails.builder()
        .workflowName(metric.getWorkflowName())
        .repoName(metric.getRepoName())
        .conclusion(RunConclusion.getStatus(metric.getConclusion()).name())
        .runAttempt(metric.getRunAttempt())
        .startedAt(metric.getStartedAt())
        .completedAt(metric.getCompletedAt())
        .triggeredEvent(metric.getTriggeredEvent())
        .build();
  }
}
