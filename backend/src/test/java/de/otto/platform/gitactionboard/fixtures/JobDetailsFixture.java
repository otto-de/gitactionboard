package de.otto.platform.gitactionboard.fixtures;

import static de.otto.platform.gitactionboard.domain.Activity.SLEEPING;
import static de.otto.platform.gitactionboard.fixtures.WorkflowsFixture.REPO_NAME;

import de.otto.platform.gitactionboard.adapters.service.job.RunConclusion;
import de.otto.platform.gitactionboard.adapters.service.job.RunStatus;
import de.otto.platform.gitactionboard.adapters.service.job.WorkflowsRunDetailsResponse.WorkflowRunDetails;
import de.otto.platform.gitactionboard.domain.Activity;
import de.otto.platform.gitactionboard.domain.JobDetails;
import de.otto.platform.gitactionboard.domain.Status;
import de.otto.platform.gitactionboard.domain.Workflow;
import java.time.Instant;

public class JobDetailsFixture {

  public static final String WORKFLOW_NAME = "hello-world-checks";
  public static final String JOB_URL = "https://github.com/johndoe/hello-world/runs/1132386127";
  public static final String JOB_NAME = "dependency-checks";

  public static JobDetails.JobDetailsBuilder getJobDetailsBuilder() {
    return JobDetails.builder()
        .id(1132386127)
        .name(JOB_NAME)
        .runNumber(206)
        .url(JOB_URL)
        .activity(SLEEPING)
        .lastBuildStatus(Status.SUCCESS)
        .lastBuildTime(Instant.parse("2020-09-18T06:14:54.000Z"))
        .repoName(REPO_NAME)
        .workflowName(WORKFLOW_NAME);
  }

  public static JobDetails from(Workflow workflow, WorkflowRunDetails workflowRun) {
    return from(workflow, workflowRun, workflowRun);
  }

  public static JobDetails from(
      Workflow workflow,
      WorkflowRunDetails currentWorkflowRun,
      WorkflowRunDetails previousWorkflowRun) {
    final Activity activity = RunStatus.getActivity(currentWorkflowRun.getStatus());
    final WorkflowRunDetails runDetails =
        SLEEPING.equals(activity) ? currentWorkflowRun : previousWorkflowRun;
    return JobDetails.builder()
        .id(currentWorkflowRun.getId())
        .workflowName(workflow.getName())
        .repoName(workflow.getRepoName())
        .url(currentWorkflowRun.getUrl())
        .runNumber(currentWorkflowRun.getRunNumber())
        .lastBuildStatus(RunConclusion.getStatus(runDetails.getConclusion()))
        .activity(RunStatus.getActivity(currentWorkflowRun.getStatus()))
        .lastBuildTime(runDetails.getCreatedAt())
        .build();
  }
}
