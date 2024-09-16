package de.otto.platform.gitactionboard.fixtures;

import static de.otto.platform.gitactionboard.fixtures.JobFixture.LAST_BUILD_STARTED_AT;
import static de.otto.platform.gitactionboard.fixtures.JobFixture.LAST_BUILD_TIME;
import static de.otto.platform.gitactionboard.fixtures.JobFixture.TRIGGERED_EVENT;
import static de.otto.platform.gitactionboard.fixtures.WorkflowRunFixture.RUN_ATTEMPT;
import static de.otto.platform.gitactionboard.fixtures.WorkflowRunFixture.RUN_CONCLUSION;
import static de.otto.platform.gitactionboard.fixtures.WorkflowsFixture.BUILD_AND_DEPLOYMENT_WORKFLOW_NAME;
import static de.otto.platform.gitactionboard.fixtures.WorkflowsFixture.REPO_NAME;

import de.otto.platform.gitactionboard.adapters.controller.WorkflowRunMetricDetails;
import de.otto.platform.gitactionboard.adapters.metrics.WorkflowRunMetricRecord;
import de.otto.platform.gitactionboard.domain.metrics.WorkflowRunMetric;
import lombok.experimental.UtilityClass;

@UtilityClass
public class WorkflowRunMetricFixture {

  public static WorkflowRunMetricDetails.WorkflowRunMetricDetailsBuilder
      getWorkflowRunMetricDetailsBuilder() {
    return WorkflowRunMetricDetails.builder()
        .workflowName(BUILD_AND_DEPLOYMENT_WORKFLOW_NAME)
        .repoName(REPO_NAME)
        .conclusion(RUN_CONCLUSION.name())
        .completedAt(LAST_BUILD_TIME)
        .startedAt(LAST_BUILD_STARTED_AT)
        .runAttempt(RUN_ATTEMPT)
        .triggeredEvent(TRIGGERED_EVENT);
  }

  public static WorkflowRunMetric.WorkflowRunMetricBuilder getWorkflowRunMetricBuilder() {
    return WorkflowRunMetric.builder()
        .workflowName(BUILD_AND_DEPLOYMENT_WORKFLOW_NAME)
        .repoName(REPO_NAME)
        .conclusion(RUN_CONCLUSION)
        .completedAt(LAST_BUILD_TIME)
        .startedAt(LAST_BUILD_STARTED_AT)
        .runAttempt(RUN_ATTEMPT)
        .triggeredEvent(TRIGGERED_EVENT);
  }

  public static WorkflowRunMetricRecord.WorkflowRunMetricRecordBuilder
      getWorkflowRunMetricRecordBuilder() {
    return WorkflowRunMetricRecord.builder()
        .workflowName(BUILD_AND_DEPLOYMENT_WORKFLOW_NAME)
        .repoName(REPO_NAME)
        .conclusion(RUN_CONCLUSION)
        .completedAt(LAST_BUILD_TIME)
        .startedAt(LAST_BUILD_STARTED_AT)
        .runAttempt(RUN_ATTEMPT)
        .triggeredEvent(TRIGGERED_EVENT);
  }
}
