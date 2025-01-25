package de.otto.platform.gitactionboard.adapters.repository.workflow.run;

import static de.otto.platform.gitactionboard.fixtures.WorkflowRunFixture.getWorkflowRunBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import de.otto.platform.gitactionboard.domain.workflow.RunConclusion;
import de.otto.platform.gitactionboard.domain.workflow.WorkflowRun;
import java.sql.PreparedStatement;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import lombok.SneakyThrows;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;

@ExtendWith(MockitoExtension.class)
class PersistingWorkflowRunRepositoryTest {
  private static final Duration TEN_SECONDS = Duration.ofSeconds(10);
  @Mock private JdbcCurdWorkflowRunRepository jdbcCurdWorkflowRunRepository;

  @Mock private JdbcTemplate jdbcTemplate;

  @Mock private WorkflowRun mockWorkflowRun1;

  @Mock private WorkflowRun mockWorkflowRun2;

  @Mock private WorkflowRunRecord mockWorkflowRunRecord1;

  @Mock private WorkflowRunRecord mockWorkflowRunRecord2;

  @Mock private PreparedStatement preparedStatement;

  @Captor
  private ArgumentCaptor<ParameterizedPreparedStatementSetter<WorkflowRun>>
      parameterizedPreparedStatementSetterArgumentCaptor;

  private PersistingWorkflowRunRepository persistingWorkflowRunRepository;

  @BeforeEach
  void setUp() {
    persistingWorkflowRunRepository =
        new PersistingWorkflowRunRepository(jdbcCurdWorkflowRunRepository, jdbcTemplate);
  }

  @Test
  void shouldSafelyHandleEmptyLisWhileSavingGivenWorkflowRuns() {
    assertThat(persistingWorkflowRunRepository.save(List.of())).succeedsWithin(TEN_SECONDS);

    verifyNoInteractions(jdbcCurdWorkflowRunRepository, jdbcTemplate);
  }

  @ParameterizedTest
  @NullSource
  @SneakyThrows
  @EnumSource(RunConclusion.class)
  void shouldSaveGivenWorkflowRuns(RunConclusion conclusion) {
    final WorkflowRun workflowRun = getWorkflowRunBuilder().conclusion(conclusion).build();

    final List<WorkflowRun> workflowRuns = List.of(workflowRun, mockWorkflowRun2);

    assertThat(persistingWorkflowRunRepository.save(workflowRuns)).succeedsWithin(TEN_SECONDS);

    verify(jdbcTemplate, times(1))
        .batchUpdate(
            eq(
                "insert or replace into workflow_runs (id, run_attempt, workflow_id, status, conclusion, run_number, updated_at, created_at, triggered_event) values (?, ?, ?,?, ?, ?,?, ?, ?)"),
            eq(workflowRuns),
            eq(100),
            parameterizedPreparedStatementSetterArgumentCaptor.capture());

    parameterizedPreparedStatementSetterArgumentCaptor
        .getValue()
        .setValues(preparedStatement, workflowRun);

    verify(preparedStatement).setLong(1, workflowRun.getId());
    verify(preparedStatement).setLong(2, workflowRun.getRunAttempt());
    verify(preparedStatement).setLong(3, workflowRun.getWorkflowId());
    verify(preparedStatement).setString(4, workflowRun.getStatus().name());
    verify(preparedStatement)
        .setString(5, Optional.ofNullable(conclusion).map(Enum::name).orElse(null));
    verify(preparedStatement).setLong(6, workflowRun.getRunNumber());
    verify(preparedStatement).setString(7, workflowRun.getUpdatedAt().toString());
    verify(preparedStatement).setString(8, workflowRun.getCreatedAt().toString());
    verify(preparedStatement).setString(9, workflowRun.getTriggeredEvent());

    verifyNoInteractions(jdbcCurdWorkflowRunRepository);
  }

  @Test
  void shouldFetchAllWorkflowRunsFromDatabase() {
    when(mockWorkflowRunRecord1.toWorkflowRun()).thenReturn(mockWorkflowRun1);
    when(mockWorkflowRunRecord2.toWorkflowRun()).thenReturn(mockWorkflowRun2);
    when(jdbcCurdWorkflowRunRepository.findAll())
        .thenReturn(List.of(mockWorkflowRunRecord1, mockWorkflowRunRecord2));

    assertThat(persistingWorkflowRunRepository.findAll())
        .succeedsWithin(TEN_SECONDS)
        .asInstanceOf(InstanceOfAssertFactories.LIST)
        .isEqualTo(List.of(mockWorkflowRun1, mockWorkflowRun2));

    verifyNoInteractions(jdbcTemplate);
  }
}
