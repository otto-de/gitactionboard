package de.otto.platform.gitactionboard.adapters.service.notifications;

import static de.otto.platform.gitactionboard.fixtures.JobFixture.getJobDetailsBuilder;
import static de.otto.platform.gitactionboard.fixtures.NotificationMessagePayloadFixture.getNotificationMessagePayloadBuilder;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class NotificationMessagePayloadTest {
  @Test
  void shouldCreateNotificationPayloadForGivenJobDetails() {
    final TeamsNotificationMessagePayload expectedMessagePayload =
        getNotificationMessagePayloadBuilder().build();

    assertThat(TeamsNotificationMessagePayload.from(getJobDetailsBuilder().build()))
        .isEqualTo(expectedMessagePayload);
  }
}
