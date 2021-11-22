package de.otto.platform.gitactionboard.fixtures;

import de.otto.platform.gitactionboard.domain.Workflow;

public class WorkflowsFixture {
  public static final String REPO_NAME = "hello-world";
  public static final String BUILD_AND_DEPLOYMENT_WORKFLOW_NAME =
      "hello-world-build-and-deployment";

  public static Workflow.WorkflowBuilder getWorkflowBuilder() {
    return Workflow.builder()
        .id(2151835)
        .name("hello-world-build-and-deployment")
        .repoName(REPO_NAME);
  }
}
