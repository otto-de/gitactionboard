package de.otto.platform.gitactionboard.domain.scan;

import static de.otto.platform.gitactionboard.fixtures.SecretsScanDetailsFixtures.getSecretsScanDetailsBuilder;
import static de.otto.platform.gitactionboard.fixtures.SecurityScanAlertFixtures.getSecurityScanAlertBuilder;
import static org.assertj.core.api.Assertions.assertThat;

import de.otto.platform.gitactionboard.domain.scan.secrets.SecretsScanDetails;
import org.junit.jupiter.api.Test;

class SecurityScanAlertTest {
  @Test
  void shouldConvertFromSecretsScanDetails() {
    final SecretsScanDetails secretsScanDetails = getSecretsScanDetailsBuilder().build();
    final SecurityScanAlert securityScanAlert = SecurityScanAlert.from(secretsScanDetails);

    assertThat(securityScanAlert).isEqualTo(getSecurityScanAlertBuilder().build());
  }
}
