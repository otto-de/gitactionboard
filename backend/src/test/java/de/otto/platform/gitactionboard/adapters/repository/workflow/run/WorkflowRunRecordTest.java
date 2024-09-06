package de.otto.platform.gitactionboard.adapters.repository.workflow.run;

import static de.otto.platform.gitactionboard.fixtures.WorkflowRunFixture.getWorkflowRunBuilder;
import static de.otto.platform.gitactionboard.fixtures.WorkflowRunFixture.getWorkflowRunRecordBuilder;
import static org.assertj.core.api.Assertions.assertThat;

import de.otto.platform.gitactionboard.domain.workflow.RunConclusion;
import de.otto.platform.gitactionboard.domain.workflow.RunStatus;
import java.util.Optional;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;

class WorkflowRunRecordTest {
  @ParameterizedTest
  @EnumSource(RunStatus.class)
  void shouldGetInstantiatedFromGivenWorkflowRun(RunStatus status) {
    assertThat(WorkflowRunRecord.from(getWorkflowRunBuilder().status(status).build()))
        .isEqualTo(getWorkflowRunRecordBuilder().status(status.name()).build());
  }

  @ParameterizedTest
  @EnumSource(RunConclusion.class)
  @NullSource
  void shouldGetInstantiatedFromGivenWorkflowRun(RunConclusion conclusion) {
    assertThat(WorkflowRunRecord.from(getWorkflowRunBuilder().conclusion(conclusion).build()))
        .isEqualTo(
            getWorkflowRunRecordBuilder()
                .conclusion(Optional.ofNullable(conclusion).map(Enum::name).orElse(null))
                .build());
  }

  @ParameterizedTest
  @EnumSource(RunStatus.class)
  void shouldGetConvertedToWorkflowRun(RunStatus status) {
    assertThat(getWorkflowRunRecordBuilder().status(status.name()).build().toWorkflowRun())
        .isEqualTo(getWorkflowRunBuilder().status(status).build());
  }

  @ParameterizedTest
  @EnumSource(RunConclusion.class)
  @NullSource
  void shouldGetConvertedToWorkflowRun(RunConclusion conclusion) {
    assertThat(
            getWorkflowRunRecordBuilder()
                .conclusion(Optional.ofNullable(conclusion).map(Enum::name).orElse(null))
                .build()
                .toWorkflowRun())
        .isEqualTo(getWorkflowRunBuilder().conclusion(conclusion).build());
  }
}
