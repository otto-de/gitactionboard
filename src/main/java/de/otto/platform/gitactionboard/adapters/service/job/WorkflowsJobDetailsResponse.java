package de.otto.platform.gitactionboard.adapters.service.job;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.Instant;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;

@Data
@Builder
public class WorkflowsJobDetailsResponse {
  @NonNull List<WorkflowsJobDetails> jobs;

  @Data
  @Builder(toBuilder = true)
  public static class WorkflowsJobDetails {
    int id;

    @NonNull String name;

    @NonNull RunStatus status;

    RunConclusion conclusion;

    @NonNull
    @JsonProperty("started_at")
    Instant startedAt;

    @JsonProperty("completed_at")
    Instant completedAt;

    @NonNull
    @JsonProperty("html_url")
    String url;
  }
}
