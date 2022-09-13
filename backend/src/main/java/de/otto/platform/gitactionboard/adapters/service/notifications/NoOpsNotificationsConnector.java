package de.otto.platform.gitactionboard.adapters.service.notifications;

import de.otto.platform.gitactionboard.domain.scan.code.violations.CodeStandardViolationDetails;
import de.otto.platform.gitactionboard.domain.scan.secrets.SecretsScanDetails;
import de.otto.platform.gitactionboard.domain.service.notifications.NotificationConnector;
import de.otto.platform.gitactionboard.domain.workflow.JobDetails;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@ConditionalOnMissingBean(TeamsWebHookNotificationConnector.class)
@Profile("local")
public class NoOpsNotificationsConnector implements NotificationConnector {
  @Override
  public void notify(JobDetails jobDetails) {
    log.info("Sent notification for {}", jobDetails);
  }

  @Override
  public void notify(SecretsScanDetails secretsScanDetails) {
    log.info("Sent notification for {}", secretsScanDetails);
  }

  @Override
  public void notify(CodeStandardViolationDetails codeStandardViolationDetails) {
    log.info("Sent notification for {}", codeStandardViolationDetails);
  }

  @Override
  public String getType() {
    return "NO_OPS";
  }
}
