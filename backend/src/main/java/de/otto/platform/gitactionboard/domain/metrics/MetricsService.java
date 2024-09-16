package de.otto.platform.gitactionboard.domain.metrics;

import de.otto.platform.gitactionboard.domain.repository.WorkflowRepository;
import de.otto.platform.gitactionboard.domain.workflow.Workflow;
import java.time.Instant;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MetricsService {
  private final WorkflowRunMetricRepository workflowRunMetricRepository;
  private final WorkflowRepository workflowRepository;

  @Cacheable(cacheNames = "workflowRunMetrics", sync = true)
  public Map<String, List<WorkflowRunMetric>> getWorkflowRunMetrics(
      String repoName, Instant from, Instant to) {

    return workflowRepository
        .findByRepoName(repoName)
        .thenCompose(workflows -> fetchWorkflowRunMetrics(workflows, from, to))
        .join()
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  private CompletableFuture<Stream<SimpleImmutableEntry<String, List<WorkflowRunMetric>>>>
      fetchWorkflowRunMetrics(List<Workflow> workflows, Instant from, Instant to) {
    if (workflows.isEmpty()) {
      return CompletableFuture.completedFuture(Stream.empty());
    }
    final Set<Long> workflowIds =
        workflows.stream().map(Workflow::getId).collect(Collectors.toSet());

    return workflowRunMetricRepository
        .getWorkflowRunMetrics(workflowIds, from, to)
        .thenApply(
            workflowRunMetrics -> {
              final Map<String, List<WorkflowRunMetric>> workflowRunMetricsMap =
                  workflowRunMetrics.stream()
                      .collect(Collectors.groupingBy(WorkflowRunMetric::getWorkflowName));

              return workflows.stream()
                  .map(
                      workflow ->
                          new SimpleImmutableEntry<>(
                              workflow.getName(),
                              workflowRunMetricsMap.getOrDefault(workflow.getName(), List.of())));
            });
  }
}
