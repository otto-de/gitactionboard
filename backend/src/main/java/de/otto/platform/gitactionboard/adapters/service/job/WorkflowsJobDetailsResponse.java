package de.otto.platform.gitactionboard.adapters.service.job;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.otto.platform.gitactionboard.domain.workflow.RunConclusion;
import de.otto.platform.gitactionboard.domain.workflow.RunStatus;
import de.otto.platform.gitactionboard.domain.workflow.WorkflowJob;
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
    long id;

    @NonNull String name;

    @NonNull RunStatus status;

    RunConclusion conclusion;

    @NonNull
    @JsonProperty("started_at")
    Instant startedAt;

    @JsonProperty("completed_at")
    Instant completedAt;

    @JsonProperty("run_id")
    @NonNull
    Long runId;

    @NonNull
    @JsonProperty("run_attempt")
    Integer runAttempt;

    @NonNull
    @JsonProperty("html_url")
    String url;

    public WorkflowJob toWorkflowJob(long workflowId) {
      return WorkflowJob.builder()
          .id(id)
          .name(name)
          .status(status)
          .conclusion(conclusion)
          .startedAt(startedAt)
          .completedAt(completedAt)
          .workflowId(workflowId)
          .workflowRunId(runId)
          .url(url)
          .runAttempt(runAttempt)
          .build();
    }
  }
}
