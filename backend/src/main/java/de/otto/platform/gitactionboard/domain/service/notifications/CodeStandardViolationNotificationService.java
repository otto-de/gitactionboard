package de.otto.platform.gitactionboard.domain.service.notifications;

import de.otto.platform.gitactionboard.domain.repository.NotificationRepository;
import de.otto.platform.gitactionboard.domain.scan.code.violations.CodeStandardViolationDetails;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CodeStandardViolationNotificationService
    extends BaseNotificationsService<CodeStandardViolationDetails> {

  @Autowired
  public CodeStandardViolationNotificationService(
      List<NotificationConnector> notificationConnectors,
      NotificationRepository notificationRepository) {
    super(notificationConnectors, notificationRepository);
  }

  @Override
  protected String getName(CodeStandardViolationDetails codeStandardViolationDetails) {
    return codeStandardViolationDetails.getFormattedName();
  }

  @Override
  protected void notify(
      CodeStandardViolationDetails codeStandardViolationDetails,
      NotificationConnector notificationConnector) {
    notificationConnector.notify(codeStandardViolationDetails);
  }
}
