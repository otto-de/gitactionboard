package de.otto.platform.gitactionboard.adapters.repository.workflow.job;

import static de.otto.platform.gitactionboard.fixtures.JobFixture.LAST_BUILD_TIME;
import static de.otto.platform.gitactionboard.fixtures.JobFixture.getWorkflowJobBuilder;
import static de.otto.platform.gitactionboard.fixtures.WorkflowRunFixture.RUN_ATTEMPT;
import static de.otto.platform.gitactionboard.fixtures.WorkflowRunFixture.RUN_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import de.otto.platform.gitactionboard.domain.workflow.RunConclusion;
import de.otto.platform.gitactionboard.domain.workflow.WorkflowJob;
import java.sql.PreparedStatement;
import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
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
class PersistingWorkflowJobRepositoryTest {

  private static final Duration TEN_SECONDS = Duration.ofSeconds(10);

  @Mock private JdbcTemplate jdbcTemplate;

  @Mock private JdbcCurdWorkflowJobRepository jdbcCurdWorkflowJobRepository;

  @Mock private PreparedStatement preparedStatement;

  @Mock private WorkflowJob mockWorkflowJob1;

  @Mock private WorkflowJob mockWorkflowJob2;

  @Mock private WorkflowJobRecord mockWorkflowJobRecord1;

  @Mock private WorkflowJobRecord mockWorkflowJobRecord2;

  @Captor
  private ArgumentCaptor<ParameterizedPreparedStatementSetter<WorkflowJob>>
      parameterizedPreparedStatementSetterArgumentCaptor;

  private PersistingWorkflowJobRepository persistingWorkflowJobRepository;

  @BeforeEach
  void setUp() {
    persistingWorkflowJobRepository =
        new PersistingWorkflowJobRepository(jdbcTemplate, jdbcCurdWorkflowJobRepository);
  }

  @Test
  void shouldSafelyHandleEmptyListOfWorkflowJobsDuringSave() {
    assertThat(persistingWorkflowJobRepository.save(List.of())).succeedsWithin(TEN_SECONDS);

    verifyNoInteractions(jdbcCurdWorkflowJobRepository, jdbcTemplate, preparedStatement);
  }

  @SneakyThrows
  @ParameterizedTest
  @NullSource
  @EnumSource(RunConclusion.class)
  void shouldSaveGivenWorkflowJobs(RunConclusion runConclusion) {
    final WorkflowJob workflowJob =
        getWorkflowJobBuilder()
            .conclusion(runConclusion)
            .completedAt(
                Optional.ofNullable(runConclusion).map(unused -> LAST_BUILD_TIME).orElse(null))
            .build();

    final List<WorkflowJob> workflowJobs = List.of(workflowJob, mockWorkflowJob2);

    assertThat(persistingWorkflowJobRepository.save(workflowJobs)).succeedsWithin(TEN_SECONDS);

    verify(jdbcTemplate, times(1))
        .batchUpdate(
            eq(
                "insert or replace into workflow_jobs (id, name, status, conclusion, started_at, completed_at, workflow_id,workflow_run_id, run_attempt, url) values (?, ?, ?,?, ?, ?,?, ?, ?, ?)"),
            eq(workflowJobs),
            eq(100),
            parameterizedPreparedStatementSetterArgumentCaptor.capture());

    parameterizedPreparedStatementSetterArgumentCaptor
        .getValue()
        .setValues(preparedStatement, workflowJob);

    verify(preparedStatement).setLong(1, workflowJob.getId());
    verify(preparedStatement).setString(2, workflowJob.getName());
    verify(preparedStatement).setString(3, workflowJob.getStatus().name());
    verify(preparedStatement)
        .setString(4, Optional.ofNullable(runConclusion).map(Enum::name).orElse(null));
    verify(preparedStatement).setString(5, workflowJob.getStartedAt().toString());
    verify(preparedStatement)
        .setString(
            6,
            Optional.ofNullable(runConclusion)
                .map(unused -> LAST_BUILD_TIME.toString())
                .orElse(null));
    verify(preparedStatement).setLong(7, workflowJob.getWorkflowId());
    verify(preparedStatement).setLong(8, workflowJob.getWorkflowRunId());
    verify(preparedStatement).setLong(9, workflowJob.getRunAttempt());
    verify(preparedStatement).setString(10, workflowJob.getUrl());

    verifyNoInteractions(jdbcCurdWorkflowJobRepository);
  }

  @Test
  void shouldFindWorkflowJobByRunIdAndRunAttempt() {
    when(jdbcCurdWorkflowJobRepository.findByWorkflowRunIdAndRunAttempt(RUN_ID, RUN_ATTEMPT))
        .thenReturn(Stream.of(mockWorkflowJobRecord1, mockWorkflowJobRecord2));
    when(mockWorkflowJobRecord1.toWorkflowJob()).thenReturn(mockWorkflowJob1);
    when(mockWorkflowJobRecord2.toWorkflowJob()).thenReturn(mockWorkflowJob2);

    assertThat(
            persistingWorkflowJobRepository.findByWorkflowRunIdAndRunAttempt(RUN_ID, RUN_ATTEMPT))
        .succeedsWithin(TEN_SECONDS)
        .asInstanceOf(InstanceOfAssertFactories.LIST)
        .isEqualTo(List.of(mockWorkflowJob1, mockWorkflowJob2));

    verifyNoInteractions(jdbcTemplate);
  }
}
