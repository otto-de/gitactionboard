package de.otto.platform.gitactionboard.domain.scan;

import static de.otto.platform.gitactionboard.fixtures.SecretsScanDetailsFixtures.getSecretsScanDetailsBuilder;
import static de.otto.platform.gitactionboard.fixtures.SecurityScanAlertFixtures.getSecretsScanAlertBuilder;
import static org.assertj.core.api.Assertions.assertThat;

import de.otto.platform.gitactionboard.adapters.controller.SecretsScanAlert;
import de.otto.platform.gitactionboard.domain.scan.secrets.SecretsScanDetails;
import org.junit.jupiter.api.Test;

class SecretsScanAlertTest {
  @Test
  void shouldConvertFromSecretsScanDetails() {
    final SecretsScanDetails secretsScanDetails = getSecretsScanDetailsBuilder().build();
    final SecretsScanAlert secretsScanAlert = SecretsScanAlert.from(secretsScanDetails);

    assertThat(secretsScanAlert).isEqualTo(getSecretsScanAlertBuilder().build());
  }
}
