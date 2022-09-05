package de.otto.platform.gitactionboard.domain.service;

import de.otto.platform.gitactionboard.domain.scan.secrets.SecretsScanDetails;
import de.otto.platform.gitactionboard.domain.workflow.JobDetails;

public interface NotificationConnector {
  void notify(JobDetails jobDetails);

  void notify(SecretsScanDetails secretsScanDetails);

  String getType();
}
