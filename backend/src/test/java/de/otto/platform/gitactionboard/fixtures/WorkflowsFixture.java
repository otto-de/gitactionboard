package de.otto.platform.gitactionboard.fixtures;

import de.otto.platform.gitactionboard.adapters.repository.workflow.WorkflowRecord;
import de.otto.platform.gitactionboard.domain.workflow.Workflow;

public class WorkflowsFixture {
  public static final String REPO_NAME = "hello-world";
  public static final String BUILD_AND_DEPLOYMENT_WORKFLOW_NAME =
      "hello-world-build-and-deployment";
  public static final long WORKFLOW_ID = 2151835;

  public static Workflow.WorkflowBuilder getWorkflowBuilder() {
    return Workflow.builder()
        .id(WORKFLOW_ID)
        .name(BUILD_AND_DEPLOYMENT_WORKFLOW_NAME)
        .repoName(REPO_NAME);
  }

  public static WorkflowRecord.WorkflowRecordBuilder getWorkflowRecordBuilder() {
    return WorkflowRecord.builder()
        .id(WORKFLOW_ID)
        .name(BUILD_AND_DEPLOYMENT_WORKFLOW_NAME)
        .repoName(REPO_NAME);
  }
}
