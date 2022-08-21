package de.otto.platform.gitactionboard.fixtures;

import static de.otto.platform.gitactionboard.fixtures.SecretsScanDetailsFixtures.REPO_NAME;
import static de.otto.platform.gitactionboard.fixtures.SecretsScanDetailsFixtures.SECRETS_SCAN_DETAILS_CREATED_AT;
import static de.otto.platform.gitactionboard.fixtures.SecretsScanDetailsFixtures.SECRETS_SCAN_DETAILS_ID;
import static de.otto.platform.gitactionboard.fixtures.SecretsScanDetailsFixtures.SECRETS_SCAN_DETAILS_NAME;
import static de.otto.platform.gitactionboard.fixtures.SecretsScanDetailsFixtures.SECRETS_SCAN_DETAILS_URL;

import de.otto.platform.gitactionboard.domain.scan.SecurityScanAlert;

public class SecurityScanAlertFixtures {

  public static SecurityScanAlert.SecurityScanAlertBuilder getSecurityScanAlertBuilder() {
    return SecurityScanAlert.builder()
        .id(
            String.format(
                "%s::%s::%d", REPO_NAME, SECRETS_SCAN_DETAILS_NAME, SECRETS_SCAN_DETAILS_ID))
        .url(SECRETS_SCAN_DETAILS_URL)
        .name(String.format("%s :: %s", REPO_NAME, SECRETS_SCAN_DETAILS_NAME))
        .createdAt(SECRETS_SCAN_DETAILS_CREATED_AT);
  }
}
