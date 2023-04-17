package de.otto.platform.gitactionboard.adapters.service.job;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.otto.platform.gitactionboard.domain.workflow.Status;
import java.util.Objects;

public enum RunConclusion {
  @JsonProperty("success")
  SUCCESS,

  @JsonProperty("failure")
  FAILURE,

  @JsonProperty("startup_failure")
  STARTUP_FAILURE,

  @JsonProperty("neutral")
  NEUTRAL,

  @JsonProperty("cancelled")
  CANCELLED,

  @JsonProperty("skipped")
  SKIPPED,

  @JsonProperty("timed_out")
  TIMED_OUT,

  @JsonProperty("action_required")
  ACTION_REQUIRED;

  public static Status getStatus(RunConclusion conclusion) {
    if (Objects.isNull(conclusion)) {
      return Status.UNKNOWN;
    }
    return switch (conclusion) {
      case SUCCESS, SKIPPED -> Status.SUCCESS;
      case FAILURE, STARTUP_FAILURE, CANCELLED, TIMED_OUT -> Status.FAILURE;
      case ACTION_REQUIRED -> Status.EXCEPTION;
      default -> Status.UNKNOWN;
    };
  }
}
