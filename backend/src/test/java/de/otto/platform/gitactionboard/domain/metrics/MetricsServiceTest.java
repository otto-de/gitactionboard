package de.otto.platform.gitactionboard.domain.metrics;

import static de.otto.platform.gitactionboard.fixtures.JobFixture.LAST_BUILD_STARTED_AT;
import static de.otto.platform.gitactionboard.fixtures.JobFixture.LAST_BUILD_TIME;
import static de.otto.platform.gitactionboard.fixtures.WorkflowsFixture.BUILD_AND_DEPLOYMENT_WORKFLOW_NAME;
import static de.otto.platform.gitactionboard.fixtures.WorkflowsFixture.REPO_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import de.otto.platform.gitactionboard.Sequential;
import de.otto.platform.gitactionboard.domain.repository.WorkflowRepository;
import de.otto.platform.gitactionboard.domain.workflow.Workflow;
import java.time.Instant;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Sequential
@ExtendWith(MockitoExtension.class)
class MetricsServiceTest {
  private static final String WORKFLOW_NAME_2 =
      "%s_2".formatted(BUILD_AND_DEPLOYMENT_WORKFLOW_NAME);
  private static final long WORKFLOW_1_ID = 1L;
  private static final long WORKFLOW_2_ID = 2L;

  @Mock private WorkflowRunMetric workflowRunMetric1;
  @Mock private WorkflowRunMetric workflowRunMetric2;
  @Mock private WorkflowRunMetric workflowRunMetric3;
  @Mock private Workflow workflow1;
  @Mock private Workflow workflow2;

  @Mock private WorkflowRunMetricRepository workflowRunMetricRepository;
  @Mock private WorkflowRepository workflowRepository;

  private MetricsService metricsService;

  @BeforeEach
  void setUp() {
    metricsService = new MetricsService(workflowRunMetricRepository, workflowRepository);
  }

  @Test
  void shouldFindWorkflowRunsForGivenRepoAndTimeRangeAndGroupByWorkflowName() {
    when(workflow1.getId()).thenReturn(WORKFLOW_1_ID);
    when(workflow1.getName()).thenReturn(BUILD_AND_DEPLOYMENT_WORKFLOW_NAME);
    when(workflowRunMetric1.getWorkflowName()).thenReturn(BUILD_AND_DEPLOYMENT_WORKFLOW_NAME);
    when(workflowRunMetric3.getWorkflowName()).thenReturn(BUILD_AND_DEPLOYMENT_WORKFLOW_NAME);

    when(workflow2.getId()).thenReturn(WORKFLOW_2_ID);
    when(workflow2.getName()).thenReturn(WORKFLOW_NAME_2);
    when(workflowRunMetric2.getWorkflowName()).thenReturn(WORKFLOW_NAME_2);

    when(workflowRepository.findByRepoName(anyString()))
        .thenReturn(CompletableFuture.completedFuture(List.of(workflow1, workflow2)));

    when(workflowRunMetricRepository.getWorkflowRunMetrics(
            eq(Set.of(WORKFLOW_1_ID, WORKFLOW_2_ID)), any(Instant.class), any(Instant.class)))
        .thenReturn(
            CompletableFuture.completedFuture(
                List.of(workflowRunMetric1, workflowRunMetric2, workflowRunMetric3)));

    assertThat(
            metricsService.getWorkflowRunMetrics(REPO_NAME, LAST_BUILD_STARTED_AT, LAST_BUILD_TIME))
        .hasSize(2)
        .containsEntry(
            BUILD_AND_DEPLOYMENT_WORKFLOW_NAME, List.of(workflowRunMetric1, workflowRunMetric3))
        .containsEntry(WORKFLOW_NAME_2, List.of(workflowRunMetric2));

    verify(workflowRepository, times(1)).findByRepoName(REPO_NAME);
    verify(workflowRunMetricRepository, times(1))
        .getWorkflowRunMetrics(
            Set.of(WORKFLOW_1_ID, WORKFLOW_2_ID), LAST_BUILD_STARTED_AT, LAST_BUILD_TIME);
  }

  @Test
  void shouldReturnEmptyForSpecificWorkflowIfRunInformationIsNotAvailableForGivenTimeRange() {
    when(workflow1.getId()).thenReturn(WORKFLOW_1_ID);
    when(workflow1.getName()).thenReturn(BUILD_AND_DEPLOYMENT_WORKFLOW_NAME);
    when(workflowRunMetric1.getWorkflowName()).thenReturn(BUILD_AND_DEPLOYMENT_WORKFLOW_NAME);
    when(workflowRunMetric3.getWorkflowName()).thenReturn(BUILD_AND_DEPLOYMENT_WORKFLOW_NAME);

    when(workflow2.getId()).thenReturn(WORKFLOW_2_ID);
    when(workflow2.getName()).thenReturn(WORKFLOW_NAME_2);

    when(workflowRepository.findByRepoName(anyString()))
        .thenReturn(CompletableFuture.completedFuture(List.of(workflow1, workflow2)));

    when(workflowRunMetricRepository.getWorkflowRunMetrics(
            eq(Set.of(WORKFLOW_1_ID, WORKFLOW_2_ID)), any(Instant.class), any(Instant.class)))
        .thenReturn(
            CompletableFuture.completedFuture(List.of(workflowRunMetric1, workflowRunMetric3)));

    assertThat(
            metricsService.getWorkflowRunMetrics(REPO_NAME, LAST_BUILD_STARTED_AT, LAST_BUILD_TIME))
        .hasSize(2)
        .containsEntry(
            BUILD_AND_DEPLOYMENT_WORKFLOW_NAME, List.of(workflowRunMetric1, workflowRunMetric3))
        .containsEntry(WORKFLOW_NAME_2, List.of());

    verify(workflowRepository, times(1)).findByRepoName(REPO_NAME);
    verify(workflowRunMetricRepository, times(1))
        .getWorkflowRunMetrics(
            Set.of(WORKFLOW_1_ID, WORKFLOW_2_ID), LAST_BUILD_STARTED_AT, LAST_BUILD_TIME);
  }

  @Test
  void shouldReturnEmptyWhenUnableToFindWorkflowsForGivenRepo() {
    when(workflowRepository.findByRepoName(anyString()))
        .thenReturn(CompletableFuture.completedFuture(List.of()));

    assertThat(
            metricsService.getWorkflowRunMetrics(REPO_NAME, LAST_BUILD_STARTED_AT, LAST_BUILD_TIME))
        .isEmpty();

    verify(workflowRepository, times(1)).findByRepoName(REPO_NAME);
    verifyNoInteractions(workflowRunMetricRepository);
  }
}
