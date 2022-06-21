package de.otto.platform.gitactionboard.domain.service;

import de.otto.platform.gitactionboard.domain.JobDetails;

public interface NotificationConnector {
  void notify(JobDetails jobDetails);

  String getType();
}
