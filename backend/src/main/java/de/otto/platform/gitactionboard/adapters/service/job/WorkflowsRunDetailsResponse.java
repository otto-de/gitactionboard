package de.otto.platform.gitactionboard.adapters.service.job;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.otto.platform.gitactionboard.domain.workflow.RunConclusion;
import de.otto.platform.gitactionboard.domain.workflow.RunStatus;
import de.otto.platform.gitactionboard.domain.workflow.WorkflowRun;
import java.time.Instant;
import java.util.List;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class WorkflowsRunDetailsResponse {
  @JsonProperty("workflow_runs")
  @NonNull
  List<WorkflowRunDetails> workflowRuns;

  @Value
  @Builder(toBuilder = true)
  public static class WorkflowRunDetails {
    public static final String COMPLETED = "completed";

    long id;

    @NonNull RunStatus status;

    RunConclusion conclusion;

    @JsonProperty("run_number")
    long runNumber;

    @NonNull
    @JsonProperty("updated_at")
    Instant updatedAt;

    @NonNull
    @JsonProperty("created_at")
    Instant createdAt;

    @NonNull
    @JsonProperty("html_url")
    String url;

    @NonNull
    @JsonProperty("event")
    String triggeredEvent;

    @NonNull
    @JsonProperty("run_attempt")
    Integer runAttempt;

    @NonNull
    @JsonProperty("workflow_id")
    Long workflowId;

    public WorkflowRun toWorkflowRun() {
      return WorkflowRun.builder()
          .id(id)
          .status(status)
          .conclusion(conclusion)
          .runNumber(runNumber)
          .updatedAt(updatedAt)
          .createdAt(createdAt)
          .triggeredEvent(triggeredEvent)
          .runAttempt(runAttempt)
          .workflowId(workflowId)
          .build();
    }
  }
}
