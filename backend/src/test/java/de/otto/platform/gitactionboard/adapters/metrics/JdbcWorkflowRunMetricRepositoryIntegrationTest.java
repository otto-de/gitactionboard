package de.otto.platform.gitactionboard.adapters.metrics;

import static de.otto.platform.gitactionboard.TestUtil.persistWorkflowRunRecords;
import static de.otto.platform.gitactionboard.TestUtil.saveWorkflowRecords;
import static de.otto.platform.gitactionboard.fixtures.WorkflowRunFixture.getWorkflowRunRecordBuilder;
import static de.otto.platform.gitactionboard.fixtures.WorkflowRunMetricFixture.getWorkflowRunMetricBuilder;
import static de.otto.platform.gitactionboard.fixtures.WorkflowsFixture.REPO_NAME;
import static de.otto.platform.gitactionboard.fixtures.WorkflowsFixture.WORKFLOW_ID;
import static de.otto.platform.gitactionboard.fixtures.WorkflowsFixture.getWorkflowRecordBuilder;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;

import de.otto.platform.gitactionboard.RepositoryTest;
import de.otto.platform.gitactionboard.adapters.repository.workflow.run.WorkflowRunRecord;
import de.otto.platform.gitactionboard.domain.metrics.WorkflowRunMetric;
import de.otto.platform.gitactionboard.domain.metrics.WorkflowRunMetricRepository;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

@RepositoryTest
@TestPropertySource(
    properties = {"sqlite.db.file.path=gitactionboard_JdbcWorkflowRunMetricRepository_test.db"})
@ContextConfiguration(classes = {JdbcWorkflowRunMetricRepository.class})
class JdbcWorkflowRunMetricRepositoryIntegrationTest {
  private static final long RUN_ID_2 = 3212;
  private static final long RUN_ID_3 = 31102;
  private static final long RUN_ID_4 = 41102;
  private static final long RUN_ID_5 = 51102;
  private static final Instant STARTED_AT_1 = Instant.now();
  private static final Instant COMPLETED_1 = STARTED_AT_1.plus(1, MINUTES);
  private static final Instant STARTED_AT_2 = Instant.now().minus(7, ChronoUnit.DAYS);
  private static final Instant COMPLETED_AT_2 = STARTED_AT_2.plus(1, MINUTES);
  private static final Instant STARTED_AT_3 = Instant.now().minus(30, ChronoUnit.DAYS);
  private static final Instant COMPLETED_AT_3 = STARTED_AT_3.plus(1, MINUTES);
  private static final long WORKFLOW_ID_2 = WORKFLOW_ID + 2;
  private static final String REPO_NAME_2 = "%s_2".formatted(REPO_NAME);

  @Autowired private JdbcTemplate jdbcTemplate;

  @Autowired private WorkflowRunMetricRepository workflowRunMetricRepository;

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

  @ParameterizedTest(name = "{index} ==> workflow ids {0}, from {1}, to {2}")
  @MethodSource("getParameters")
  void shouldReturnWorkflowRunMetricData(
      Set<Long> workflowIds,
      Instant from,
      Instant to,
      List<WorkflowRunMetric> expectedWorkflowRunMetrics) {
    assertThat(workflowRunMetricRepository.getWorkflowRunMetrics(workflowIds, from, to).join())
        .containsExactlyElementsOf(expectedWorkflowRunMetrics);
  }

  private static Stream<Arguments> getParameters() {
    return Stream.of(
        Arguments.of(
            Set.of(WORKFLOW_ID),
            STARTED_AT_2,
            STARTED_AT_1,
            List.of(
                getWorkflowRunMetricBuilder()
                    .startedAt(STARTED_AT_1)
                    .completedAt(COMPLETED_1)
                    .build(),
                getWorkflowRunMetricBuilder()
                    .startedAt(STARTED_AT_2)
                    .completedAt(COMPLETED_AT_2)
                    .build())),
        Arguments.of(
            Set.of(WORKFLOW_ID, WORKFLOW_ID_2),
            STARTED_AT_2,
            STARTED_AT_1,
            List.of(
                getWorkflowRunMetricBuilder()
                    .startedAt(STARTED_AT_1)
                    .completedAt(COMPLETED_1)
                    .build(),
                getWorkflowRunMetricBuilder()
                    .repoName(REPO_NAME_2)
                    .startedAt(STARTED_AT_1)
                    .completedAt(COMPLETED_1)
                    .build(),
                getWorkflowRunMetricBuilder()
                    .startedAt(STARTED_AT_2)
                    .completedAt(COMPLETED_AT_2)
                    .build(),
                getWorkflowRunMetricBuilder()
                    .repoName(REPO_NAME_2)
                    .startedAt(STARTED_AT_2)
                    .completedAt(COMPLETED_AT_2)
                    .build())),
        Arguments.of(
            Set.of(WORKFLOW_ID),
            STARTED_AT_2.minus(1, MINUTES),
            STARTED_AT_1.plus(1, MINUTES),
            List.of(
                getWorkflowRunMetricBuilder()
                    .startedAt(STARTED_AT_1)
                    .completedAt(COMPLETED_1)
                    .build(),
                getWorkflowRunMetricBuilder()
                    .startedAt(STARTED_AT_2)
                    .completedAt(COMPLETED_AT_2)
                    .build())),
        Arguments.of(
            Set.of(WORKFLOW_ID),
            STARTED_AT_1,
            STARTED_AT_1,
            List.of(
                getWorkflowRunMetricBuilder()
                    .startedAt(STARTED_AT_1)
                    .completedAt(COMPLETED_1)
                    .build())),
        Arguments.of(Set.of(WORKFLOW_ID), COMPLETED_1, COMPLETED_1, List.of()),
        Arguments.of(Set.of(0L), STARTED_AT_2, STARTED_AT_1, List.of()));
  }
}
