package de.otto.platform.gitactionboard.fixtures;

import static de.otto.platform.gitactionboard.fixtures.WorkflowsFixture.REPO_NAME;

import de.otto.platform.gitactionboard.adapters.service.scan.secrets.SecretsScanDetailsResponse;
import de.otto.platform.gitactionboard.domain.scan.secrets.SecretsScanDetails;
import java.time.Instant;

public class SecretsScanDetailsFixtures {

  public static final Long SECRETS_SCAN_DETAILS_ID = 1L;
  public static final String SECRETS_SCAN_DETAILS_URL =
      "https://github.com/johndoe/hello-world/security/secret-scanning/1";
  public static final String SECRETS_SCAN_DETAILS_NAME = "Secret Access Key";
  public static final Instant SECRETS_SCAN_DETAILS_CREATED_AT =
      Instant.parse("2021-09-18T06:14:54.000Z");

  public static SecretsScanDetails.SecretsScanDetailsBuilder getSecretsScanDetailsBuilder() {
    return SecretsScanDetails.builder()
        .id(SECRETS_SCAN_DETAILS_ID)
        .url(SECRETS_SCAN_DETAILS_URL)
        .name(SECRETS_SCAN_DETAILS_NAME)
        .createdAt(SECRETS_SCAN_DETAILS_CREATED_AT)
        .repoName(REPO_NAME);
  }

  public static SecretsScanDetailsResponse.SecretsScanDetailsResponseBuilder
      getSecretsScanDetailsResponseBuilder() {
    return SecretsScanDetailsResponse.builder()
        .id(SECRETS_SCAN_DETAILS_ID)
        .url(SECRETS_SCAN_DETAILS_URL)
        .displayName(SECRETS_SCAN_DETAILS_NAME)
        .createdAt(SECRETS_SCAN_DETAILS_CREATED_AT);
  }
}
