package de.otto.platform.gitactionboard.adapters.service.job;

import com.fasterxml.jackson.annotation.JsonProperty;
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
  }
}
