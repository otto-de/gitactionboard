package de.otto.platform.gitactionboard.fixtures;

import static de.otto.platform.gitactionboard.fixtures.SecretsScanDetailsFixtures.SECRETS_SCAN_DETAILS_CREATED_AT;
import static de.otto.platform.gitactionboard.fixtures.SecretsScanDetailsFixtures.SECRETS_SCAN_DETAILS_ID;
import static de.otto.platform.gitactionboard.fixtures.SecretsScanDetailsFixtures.SECRETS_SCAN_DETAILS_NAME;
import static de.otto.platform.gitactionboard.fixtures.SecretsScanDetailsFixtures.SECRETS_SCAN_DETAILS_URL;
import static de.otto.platform.gitactionboard.fixtures.WorkflowsFixture.REPO_NAME;

import de.otto.platform.gitactionboard.adapters.controller.SecretsScanAlert;

public class SecurityScanAlertFixtures {

  public static SecretsScanAlert.SecretsScanAlertBuilder getSecretsScanAlertBuilder() {
    return SecretsScanAlert.builder()
        .id("%s::%s::%d".formatted(REPO_NAME, SECRETS_SCAN_DETAILS_NAME, SECRETS_SCAN_DETAILS_ID))
        .url(SECRETS_SCAN_DETAILS_URL)
        .name("%s :: %s".formatted(REPO_NAME, SECRETS_SCAN_DETAILS_NAME))
        .createdAt(SECRETS_SCAN_DETAILS_CREATED_AT);
  }
}
