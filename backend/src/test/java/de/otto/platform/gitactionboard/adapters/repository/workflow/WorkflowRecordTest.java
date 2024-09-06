package de.otto.platform.gitactionboard.adapters.repository.workflow;

import static de.otto.platform.gitactionboard.fixtures.WorkflowsFixture.getWorkflowBuilder;
import static de.otto.platform.gitactionboard.fixtures.WorkflowsFixture.getWorkflowRecordBuilder;
import static org.assertj.core.api.Assertions.assertThat;

import de.otto.platform.gitactionboard.Parallel;
import org.junit.jupiter.api.Test;

@Parallel
class WorkflowRecordTest {
  @Test
  void shouldConvertToWorkflow() {
    assertThat(getWorkflowRecordBuilder().build().toWorkflow())
        .isEqualTo(getWorkflowBuilder().build());
  }

  @Test
  void shouldGetConvertFromGiveWorkflow() {
    assertThat(WorkflowRecord.from(getWorkflowBuilder().build()))
        .isEqualTo(getWorkflowRecordBuilder().build());
  }
}
