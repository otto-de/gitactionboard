package de.otto.platform.gitactionboard.adapters.service.notifications;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import de.otto.platform.gitactionboard.domain.scan.code.violations.CodeStandardViolationDetails;
import de.otto.platform.gitactionboard.domain.scan.secrets.SecretsScanDetails;
import de.otto.platform.gitactionboard.domain.service.notifications.NotificationConnector;
import de.otto.platform.gitactionboard.domain.workflow.JobDetails;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@Slf4j
@SuppressFBWarnings("EI_EXPOSE_REP2")
@Deprecated(forRemoval = true, since = "5.0.0")
@ConditionalOnProperty("MS_TEAMS_NOTIFICATIONS_WEB_HOOK_URL")
public class TeamsWebHookNotificationConnector implements NotificationConnector {
  private static final String CONNECTOR_TYPE = "MS_TEAMS_WEB_HOOK";
  private final String webHookUrl;
  private final RestClient restClient;

  @Autowired
  public TeamsWebHookNotificationConnector(
      RestClient.Builder restClientBuilder,
      @Value("${MS_TEAMS_NOTIFICATIONS_WEB_HOOK_URL}") String webHookUrl) {
    restClient = restClientBuilder.build();
    this.webHookUrl = webHookUrl;
  }

  @Override
  public void notify(JobDetails jobDetails) {
    log.info("Sending notification for {}", jobDetails);

    notify(TeamsNotificationMessagePayload.from(jobDetails));
  }

  private void notify(TeamsNotificationMessagePayload messagePayload) {
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
    notify(TeamsNotificationMessagePayload.from(secretsScanDetails));
  }

  @Override
  public void notify(CodeStandardViolationDetails codeStandardViolationDetails) {
    notify(TeamsNotificationMessagePayload.from(codeStandardViolationDetails));
  }

  @Override
  public String getType() {
    return CONNECTOR_TYPE;
  }

  @PostConstruct
  private void postConstruct() {
    log.warn(
        "MS_TEAMS_NOTIFICATIONS_WEB_HOOK_URL is deprecated and will be removed in a future version. Please switch to MS_TEAMS_NOTIFICATIONS_WORKFLOW_URL");
  }
}
