package de.otto.platform.gitactionboard.adapters.repository.workflow;

import static de.otto.platform.gitactionboard.TestUtil.saveWorkflowRecords;
import static de.otto.platform.gitactionboard.fixtures.WorkflowsFixture.REPO_NAME;
import static de.otto.platform.gitactionboard.fixtures.WorkflowsFixture.getWorkflowBuilder;
import static de.otto.platform.gitactionboard.fixtures.WorkflowsFixture.getWorkflowRecordBuilder;
import static org.assertj.core.api.Assertions.assertThat;

import de.otto.platform.gitactionboard.RepositoryTest;
import de.otto.platform.gitactionboard.domain.repository.WorkflowRepository;
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
    properties = {"sqlite.db.file.path=gitactionboard_PersistingWorkflowRepository_test.db"})
@ContextConfiguration(classes = PersistingWorkflowRepository.class)
class PersistingWorkflowRepositoryIntegrationTest {
  private static final long ID_2 = 123;

  @Autowired private WorkflowRepository workflowRepository;

  @Autowired private JdbcCurdWorkflowRepository jdbcCurdWorkflowRepository;

  @Autowired private JdbcTemplate jdbcTemplate;

  @Nested
  class SaveTest {
    @Test
    @SneakyThrows
    void shouldStoreGivenWorkflowsToDB() {
      workflowRepository
          .save(List.of(getWorkflowBuilder().build(), getWorkflowBuilder().id(ID_2).build()))
          .join();

      assertThat(jdbcCurdWorkflowRepository.findAll())
          .hasSize(2)
          .contains(
              getWorkflowRecordBuilder().build(), getWorkflowRecordBuilder().id(ID_2).build());
    }

    @Test
    @SneakyThrows
    void shouldSafelyHandleEmptyListWhileSavingWorkflows() {
      workflowRepository.save(List.of()).join();

      assertThat(jdbcCurdWorkflowRepository.findAll()).isEmpty();
    }
  }

  @Nested
  class FindAllTest {
    @Test
    void shouldFindAllWorkflowsFromDB() {
      final List<WorkflowRecord> workflowRecords =
          List.of(getWorkflowRecordBuilder().build(), getWorkflowRecordBuilder().id(ID_2).build());

      saveWorkflowRecords(jdbcTemplate, workflowRecords);

      assertThat(workflowRepository.findAll().join())
          .hasSize(2)
          .contains(getWorkflowBuilder().build(), getWorkflowBuilder().id(ID_2).build());
    }

    @Test
    void shouldReturnEmptyIfNoWorkflowExists() {
      assertThat(workflowRepository.findAll().join()).isEmpty();
    }
  }

  @Nested
  class FindByRepoNameTest {
    @BeforeEach
    void setUp() {
      final List<WorkflowRecord> workflowRecords =
          List.of(getWorkflowRecordBuilder().build(), getWorkflowRecordBuilder().id(ID_2).build());

      saveWorkflowRecords(jdbcTemplate, workflowRecords);
    }

    @Test
    void shouldFindWorkflowsByRepoName() {
      assertThat(workflowRepository.findByRepoName(REPO_NAME).join())
          .hasSize(2)
          .contains(getWorkflowBuilder().build(), getWorkflowBuilder().id(ID_2).build());
    }

    @Test
    void shouldReturnEmptyListIfNoWorkflowExistsForRepo() {
      assertThat(workflowRepository.findByRepoName("test repo").join()).isEmpty();
    }
  }
}
