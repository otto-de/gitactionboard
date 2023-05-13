package de.otto.platform.gitactionboard.fixtures;

import static de.otto.platform.gitactionboard.domain.workflow.Activity.SLEEPING;
import static de.otto.platform.gitactionboard.fixtures.WorkflowsFixture.REPO_NAME;

import de.otto.platform.gitactionboard.domain.workflow.JobDetails;
import de.otto.platform.gitactionboard.domain.workflow.Status;
import java.time.Instant;

public class JobDetailsFixture {

  public static final String WORKFLOW_NAME = "hello-world-checks";
  public static final String JOB_URL = "https://github.com/johndoe/hello-world/runs/1132386127";
  public static final String JOB_NAME = "dependency-checks";
  public static final String TRIGGERED_EVENT = "push";

  public static JobDetails.JobDetailsBuilder getJobDetailsBuilder() {
    return JobDetails.builder()
        .id(1132386127)
        .name(JOB_NAME)
        .runNumber(206)
        .url(JOB_URL)
        .activity(SLEEPING)
        .lastBuildStatus(Status.SUCCESS)
        .lastBuildTime(Instant.parse("2020-09-18T06:14:54.000Z"))
        .triggeredEvent(TRIGGERED_EVENT)
        .repoName(REPO_NAME)
        .workflowName(WORKFLOW_NAME);
  }
}
