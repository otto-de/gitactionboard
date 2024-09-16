package de.otto.platform.gitactionboard.fixtures;

import static de.otto.platform.gitactionboard.fixtures.JobFixture.LAST_BUILD_STARTED_AT;
import static de.otto.platform.gitactionboard.fixtures.JobFixture.LAST_BUILD_TIME;
import static de.otto.platform.gitactionboard.fixtures.JobFixture.TRIGGERED_EVENT;
import static de.otto.platform.gitactionboard.fixtures.WorkflowsFixture.WORKFLOW_ID;

import de.otto.platform.gitactionboard.adapters.repository.workflow.run.WorkflowRunRecord;
import de.otto.platform.gitactionboard.domain.workflow.RunConclusion;
import de.otto.platform.gitactionboard.domain.workflow.RunStatus;
import de.otto.platform.gitactionboard.domain.workflow.WorkflowRun;
import lombok.experimental.UtilityClass;

@UtilityClass
public class WorkflowRunFixture {
  public static final long RUN_ID = 1132386117;
  public static final long RUN_NUMBER = 206;
  public static final int RUN_ATTEMPT = 1;
  public static final RunConclusion RUN_CONCLUSION = RunConclusion.SUCCESS;

  public static WorkflowRun.WorkflowRunBuilder getWorkflowRunBuilder() {
    return WorkflowRun.builder()
        .id(RUN_ID)
        .runAttempt(RUN_ATTEMPT)
        .workflowId(WORKFLOW_ID)
        .status(RunStatus.COMPLETED)
        .conclusion(RUN_CONCLUSION)
        .runNumber(RUN_NUMBER)
        .updatedAt(LAST_BUILD_TIME)
        .createdAt(LAST_BUILD_STARTED_AT)
        .triggeredEvent(TRIGGERED_EVENT);
  }

  public static WorkflowRunRecord.WorkflowRunRecordBuilder getWorkflowRunRecordBuilder() {
    return WorkflowRunRecord.builder()
        .id(RUN_ID)
        .runAttempt(RUN_ATTEMPT)
        .workflowId(WORKFLOW_ID)
        .status(RunStatus.COMPLETED.name())
        .conclusion(RUN_CONCLUSION.name())
        .runNumber(RUN_NUMBER)
        .updatedAt(LAST_BUILD_TIME)
        .createdAt(LAST_BUILD_STARTED_AT)
        .triggeredEvent(TRIGGERED_EVENT);
  }
}
