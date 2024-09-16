package de.otto.platform.gitactionboard.fixtures;

import static de.otto.platform.gitactionboard.domain.workflow.Activity.SLEEPING;
import static de.otto.platform.gitactionboard.domain.workflow.JobStatus.SUCCESS;
import static de.otto.platform.gitactionboard.fixtures.WorkflowRunFixture.RUN_ATTEMPT;
import static de.otto.platform.gitactionboard.fixtures.WorkflowsFixture.REPO_NAME;
import static de.otto.platform.gitactionboard.fixtures.WorkflowsFixture.WORKFLOW_ID;

import de.otto.platform.gitactionboard.adapters.repository.workflow.job.WorkflowJobRecord;
import de.otto.platform.gitactionboard.domain.workflow.JobDetails;
import de.otto.platform.gitactionboard.domain.workflow.RunConclusion;
import de.otto.platform.gitactionboard.domain.workflow.RunStatus;
import de.otto.platform.gitactionboard.domain.workflow.WorkflowJob;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class JobFixture {

  public static final String WORKFLOW_NAME = "hello-world-checks";
  public static final String JOB_URL = "https://github.com/johndoe/hello-world/runs/1132386127";
  public static final String JOB_NAME = "dependency-checks";
  public static final String TRIGGERED_EVENT = "push";
  public static final int JOB_ID = 1132386127;
  public static final Instant LAST_BUILD_TIME = Instant.parse("2024-09-18T06:14:54.000Z");
  public static final Instant LAST_BUILD_STARTED_AT = LAST_BUILD_TIME.minus(1, ChronoUnit.MINUTES);

  public static JobDetails.JobDetailsBuilder getJobDetailsBuilder() {
    return JobDetails.builder()
        .id(JOB_ID)
        .name(JOB_NAME)
        .runNumber(WorkflowRunFixture.RUN_NUMBER)
        .url(JOB_URL)
        .activity(SLEEPING)
        .lastBuildStatus(SUCCESS)
        .lastBuildTime(LAST_BUILD_TIME)
        .triggeredEvent(TRIGGERED_EVENT)
        .repoName(REPO_NAME)
        .runAttempt(RUN_ATTEMPT)
        .workflowName(WORKFLOW_NAME);
  }

  public static WorkflowJob.WorkflowJobBuilder getWorkflowJobBuilder() {
    return WorkflowJob.builder()
        .id(JOB_ID)
        .name(JOB_NAME)
        .status(RunStatus.COMPLETED)
        .conclusion(RunConclusion.SUCCESS)
        .startedAt(LAST_BUILD_STARTED_AT)
        .completedAt(LAST_BUILD_TIME)
        .workflowId(WORKFLOW_ID)
        .workflowRunId(WorkflowRunFixture.RUN_ID)
        .runAttempt(RUN_ATTEMPT)
        .url(JOB_URL);
  }

  public static WorkflowJobRecord.WorkflowJobRecordBuilder getWorkflowJobRecordBuilder() {
    return WorkflowJobRecord.builder()
        .id(JOB_ID)
        .name(JOB_NAME)
        .status(RunStatus.COMPLETED.name())
        .conclusion(RunConclusion.SUCCESS.name())
        .startedAt(LAST_BUILD_STARTED_AT)
        .completedAt(LAST_BUILD_TIME)
        .workflowId(WORKFLOW_ID)
        .workflowRunId(WorkflowRunFixture.RUN_ID)
        .runAttempt(RUN_ATTEMPT)
        .url(JOB_URL);
  }
}
