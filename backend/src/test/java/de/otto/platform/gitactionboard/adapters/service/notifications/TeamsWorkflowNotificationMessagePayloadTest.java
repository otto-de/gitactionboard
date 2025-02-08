package de.otto.platform.gitactionboard.adapters.service.notifications;

import static de.otto.platform.gitactionboard.fixtures.CodeStandardViolationFixture.getCodeStandardViolationDetailsBuilder;
import static de.otto.platform.gitactionboard.fixtures.JobFixture.getJobDetailsBuilder;
import static de.otto.platform.gitactionboard.fixtures.SecretsScanDetailsFixtures.getSecretsScanDetailsBuilder;
import static de.otto.platform.gitactionboard.fixtures.TeamsWorkflowNotificationsMessagePayloadFixture.getTeamsNotificationPayloadBuilderForCodeStandardViolation;
import static de.otto.platform.gitactionboard.fixtures.TeamsWorkflowNotificationsMessagePayloadFixture.getTeamsNotificationPayloadBuilderForJob;
import static de.otto.platform.gitactionboard.fixtures.TeamsWorkflowNotificationsMessagePayloadFixture.getTeamsNotificationPayloadBuilderForSecret;
import static org.assertj.core.api.Assertions.assertThat;

import de.otto.platform.gitactionboard.domain.scan.code.violations.CodeStandardViolationDetails;
import de.otto.platform.gitactionboard.domain.scan.secrets.SecretsScanDetails;
import de.otto.platform.gitactionboard.domain.workflow.JobDetails;
import org.junit.jupiter.api.Test;

class TeamsWorkflowNotificationMessagePayloadTest {
  @Test
  void shouldConvertSecretScanDetailsToPayload() {
    final SecretsScanDetails secretsScanDetails = getSecretsScanDetailsBuilder().build();

    assertThat(TeamsWorkflowNotificationMessagePayload.from(secretsScanDetails))
        .isEqualTo(getTeamsNotificationPayloadBuilderForSecret().build());
  }

  @Test
  void shouldConvertJobDetailsToPayload() {
    final JobDetails jobDetails = getJobDetailsBuilder().build();

    assertThat(TeamsWorkflowNotificationMessagePayload.from(jobDetails))
        .isEqualTo(getTeamsNotificationPayloadBuilderForJob().build());
  }

  @Test
  void shouldConvertCodeStandardViolationToPayload() {
    final CodeStandardViolationDetails violationDetails =
        getCodeStandardViolationDetailsBuilder().build();

    assertThat(TeamsWorkflowNotificationMessagePayload.from(violationDetails))
        .isEqualTo(getTeamsNotificationPayloadBuilderForCodeStandardViolation().build());
  }
}
