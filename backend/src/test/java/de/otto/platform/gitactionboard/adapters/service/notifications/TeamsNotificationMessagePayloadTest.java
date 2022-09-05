package de.otto.platform.gitactionboard.adapters.service.notifications;

import static de.otto.platform.gitactionboard.fixtures.TeamsNotificationsMessagePayloadFixture.getTeamsNotificationPayloadBuilderForJob;
import static de.otto.platform.gitactionboard.fixtures.TeamsNotificationsMessagePayloadFixture.getTeamsNotificationPayloadBuilderForSecret;
import static org.assertj.core.api.Assertions.assertThat;

import de.otto.platform.gitactionboard.domain.scan.secrets.SecretsScanDetails;
import de.otto.platform.gitactionboard.domain.workflow.JobDetails;
import de.otto.platform.gitactionboard.fixtures.JobDetailsFixture;
import de.otto.platform.gitactionboard.fixtures.SecretsScanDetailsFixtures;
import org.junit.jupiter.api.Test;

class TeamsNotificationMessagePayloadTest {

  @Test
  void shouldConvertSecretScanDetailsToPayload() {
    final SecretsScanDetails secretsScanDetails =
        SecretsScanDetailsFixtures.getSecretsScanDetailsBuilder().build();

    assertThat(TeamsNotificationMessagePayload.from(secretsScanDetails))
        .isEqualTo(getTeamsNotificationPayloadBuilderForSecret().build());
  }

  @Test
  void shouldConvertJobDetailsDetailsToPayload() {
    final JobDetails jobDetails = JobDetailsFixture.getJobDetailsBuilder().build();

    assertThat(TeamsNotificationMessagePayload.from(jobDetails))
        .isEqualTo(getTeamsNotificationPayloadBuilderForJob().build());
  }
}
