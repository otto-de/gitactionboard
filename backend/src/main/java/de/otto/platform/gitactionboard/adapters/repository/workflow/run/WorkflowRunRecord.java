package de.otto.platform.gitactionboard.adapters.repository.workflow.run;

import de.otto.platform.gitactionboard.domain.workflow.RunConclusion;
import de.otto.platform.gitactionboard.domain.workflow.RunStatus;
import de.otto.platform.gitactionboard.domain.workflow.WorkflowRun;
import java.time.Instant;
import java.util.Optional;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Value
@Builder
@Table("workflow_runs")
public class WorkflowRunRecord {
  Long id;

  @NonNull
  @Column("run_attempt")
  Integer runAttempt;

  @NonNull
  @Column("workflow_id")
  Long workflowId;

  @NonNull String status;

  String conclusion;

  @Column("run_number")
  long runNumber;

  @NonNull
  @Column("updated_at")
  Instant updatedAt;

  @NonNull
  @Column("created_at")
  Instant createdAt;

  @NonNull
  @Column("triggered_event")
  String triggeredEvent;

  public static WorkflowRunRecord from(WorkflowRun workflowRun) {
    return WorkflowRunRecord.builder()
        .id(workflowRun.getId())
        .runAttempt(workflowRun.getRunAttempt())
        .workflowId(workflowRun.getWorkflowId())
        .status(workflowRun.getStatus().name())
        .conclusion(Optional.ofNullable(workflowRun.getConclusion()).map(Enum::name).orElse(null))
        .runNumber(workflowRun.getRunNumber())
        .updatedAt(workflowRun.getUpdatedAt())
        .createdAt(workflowRun.getCreatedAt())
        .triggeredEvent(workflowRun.getTriggeredEvent())
        .build();
  }

  public WorkflowRun toWorkflowRun() {
    return WorkflowRun.builder()
        .id(id)
        .runAttempt(runAttempt)
        .workflowId(workflowId)
        .status(RunStatus.valueOf(status))
        .conclusion(Optional.ofNullable(conclusion).map(RunConclusion::valueOf).orElse(null))
        .runNumber(runNumber)
        .updatedAt(updatedAt)
        .createdAt(createdAt)
        .triggeredEvent(triggeredEvent)
        .build();
  }
}
