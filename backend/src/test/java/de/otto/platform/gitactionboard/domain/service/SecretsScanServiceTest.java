package de.otto.platform.gitactionboard.domain.service;

import static de.otto.platform.gitactionboard.fixtures.SecretsScanDetailsFixtures.REPO_NAME;
import static de.otto.platform.gitactionboard.fixtures.SecretsScanDetailsFixtures.SECRETS_SCAN_DETAILS_ID;
import static de.otto.platform.gitactionboard.fixtures.SecretsScanDetailsFixtures.SECRETS_SCAN_DETAILS_NAME;
import static de.otto.platform.gitactionboard.fixtures.SecretsScanDetailsFixtures.getSecretsScanDetailsBuilder;
import static de.otto.platform.gitactionboard.fixtures.SecurityScanAlertFixtures.getSecurityScanAlertBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.ACCESS_TOKEN;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SecretsScanServiceTest {

  @Mock private SecretsScanDetailsService secretsScanDetailsService;

  @Test
  void shouldFetchSecretsScanAlerts() {
    final String repoName2 = "dummy";
    final SecretsScanService secretsScanService =
        new SecretsScanService(secretsScanDetailsService, List.of(REPO_NAME, repoName2));

    when(secretsScanDetailsService.fetchSecretsScanDetails(REPO_NAME, ACCESS_TOKEN))
        .thenReturn(
            CompletableFuture.completedFuture(List.of(getSecretsScanDetailsBuilder().build())));

    when(secretsScanDetailsService.fetchSecretsScanDetails(repoName2, ACCESS_TOKEN))
        .thenReturn(
            CompletableFuture.completedFuture(
                List.of(getSecretsScanDetailsBuilder().repoName(repoName2).build())));

    assertThat(secretsScanService.fetchSecurityScanAlerts(ACCESS_TOKEN))
        .hasSize(2)
        .containsExactlyInAnyOrder(
            getSecurityScanAlertBuilder().build(),
            getSecurityScanAlertBuilder()
                .name(String.format("%s :: %s", repoName2, SECRETS_SCAN_DETAILS_NAME))
                .id(
                    String.format(
                        "%s::%s::%d",
                        repoName2, SECRETS_SCAN_DETAILS_NAME, SECRETS_SCAN_DETAILS_ID))
                .build());
  }
}
