package de.otto.platform.gitactionboard.adapters.repository.workflow.job;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.otto.platform.gitactionboard.domain.workflow.RunConclusion;
import de.otto.platform.gitactionboard.domain.workflow.RunStatus;
import de.otto.platform.gitactionboard.domain.workflow.WorkflowJob;
import java.time.Instant;
import java.util.Optional;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.springframework.data.relational.core.mapping.Table;

@Value
@Builder
@Table("workflow_jobs")
public class WorkflowJobRecord {
  long id;

  @NonNull String name;

  @NonNull String status;

  String conclusion;

  @NonNull
  @JsonProperty("started_at")
  Instant startedAt;

  @JsonProperty("completed_at")
  Instant completedAt;

  @NonNull
  @JsonProperty("workflow_id")
  Long workflowId;

  @NonNull
  @JsonProperty("workflow_run_id")
  Long workflowRunId;

  @NonNull String url;

  @NonNull
  @JsonProperty("run_attempt")
  Integer runAttempt;

  public static WorkflowJobRecord from(WorkflowJob workflowJob) {
    return WorkflowJobRecord.builder()
        .id(workflowJob.getId())
        .name(workflowJob.getName())
        .status(workflowJob.getStatus().name())
        .conclusion(Optional.ofNullable(workflowJob.getConclusion()).map(Enum::name).orElse(null))
        .startedAt(workflowJob.getStartedAt())
        .completedAt(workflowJob.getCompletedAt())
        .workflowId(workflowJob.getWorkflowId())
        .workflowRunId(workflowJob.getWorkflowRunId())
        .url(workflowJob.getUrl())
        .runAttempt(workflowJob.getRunAttempt())
        .build();
  }

  public WorkflowJob toWorkflowJob() {
    return WorkflowJob.builder()
        .id(id)
        .name(name)
        .conclusion(Optional.ofNullable(conclusion).map(RunConclusion::valueOf).orElse(null))
        .startedAt(startedAt)
        .completedAt(completedAt)
        .status(RunStatus.valueOf(status))
        .workflowId(workflowId)
        .workflowRunId(workflowRunId)
        .url(url)
        .runAttempt(runAttempt)
        .build();
  }
}
