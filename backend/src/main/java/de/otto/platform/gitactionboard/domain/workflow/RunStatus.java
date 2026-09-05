package de.otto.platform.gitactionboard.domain.workflow;

public enum RunStatus {
  QUEUED,
  IN_PROGRESS,
  COMPLETED,
  WAITING;

  public Activity getActivity() {
    return this == RunStatus.COMPLETED ? Activity.SLEEPING : Activity.BUILDING;
  }
}
