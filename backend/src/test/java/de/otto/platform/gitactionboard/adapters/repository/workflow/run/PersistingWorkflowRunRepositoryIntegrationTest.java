package de.otto.platform.gitactionboard.adapters.repository.workflow.run;

import static de.otto.platform.gitactionboard.TestUtil.persistWorkflowRunRecords;
import static de.otto.platform.gitactionboard.TestUtil.saveWorkflowRecords;
import static de.otto.platform.gitactionboard.fixtures.WorkflowRunFixture.getWorkflowRunBuilder;
import static de.otto.platform.gitactionboard.fixtures.WorkflowRunFixture.getWorkflowRunRecordBuilder;
import static de.otto.platform.gitactionboard.fixtures.WorkflowsFixture.getWorkflowRecordBuilder;
import static org.assertj.core.api.Assertions.assertThat;

import de.otto.platform.gitactionboard.RepositoryTest;
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
    properties = {"sqlite.db.file.path=gitactionboard_PersistingWorkflowRunRepository_test.db"})
@ContextConfiguration(classes = PersistingWorkflowRunRepository.class)
class PersistingWorkflowRunRepositoryIntegrationTest {
  private static final long RUN_ID_2 = 3212;
  @Autowired private PersistingWorkflowRunRepository workflowRunRepository;

  @Autowired private JdbcCurdWorkflowRunRepository jdbcCurdWorkflowRunRepository;

  @Autowired private JdbcTemplate jdbcTemplate;

  @BeforeEach
  void setUp() {
    saveWorkflowRecords(jdbcTemplate, getWorkflowRecordBuilder().build());
  }

  @Nested
  class SaveTest {
    @Test
    void shouldStoreGivenWorkflowRuns() {
      workflowRunRepository
          .save(
              List.of(
                  getWorkflowRunBuilder().build(), getWorkflowRunBuilder().id(RUN_ID_2).build()))
          .join();

      assertThat(jdbcCurdWorkflowRunRepository.findAll())
          .hasSize(2)
          .contains(
              getWorkflowRunRecordBuilder().build(),
              getWorkflowRunRecordBuilder().id(RUN_ID_2).build());
    }

    @Test
    @SneakyThrows
    void shouldSafelyHandleEmptyListWhileSavingWorkflows() {
      workflowRunRepository.save(List.of()).join();

      assertThat(jdbcCurdWorkflowRunRepository.findAll()).isEmpty();
    }
  }

  @Nested
  class FindAllTest {
    @Test
    void shouldFindAllPersistedWorkflowRuns() {

      final List<WorkflowRunRecord> workflowRunRecords =
          List.of(
              getWorkflowRunRecordBuilder().build(),
              getWorkflowRunRecordBuilder().id(RUN_ID_2).build());

      persistWorkflowRunRecords(jdbcTemplate, workflowRunRecords);

      assertThat(workflowRunRepository.findAll().join())
          .containsExactlyInAnyOrder(
              getWorkflowRunBuilder().build(), getWorkflowRunBuilder().id(RUN_ID_2).build());
    }

    @Test
    void shouldReturnEmptyIfNoWorkflowRunExists() {
      assertThat(workflowRunRepository.findAll().join()).isEmpty();
    }
  }
}
