package de.otto.platform.gitactionboard.adapters.service.notifications;

import static de.otto.platform.gitactionboard.TestUtil.mockRequest;
import static de.otto.platform.gitactionboard.fixtures.JobDetailsFixture.getJobDetailsBuilder;
import static de.otto.platform.gitactionboard.fixtures.NotificationMessagePayloadFixture.getNotificationMessagePayloadBuilder;
import static jakarta.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.verify.VerificationTimes.once;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.otto.platform.gitactionboard.IntegrationTest;
import de.otto.platform.gitactionboard.config.CodecConfig;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockserver.client.MockServerClient;
import org.mockserver.springtest.MockServerTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@IntegrationTest
@MockServerTest
@SuppressFBWarnings("NP_UNWRITTEN_FIELD")
class TeamsWebHookNotificationConnectorIntegrationTest {

  @Autowired private TeamsWebHookNotificationConnector teamsWebHookNotificationConnector;

  @Value("${wiremock.webhook.url}")
  private String webHookUrl;

  private ObjectMapper objectMapper;

  @SuppressFBWarnings("UWF_UNWRITTEN_FIELD")
  private MockServerClient mockServerClient;

  @BeforeEach
  void setUp() {
    objectMapper = CodecConfig.OBJECT_MAPPER_BUILDER.build();
  }

  @Test
  void shouldReturnConnectorType() {
    assertThat(teamsWebHookNotificationConnector.getType()).isEqualTo("MS_TEAMS_WEB_HOOK");
  }

  @Test
  @SneakyThrows
  void shouldSendNotificationToGivenWebHookUrl() {
    mockRequest(
        mockServerClient,
        request().withMethod("POST").withPath(webHookUrl),
        SC_OK,
        "1",
        TEXT_PLAIN_VALUE);

    teamsWebHookNotificationConnector.notify(getJobDetailsBuilder().build());

    final TeamsNotificationMessagePayload expectedNotificationMessagePayload =
        getNotificationMessagePayloadBuilder().build();

    verifyMockServerRequest(expectedNotificationMessagePayload);
  }

  @SneakyThrows
  private String getValueAsString(TeamsNotificationMessagePayload notificationMessagePayload) {
    return objectMapper.writeValueAsString(notificationMessagePayload);
  }

  @Test
  void shouldReturnFalseIfUnableToSendNotificationForJob() {
    mockRequest(
        mockServerClient,
        request().withMethod("POST").withPath(webHookUrl),
        SC_BAD_REQUEST,
        "Bad Request",
        TEXT_PLAIN_VALUE);

    assertThatExceptionOfType(RuntimeException.class)
        .isThrownBy(() -> teamsWebHookNotificationConnector.notify(getJobDetailsBuilder().build()))
        .withMessageContaining("Bad Request");

    final TeamsNotificationMessagePayload expectedNotificationMessagePayload =
        getNotificationMessagePayloadBuilder().build();

    verifyMockServerRequest(expectedNotificationMessagePayload);
  }

  private void verifyMockServerRequest(
      TeamsNotificationMessagePayload expectedNotificationMessagePayload) {
    mockServerClient.verify(
        request()
            .withMethod("POST")
            .withBody(getValueAsString(expectedNotificationMessagePayload))
            .withPath(webHookUrl)
            .withHeader(CONTENT_TYPE, APPLICATION_JSON_VALUE),
        once());
  }
}
