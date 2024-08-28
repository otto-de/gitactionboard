package de.otto.platform.gitactionboard.domain.workflow;

public enum RunStatus {
  QUEUED,
  IN_PROGRESS,
  COMPLETED,
  WAITING;

  public Activity getActivity() {
    return RunStatus.COMPLETED.equals(this) ? Activity.SLEEPING : Activity.BUILDING;
  }
}
