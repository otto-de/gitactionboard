package de.otto.platform.gitactionboard.adapters.repository.workflow;

import static de.otto.platform.gitactionboard.fixtures.WorkflowsFixture.REPO_NAME;
import static de.otto.platform.gitactionboard.fixtures.WorkflowsFixture.getWorkflowBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import de.otto.platform.gitactionboard.Sequential;
import de.otto.platform.gitactionboard.domain.workflow.Workflow;
import java.sql.PreparedStatement;
import java.time.Duration;
import java.util.List;
import lombok.SneakyThrows;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;

@Sequential
@ExtendWith(MockitoExtension.class)
class PersistingWorkflowRepositoryTest {
  private static final Duration DURATION = Duration.ofSeconds(10);

  @Mock private JdbcCurdWorkflowRepository jdbcCurdWorkflowRepository;

  @Mock private JdbcTemplate jdbcTemplate;

  @Mock private Workflow mockWorkflow1;

  @Mock private Workflow mockWorkflow2;

  @Mock private WorkflowRecord workflowRecord1;

  @Mock private WorkflowRecord workflowRecord2;

  @Mock private PreparedStatement preparedStatement;

  @Captor
  private ArgumentCaptor<ParameterizedPreparedStatementSetter<Workflow>>
      parameterizedPreparedStatementSetterArgumentCaptor;

  private PersistingWorkflowRepository persistingWorkflowRepository;

  @BeforeEach
  void setUp() {
    persistingWorkflowRepository =
        new PersistingWorkflowRepository(jdbcCurdWorkflowRepository, jdbcTemplate);
  }

  @Test
  void shouldSafelyHandleSaveForEmptyListOfWorkflows() {
    assertThat(persistingWorkflowRepository.save(List.of())).succeedsWithin(DURATION);

    verifyNoInteractions(jdbcCurdWorkflowRepository, jdbcTemplate);
  }

  @Test
  @SneakyThrows
  void shouldSaveGivenWorkflows() {
    final Workflow workflow = getWorkflowBuilder().build();

    final List<Workflow> workflows = List.of(workflow, mockWorkflow2);

    assertThat(persistingWorkflowRepository.save(workflows)).succeedsWithin(DURATION);

    verify(jdbcTemplate, times(1))
        .batchUpdate(
            eq("insert or replace into workflows (id, name, repo_name) values (?, ?, ?)"),
            eq(workflows),
            eq(100),
            parameterizedPreparedStatementSetterArgumentCaptor.capture());

    parameterizedPreparedStatementSetterArgumentCaptor
        .getValue()
        .setValues(preparedStatement, workflow);

    verify(preparedStatement, times(1)).setLong(1, workflow.getId());
    verify(preparedStatement, times(1)).setString(2, workflow.getName());
    verify(preparedStatement, times(1)).setString(3, workflow.getRepoName());

    verifyNoInteractions(jdbcCurdWorkflowRepository);
  }

  @Test
  void shouldFetchAllWorkflowsFromDatabase() {
    final List<WorkflowRecord> workflowRecords = List.of(workflowRecord1, workflowRecord2);

    when(workflowRecord1.toWorkflow()).thenReturn(mockWorkflow1);
    when(workflowRecord2.toWorkflow()).thenReturn(mockWorkflow2);

    when(jdbcCurdWorkflowRepository.findAll()).thenReturn(workflowRecords);

    assertThat(persistingWorkflowRepository.findAll())
        .succeedsWithin(DURATION)
        .asInstanceOf(InstanceOfAssertFactories.LIST)
        .isEqualTo(List.of(mockWorkflow1, mockWorkflow2));

    verify(jdbcCurdWorkflowRepository, times(1)).findAll();
    verifyNoInteractions(jdbcTemplate);
  }

  @Test
  void shouldFetchAllWorkflowsFromDatabaseForGivenRepository() {
    final List<WorkflowRecord> workflowRecords = List.of(workflowRecord1, workflowRecord2);

    when(workflowRecord1.toWorkflow()).thenReturn(mockWorkflow1);
    when(workflowRecord2.toWorkflow()).thenReturn(mockWorkflow2);

    when(jdbcCurdWorkflowRepository.findByRepoName(REPO_NAME)).thenReturn(workflowRecords.stream());

    assertThat(persistingWorkflowRepository.findByRepoName(REPO_NAME))
        .succeedsWithin(DURATION)
        .asInstanceOf(InstanceOfAssertFactories.LIST)
        .isEqualTo(List.of(mockWorkflow1, mockWorkflow2));

    verify(jdbcCurdWorkflowRepository, times(1)).findByRepoName(REPO_NAME);
    verifyNoInteractions(jdbcTemplate);
  }
}
