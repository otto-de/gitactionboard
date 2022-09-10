package de.otto.platform.gitactionboard.domain.service.notifications;

import de.otto.platform.gitactionboard.domain.scan.code.violations.CodeStandardViolationDetails;
import de.otto.platform.gitactionboard.domain.scan.secrets.SecretsScanDetails;
import de.otto.platform.gitactionboard.domain.workflow.JobDetails;

public interface NotificationConnector {
  void notify(JobDetails jobDetails);

  void notify(SecretsScanDetails secretsScanDetails);

  void notify(CodeStandardViolationDetails codeStandardViolationDetails);

  String getType();
}
