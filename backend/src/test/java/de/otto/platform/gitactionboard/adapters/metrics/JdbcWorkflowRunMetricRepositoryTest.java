package de.otto.platform.gitactionboard.adapters.metrics;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import de.otto.platform.gitactionboard.domain.metrics.WorkflowRunMetric;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JdbcWorkflowRunMetricRepositoryTest {
  private static final Duration DURATION = Duration.ofSeconds(10);

  @Mock private JdbcWorkflowRunMetricCurdRepository jdbcWorkflowRunMetricCurdRepository;

  @Mock private WorkflowRunMetricRecord workflowRunMetricRecord1;

  @Mock private WorkflowRunMetricRecord workflowRunMetricRecord2;

  @Mock private WorkflowRunMetric workflowRunMetric1;

  @Mock private WorkflowRunMetric workflowRunMetric2;

  private JdbcWorkflowRunMetricRepository jdbcWorkflowRunMetricRepository;

  @BeforeEach
  void setUp() {
    jdbcWorkflowRunMetricRepository =
        new JdbcWorkflowRunMetricRepository(jdbcWorkflowRunMetricCurdRepository);
  }

  @Test
  void shouldFindWorkflowRunMetricsForGivenWorkflowIdsAndGivenTimeRange() {
    when(jdbcWorkflowRunMetricCurdRepository
            .findAllByWorkflowIdsAndConclusionIsNotNullAndStartedAtBetweenOrderByStartedAtDesc(
                anySet(), anyString(), anyString()))
        .thenReturn(Stream.of(workflowRunMetricRecord1, workflowRunMetricRecord2));

    when(workflowRunMetricRecord1.toWorkflowRunMetric()).thenReturn(workflowRunMetric1);
    when(workflowRunMetricRecord2.toWorkflowRunMetric()).thenReturn(workflowRunMetric2);

    final Instant from = Instant.now().minus(1, ChronoUnit.DAYS);
    final Instant to = Instant.now();

    assertThat(jdbcWorkflowRunMetricRepository.getWorkflowRunMetrics(Set.of(1L, 2L), from, to))
        .succeedsWithin(DURATION)
        .asInstanceOf(InstanceOfAssertFactories.LIST)
        .isEqualTo(List.of(workflowRunMetric1, workflowRunMetric2));
  }
}
