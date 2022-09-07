package de.otto.platform.gitactionboard.adapters.service.notifications;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static de.otto.platform.gitactionboard.fixtures.JobDetailsFixture.getJobDetailsBuilder;
import static de.otto.platform.gitactionboard.fixtures.NotificationMessagePayloadFixture.getNotificationMessagePayloadBuilder;
import static org.apache.http.HttpStatus.SC_BAD_REQUEST;
import static org.apache.http.HttpStatus.SC_OK;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.TEXT_PLAIN_VALUE;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.otto.platform.gitactionboard.IntegrationTest;
import de.otto.platform.gitactionboard.WireMockExtension;
import de.otto.platform.gitactionboard.config.CodecConfig;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

@ExtendWith(WireMockExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@IntegrationTest
class TeamsWebHookNotificationConnectorIntegrationTest {

  @Autowired private TeamsWebHookNotificationConnector teamsWebHookNotificationConnector;

  @Value("${wiremock.webhook.url}")
  private String webHookUrl;

  private ObjectMapper objectMapper;

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
    stubFor(
        post(webHookUrl)
            .willReturn(
                aResponse()
                    .withStatus(SC_OK)
                    .withHeader(CONTENT_TYPE, TEXT_PLAIN_VALUE)
                    .withBody("1")));

    teamsWebHookNotificationConnector.notify(getJobDetailsBuilder().build());

    final TeamsNotificationMessagePayload expectedNotificationMessagePayload =
        getNotificationMessagePayloadBuilder().build();

    verify(
        1,
        postRequestedFor(urlEqualTo(webHookUrl))
            .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON_VALUE))
            .withRequestBody(equalToJson(getValueAsString(expectedNotificationMessagePayload))));
  }

  @SneakyThrows
  private String getValueAsString(TeamsNotificationMessagePayload notificationMessagePayload) {
    return objectMapper.writeValueAsString(notificationMessagePayload);
  }

  @Test
  void shouldReturnFalseIfUnableToSendNotificationForJob() {
    stubFor(
        post(webHookUrl)
            .willReturn(
                aResponse()
                    .withStatus(SC_BAD_REQUEST)
                    .withHeader(CONTENT_TYPE, TEXT_PLAIN_VALUE)
                    .withBody("Bad Request")));

    assertThatExceptionOfType(RuntimeException.class)
        .isThrownBy(() -> teamsWebHookNotificationConnector.notify(getJobDetailsBuilder().build()))
        .withMessageContaining("Bad Request");

    final TeamsNotificationMessagePayload expectedNotificationMessagePayload =
        getNotificationMessagePayloadBuilder().build();
    verify(
        1,
        postRequestedFor(urlEqualTo(webHookUrl))
            .withHeader(CONTENT_TYPE, equalTo(APPLICATION_JSON_VALUE))
            .withRequestBody(equalToJson(getValueAsString(expectedNotificationMessagePayload))));
  }
}
