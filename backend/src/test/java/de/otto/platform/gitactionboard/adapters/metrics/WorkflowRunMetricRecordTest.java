package de.otto.platform.gitactionboard.adapters.metrics;

import static de.otto.platform.gitactionboard.fixtures.WorkflowRunMetricFixture.getWorkflowRunMetricBuilder;
import static de.otto.platform.gitactionboard.fixtures.WorkflowRunMetricFixture.getWorkflowRunMetricRecordBuilder;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class WorkflowRunMetricRecordTest {
  @Test
  void shouldConvertToWorkflowRunMetric() {
    assertThat(getWorkflowRunMetricRecordBuilder().build().toWorkflowRunMetric())
        .isEqualTo(getWorkflowRunMetricBuilder().build());
  }
}
