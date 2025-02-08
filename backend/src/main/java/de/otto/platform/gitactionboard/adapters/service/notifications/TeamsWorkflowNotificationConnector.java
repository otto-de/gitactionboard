package de.otto.platform.gitactionboard.adapters.service.notifications;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import de.otto.platform.gitactionboard.domain.scan.code.violations.CodeStandardViolationDetails;
import de.otto.platform.gitactionboard.domain.scan.secrets.SecretsScanDetails;
import de.otto.platform.gitactionboard.domain.service.notifications.NotificationConnector;
import de.otto.platform.gitactionboard.domain.workflow.JobDetails;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Slf4j
@ConditionalOnProperty("MS_TEAMS_NOTIFICATIONS_WORKFLOW_URL")
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class TeamsWorkflowNotificationConnector implements NotificationConnector {
  private static final String CONNECTOR_TYPE = "MS_TEAMS_WORKFLOW";
  private final String webHookUrl;
  private final RestClient restClient;

  @Autowired
  public TeamsWorkflowNotificationConnector(
      RestClient.Builder restClientBuilder,
      @Value("${MS_TEAMS_NOTIFICATIONS_WORKFLOW_URL}") String webHookUrl) {
    restClient = restClientBuilder.build();
    this.webHookUrl = webHookUrl;
  }

  @Override
  public void notify(JobDetails jobDetails) {
    log.info("Sending notification for {}", jobDetails);

    notify(TeamsWorkflowNotificationMessagePayload.from(jobDetails));
  }

  private void notify(TeamsWorkflowNotificationMessagePayload messagePayload) {
    restClient
        .post()
        .uri(webHookUrl)
        .body(messagePayload)
        .contentType(APPLICATION_JSON)
        .retrieve()
        .toBodilessEntity();
  }

  @Override
  public void notify(SecretsScanDetails secretsScanDetails) {
    notify(TeamsWorkflowNotificationMessagePayload.from(secretsScanDetails));
  }

  @Override
  public void notify(CodeStandardViolationDetails codeStandardViolationDetails) {
    notify(TeamsWorkflowNotificationMessagePayload.from(codeStandardViolationDetails));
  }

  @Override
  public String getType() {
    return CONNECTOR_TYPE;
  }
}
