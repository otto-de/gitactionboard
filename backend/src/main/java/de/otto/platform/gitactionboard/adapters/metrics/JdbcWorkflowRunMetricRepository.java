package de.otto.platform.gitactionboard.adapters.metrics;

import de.otto.platform.gitactionboard.domain.metrics.WorkflowRunMetric;
import de.otto.platform.gitactionboard.domain.metrics.WorkflowRunMetricRepository;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
@RequiredArgsConstructor
public class JdbcWorkflowRunMetricRepository implements WorkflowRunMetricRepository {
  private final JdbcWorkflowRunMetricCurdRepository workflowRunMetricCurdRepository;

  @Override
  public CompletableFuture<List<WorkflowRunMetric>> getWorkflowRunMetrics(
      Set<Long> workflowIds, Instant from, Instant to) {
    log.info(
        "Fetching workflow run metrics for workflow ids {} from {} to {}", workflowIds, from, to);
    return CompletableFuture.supplyAsync(
            () ->
                workflowRunMetricCurdRepository
                    .findAllByWorkflowIdsAndConclusionIsNotNullAndStartedAtBetweenOrderByStartedAtDesc(
                        workflowIds, from.toString(), to.toString()))
        .thenApply(
            workflowRunMetricRecordStream ->
                workflowRunMetricRecordStream
                    .map(WorkflowRunMetricRecord::toWorkflowRunMetric)
                    .toList());
  }
}
