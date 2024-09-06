package de.otto.platform.gitactionboard.adapters.repository.workflow.job;

import static de.otto.platform.gitactionboard.TestUtil.persistWorkflowRunRecords;
import static de.otto.platform.gitactionboard.TestUtil.saveWorkflowRecords;
import static de.otto.platform.gitactionboard.fixtures.JobFixture.getWorkflowJobBuilder;
import static de.otto.platform.gitactionboard.fixtures.JobFixture.getWorkflowJobRecordBuilder;
import static de.otto.platform.gitactionboard.fixtures.WorkflowRunFixture.RUN_ATTEMPT;
import static de.otto.platform.gitactionboard.fixtures.WorkflowRunFixture.RUN_ID;
import static de.otto.platform.gitactionboard.fixtures.WorkflowRunFixture.getWorkflowRunRecordBuilder;
import static de.otto.platform.gitactionboard.fixtures.WorkflowsFixture.getWorkflowRecordBuilder;
import static org.assertj.core.api.Assertions.assertThat;

import de.otto.platform.gitactionboard.RepositoryTest;
import de.otto.platform.gitactionboard.TestUtil;
import de.otto.platform.gitactionboard.domain.repository.WorkflowJobRepository;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@RepositoryTest
@TestPropertySource(
    properties = {"sqlite.db.file.path=gitactionboard_PersistingWorkflowJobRepository_test.db"})
@ContextConfiguration(classes = PersistingWorkflowJobRepository.class)
class PersistingWorkflowJobRepositoryIntegrationTest {
  private static final long JOB_ID_2 = 3212;

  @Autowired private WorkflowJobRepository workflowJobRepository;

  @Autowired private JdbcCurdWorkflowJobRepository jdbcCurdWorkflowJobRepository;

  @Autowired private JdbcTemplate jdbcTemplate;

  @BeforeEach
  void setUp() {
    saveWorkflowRecords(jdbcTemplate, getWorkflowRecordBuilder().build());
    persistWorkflowRunRecords(jdbcTemplate, getWorkflowRunRecordBuilder().build());
  }

  @Nested
  class SaveTest {
    @Test
    void shouldStoreGivenWorkflowJobs() {
      workflowJobRepository
          .save(
              List.of(
                  getWorkflowJobBuilder().build(), getWorkflowJobBuilder().id(JOB_ID_2).build()))
          .join();

      assertThat(jdbcCurdWorkflowJobRepository.findAll())
          .hasSize(2)
          .contains(
              getWorkflowJobRecordBuilder().build(),
              getWorkflowJobRecordBuilder().id(JOB_ID_2).build());
    }

    @Test
    @SneakyThrows
    void shouldSafelyHandleEmptyListWhileSavingWorkflowJobs() {
      workflowJobRepository.save(List.of()).join();

      assertThat(jdbcCurdWorkflowJobRepository.findAll()).isEmpty();
    }
  }

  @Nested
  class FindByWorkflowRunIdAndRunAttemptTest {
    @BeforeEach
    void setUp() {
      final long runId2 = 9887987L;
      persistWorkflowRunRecords(jdbcTemplate, getWorkflowRunRecordBuilder().id(runId2).build());

      TestUtil.persistWorkflowJobRecords(
          jdbcTemplate,
          List.of(
              getWorkflowJobRecordBuilder().build(),
              getWorkflowJobRecordBuilder().id(JOB_ID_2).build(),
              getWorkflowJobRecordBuilder().id(32124L).workflowRunId(runId2).build()));
    }

    @Test
    void shouldFindWorkflowJobByWorkflowRunIdAndRunAttempt() {
      assertThat(workflowJobRepository.findByWorkflowRunIdAndRunAttempt(RUN_ID, RUN_ATTEMPT).join())
          .containsExactlyInAnyOrder(
              getWorkflowJobBuilder().build(), getWorkflowJobBuilder().id(JOB_ID_2).build());
    }

    @Test
    void shouldReturnEmptyListIfUnableToAnyWorkflowJobForGivenRunIdAndRunAttempt() {
      assertThat(workflowJobRepository.findByWorkflowRunIdAndRunAttempt(RUN_ID, 21).join())
          .isEmpty();
    }
  }
}
