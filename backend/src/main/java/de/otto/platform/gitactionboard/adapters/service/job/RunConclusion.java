package de.otto.platform.gitactionboard.adapters.service.job;

import com.fasterxml.jackson.annotation.JsonProperty;
import de.otto.platform.gitactionboard.domain.Status;
import java.util.Objects;

public enum RunConclusion {
  @JsonProperty("success")
  SUCCESS,

  @JsonProperty("failure")
  FAILURE,

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
    switch (conclusion) {
      case SUCCESS:
      case SKIPPED:
        return Status.SUCCESS;
      case FAILURE:
      case CANCELLED:
      case TIMED_OUT:
        return Status.FAILURE;
      case ACTION_REQUIRED:
        return Status.EXCEPTION;
      default:
        return Status.UNKNOWN;
    }
  }
}
