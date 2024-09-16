package de.otto.platform.gitactionboard.domain.metrics;

import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface WorkflowRunMetricRepository {
  CompletableFuture<List<WorkflowRunMetric>> getWorkflowRunMetrics(
      Set<Long> workflowIds, Instant from, Instant to);
}
