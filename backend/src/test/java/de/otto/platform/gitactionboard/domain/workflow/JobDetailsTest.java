package de.otto.platform.gitactionboard.domain.workflow;

import static de.otto.platform.gitactionboard.domain.workflow.JobStatus.UNKNOWN;
import static de.otto.platform.gitactionboard.fixtures.JobFixture.JOB_ID;
import static de.otto.platform.gitactionboard.fixtures.JobFixture.LAST_BUILD_TIME;
import static de.otto.platform.gitactionboard.fixtures.JobFixture.getJobDetailsBuilder;
import static de.otto.platform.gitactionboard.fixtures.JobFixture.getWorkflowJobBuilder;
import static de.otto.platform.gitactionboard.fixtures.WorkflowRunFixture.RUN_NUMBER;
import static java.time.temporal.ChronoUnit.MINUTES;
import static org.assertj.core.api.Assertions.assertThat;

import de.otto.platform.gitactionboard.Parallel;
import de.otto.platform.gitactionboard.fixtures.WorkflowsFixture;
import org.junit.jupiter.api.Test;

@Parallel
class JobDetailsTest {
  private static final String WORKFLOW_NAME = "hello-world-checks";
  private static final String TRIGGERED_EVENT = "schedule";
  private final Workflow workflow =
      WorkflowsFixture.getWorkflowBuilder().name(WORKFLOW_NAME).build();

  @Test
  void shouldCreateJobDetails() {
    final WorkflowJob currentJob = getWorkflowJobBuilder().build();

    final WorkflowJob previousJob =
        currentJob.toBuilder()
            .id(JOB_ID - 1)
            .startedAt(LAST_BUILD_TIME.minus(10, MINUTES))
            .completedAt(LAST_BUILD_TIME.minus(5, MINUTES))
            .workflowRunId(RUN_NUMBER - 1)
            .build();

    assertThat(JobDetails.from(RUN_NUMBER, workflow, currentJob, previousJob, TRIGGERED_EVENT))
        .isEqualTo(
            getJobDetailsBuilder()
                .triggeredEvent(TRIGGERED_EVENT)
                .lastBuildTime(previousJob.getCompletedAt())
                .build());
  }

  @Test
  void shouldCreateJobDetailsWhenPreviousJobIsNotYetCompleted() {
    final WorkflowJob currentJob = getWorkflowJobBuilder().build();

    final WorkflowJob previousJob =
        currentJob.toBuilder()
            .id(JOB_ID - 1)
            .startedAt(LAST_BUILD_TIME.minus(10, MINUTES))
            .conclusion(null)
            .workflowRunId(RUN_NUMBER - 1)
            .build();

    assertThat(JobDetails.from(RUN_NUMBER, workflow, currentJob, previousJob, TRIGGERED_EVENT))
        .isEqualTo(
            getJobDetailsBuilder()
                .triggeredEvent(TRIGGERED_EVENT)
                .lastBuildTime(previousJob.getCompletedAt())
                .lastBuildStatus(UNKNOWN)
                .build());
  }
}
