package de.otto.platform.gitactionboard.adapters.controller;

import static de.otto.platform.gitactionboard.fixtures.WorkflowRunMetricFixture.getWorkflowRunMetricBuilder;
import static de.otto.platform.gitactionboard.fixtures.WorkflowRunMetricFixture.getWorkflowRunMetricDetailsBuilder;
import static org.assertj.core.api.Assertions.assertThat;

import de.otto.platform.gitactionboard.domain.metrics.WorkflowRunMetric;
import org.junit.jupiter.api.Test;

class WorkflowRunMetricDetailsTest {
  @Test
  void shouldConvertFromWorkflowRunMetric() {
    final WorkflowRunMetric workflowRunMetric = getWorkflowRunMetricBuilder().build();

    assertThat(WorkflowRunMetricDetails.from(workflowRunMetric))
        .isEqualTo(getWorkflowRunMetricDetailsBuilder().build());
  }
}
