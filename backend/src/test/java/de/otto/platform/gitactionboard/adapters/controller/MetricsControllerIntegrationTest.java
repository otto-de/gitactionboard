package de.otto.platform.gitactionboard.adapters.controller;

import static de.otto.platform.gitactionboard.TestUtil.invokeGetApiAndValidate;
import static de.otto.platform.gitactionboard.TestUtil.persistWorkflowRunRecords;
import static de.otto.platform.gitactionboard.TestUtil.readFile;
import static de.otto.platform.gitactionboard.TestUtil.saveWorkflowRecords;
import static de.otto.platform.gitactionboard.fixtures.WorkflowRunFixture.getWorkflowRunRecordBuilder;
import static de.otto.platform.gitactionboard.fixtures.WorkflowsFixture.REPO_NAME;
import static de.otto.platform.gitactionboard.fixtures.WorkflowsFixture.WORKFLOW_ID;
import static de.otto.platform.gitactionboard.fixtures.WorkflowsFixture.getWorkflowRecordBuilder;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

import de.otto.platform.gitactionboard.IntegrationTest;
import de.otto.platform.gitactionboard.PersistingRepositoryCleanupExtension;
import de.otto.platform.gitactionboard.adapters.repository.workflow.run.WorkflowRunRecord;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@IntegrationTest
@DirtiesContext
@AutoConfigureMockMvc
@SpringBootTest(
    webEnvironment = RANDOM_PORT,
    properties = {"sqlite.db.file.path=gitactionboard_MetricsControllerIntegration_test.db"})
@Transactional(propagation = NOT_SUPPORTED)
@ExtendWith(PersistingRepositoryCleanupExtension.class)
class MetricsControllerIntegrationTest {
  private static final long RUN_ID_2 = 3212;
  private static final long RUN_ID_3 = 31102;
  private static final long RUN_ID_4 = 41102;
  private static final long RUN_ID_5 = 51102;
  private static final Instant STARTED_AT_1 = Instant.parse("2024-09-12T15:32:32.954Z");
  private static final Instant COMPLETED_1 = STARTED_AT_1.plus(1, MINUTES);
  private static final Instant STARTED_AT_2 = STARTED_AT_1.minus(7, ChronoUnit.DAYS);
  private static final Instant COMPLETED_AT_2 = STARTED_AT_2.plus(1, MINUTES);
  private static final Instant STARTED_AT_3 = STARTED_AT_1.minus(30, ChronoUnit.DAYS);
  private static final Instant COMPLETED_AT_3 = STARTED_AT_3.plus(1, MINUTES);
  private static final long WORKFLOW_ID_2 = WORKFLOW_ID + 2;
  private static final String REPO_NAME_2 = "%s_2".formatted(REPO_NAME);

  @Autowired private JdbcTemplate jdbcTemplate;

  @Autowired private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    saveWorkflowRecords(
        jdbcTemplate,
        getWorkflowRecordBuilder().build(),
        getWorkflowRecordBuilder().repoName(REPO_NAME_2).id(WORKFLOW_ID_2).build());

    final List<WorkflowRunRecord> workflowRunRecords =
        List.of(
            getWorkflowRunRecordBuilder()
                .createdAt(STARTED_AT_3)
                .updatedAt(COMPLETED_AT_3)
                .id(RUN_ID_3)
                .build(),
            getWorkflowRunRecordBuilder()
                .createdAt(STARTED_AT_2)
                .updatedAt(COMPLETED_AT_2)
                .id(RUN_ID_2)
                .build(),
            getWorkflowRunRecordBuilder().createdAt(STARTED_AT_1).updatedAt(COMPLETED_1).build(),
            getWorkflowRunRecordBuilder()
                .workflowId(WORKFLOW_ID_2)
                .createdAt(STARTED_AT_2)
                .updatedAt(COMPLETED_AT_2)
                .id(RUN_ID_4)
                .build(),
            getWorkflowRunRecordBuilder()
                .workflowId(WORKFLOW_ID_2)
                .createdAt(STARTED_AT_1)
                .updatedAt(COMPLETED_1)
                .id(RUN_ID_5)
                .build());

    persistWorkflowRunRecords(jdbcTemplate, workflowRunRecords);
  }

  @Test
  void shouldFindWorkflowRunMetricForGivenRepoName() {
    invokeGetApiAndValidate(
        mockMvc,
        "/v1/metrics/workflow-runs/%s?from=%s".formatted(REPO_NAME, STARTED_AT_3),
        APPLICATION_JSON_VALUE,
        content().json(readFile("testData/workflowRunMetrics.json")),
        "dummyAccessToken");
  }
}
