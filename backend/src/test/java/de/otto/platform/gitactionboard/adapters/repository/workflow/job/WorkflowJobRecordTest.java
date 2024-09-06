package de.otto.platform.gitactionboard.adapters.repository.workflow.job;

import static de.otto.platform.gitactionboard.fixtures.JobFixture.getWorkflowJobBuilder;
import static de.otto.platform.gitactionboard.fixtures.JobFixture.getWorkflowJobRecordBuilder;
import static org.assertj.core.api.Assertions.assertThat;

import de.otto.platform.gitactionboard.Parallel;
import de.otto.platform.gitactionboard.domain.workflow.RunConclusion;
import de.otto.platform.gitactionboard.domain.workflow.RunStatus;
import java.util.Optional;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;

@Parallel
class WorkflowJobRecordTest {
  @ParameterizedTest
  @EnumSource(RunStatus.class)
  void shouldGetConvertedToWorkflowJob(RunStatus status) {
    assertThat(getWorkflowJobRecordBuilder().status(status.name()).build().toWorkflowJob())
        .isEqualTo(getWorkflowJobBuilder().status(status).build());
  }

  @ParameterizedTest
  @EnumSource(RunConclusion.class)
  @NullSource
  void shouldGetConvertedToWorkflowJob(RunConclusion conclusion) {
    assertThat(
            getWorkflowJobRecordBuilder()
                .conclusion(Optional.ofNullable(conclusion).map(Enum::name).orElse(null))
                .build()
                .toWorkflowJob())
        .isEqualTo(getWorkflowJobBuilder().conclusion(conclusion).build());
  }

  @ParameterizedTest
  @EnumSource(RunStatus.class)
  void shouldGetInstantiateFromGivenWorkflowJob(RunStatus status) {
    assertThat(WorkflowJobRecord.from(getWorkflowJobBuilder().status(status).build()))
        .isEqualTo(getWorkflowJobRecordBuilder().status(status.name()).build());
  }

  @ParameterizedTest
  @EnumSource(RunConclusion.class)
  @NullSource
  void shouldGetInstantiateFromGivenWorkflowJob(RunConclusion conclusion) {
    assertThat(WorkflowJobRecord.from(getWorkflowJobBuilder().conclusion(conclusion).build()))
        .isEqualTo(
            getWorkflowJobRecordBuilder()
                .conclusion(Optional.ofNullable(conclusion).map(Enum::name).orElse(null))
                .build());
  }
}
