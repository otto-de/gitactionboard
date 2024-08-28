package de.otto.platform.gitactionboard.fixtures;

import de.otto.platform.gitactionboard.domain.workflow.Workflow;

public class WorkflowsFixture {
  public static final String REPO_NAME = "hello-world";
  public static final String BUILD_AND_DEPLOYMENT_WORKFLOW_NAME =
      "hello-world-build-and-deployment";
  public static final long WORKFLOW_ID = 2151835;

  public static Workflow.WorkflowBuilder getWorkflowBuilder() {
    return Workflow.builder()
        .id(WORKFLOW_ID)
        .name("hello-world-build-and-deployment")
        .repoName(REPO_NAME);
  }
}
