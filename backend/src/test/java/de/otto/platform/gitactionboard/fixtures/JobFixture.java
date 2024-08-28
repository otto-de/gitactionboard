package de.otto.platform.gitactionboard.fixtures;

import static de.otto.platform.gitactionboard.domain.workflow.Activity.SLEEPING;
import static de.otto.platform.gitactionboard.domain.workflow.JobStatus.SUCCESS;
import static de.otto.platform.gitactionboard.fixtures.WorkflowsFixture.REPO_NAME;
import static de.otto.platform.gitactionboard.fixtures.WorkflowsFixture.WORKFLOW_ID;

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
  public static final Instant LAST_BUILD_TIME = Instant.parse("2020-09-18T06:14:54.000Z");
  public static final long RUN_NUMBER = 206;

  public static JobDetails.JobDetailsBuilder getJobDetailsBuilder() {
    return JobDetails.builder()
        .id(JOB_ID)
        .name(JOB_NAME)
        .runNumber(RUN_NUMBER)
        .url(JOB_URL)
        .activity(SLEEPING)
        .lastBuildStatus(SUCCESS)
        .lastBuildTime(LAST_BUILD_TIME)
        .triggeredEvent(TRIGGERED_EVENT)
        .repoName(REPO_NAME)
        .workflowName(WORKFLOW_NAME);
  }

  public static WorkflowJob.WorkflowJobBuilder getWorkflowJobBuilder() {
    return WorkflowJob.builder()
        .id(JOB_ID)
        .name(JOB_NAME)
        .status(RunStatus.COMPLETED)
        .conclusion(RunConclusion.SUCCESS)
        .startedAt(LAST_BUILD_TIME.minus(1, ChronoUnit.MINUTES))
        .completedAt(LAST_BUILD_TIME)
        .workflowId(WORKFLOW_ID)
        .workflowRunId(RUN_NUMBER)
        .url(JOB_URL);
  }
}
