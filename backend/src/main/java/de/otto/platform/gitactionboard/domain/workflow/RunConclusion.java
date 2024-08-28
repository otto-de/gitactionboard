package de.otto.platform.gitactionboard.domain.workflow;

import java.util.Objects;

public enum RunConclusion {
  SUCCESS,
  FAILURE,
  STARTUP_FAILURE,
  NEUTRAL,
  CANCELLED,
  SKIPPED,
  TIMED_OUT,
  ACTION_REQUIRED;

  public static JobStatus getStatus(RunConclusion conclusion) {
    if (Objects.isNull(conclusion)) {
      return JobStatus.UNKNOWN;
    }
    return switch (conclusion) {
      case SUCCESS, SKIPPED -> JobStatus.SUCCESS;
      case FAILURE, STARTUP_FAILURE, CANCELLED, TIMED_OUT -> JobStatus.FAILURE;
      case ACTION_REQUIRED -> JobStatus.EXCEPTION;
      default -> JobStatus.UNKNOWN;
    };
  }
}
