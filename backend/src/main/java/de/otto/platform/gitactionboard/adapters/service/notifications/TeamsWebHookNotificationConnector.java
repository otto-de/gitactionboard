package de.otto.platform.gitactionboard.adapters.service.notifications;

import de.otto.platform.gitactionboard.domain.scan.code.violations.CodeStandardViolationDetails;
import de.otto.platform.gitactionboard.domain.scan.secrets.SecretsScanDetails;
import de.otto.platform.gitactionboard.domain.service.notifications.NotificationConnector;
import de.otto.platform.gitactionboard.domain.workflow.JobDetails;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@Slf4j
@ConditionalOnProperty("MS_TEAMS_NOTIFICATIONS_WEB_HOOK_URL")
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class TeamsWebHookNotificationConnector implements NotificationConnector {
  private static final String CONNECTOR_TYPE = "MS_TEAMS_WEB_HOOK";
  private final RestTemplate restTemplate;
  private final String webHookUrl;

  @Autowired
  public TeamsWebHookNotificationConnector(
      RestTemplateBuilder builder,
      @Value("${MS_TEAMS_NOTIFICATIONS_WEB_HOOK_URL}") String webHookUrl) {
    final ClientHttpRequestFactory requestFactory = builder.buildRequestFactory();
    final RestTemplateBuilder restTemplateBuilder =
        builder.requestFactory(() -> new BufferingClientHttpRequestFactory(requestFactory));
    this.restTemplate = restTemplateBuilder.build();
    this.webHookUrl = webHookUrl;
  }

  @Override
  public void notify(JobDetails jobDetails) {
    log.info("Sending notification for {}", jobDetails);

    notify(TeamsNotificationMessagePayload.from(jobDetails));
  }

  private void notify(TeamsNotificationMessagePayload messagePayload) {
    final ResponseEntity<String> responseEntity =
        restTemplate.postForEntity(webHookUrl, messagePayload, String.class);

    if (!responseEntity.getStatusCode().is2xxSuccessful())
      throw new RuntimeException(responseEntity.getBody());
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
}
