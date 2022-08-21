package de.otto.platform.gitactionboard.adapters.service.job;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.otto.platform.gitactionboard.domain.workflow.Activity;

public enum RunStatus {
  @JsonProperty("queued")
  QUEUED,

  @JsonProperty("in_progress")
  IN_PROGRESS,

  @JsonProperty("completed")
  COMPLETED;

  public static Activity getActivity(RunStatus runStatus) {
    return RunStatus.COMPLETED.equals(runStatus) ? Activity.SLEEPING : Activity.BUILDING;
  }
}
