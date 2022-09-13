package de.otto.platform.gitactionboard.domain.service;

import static de.otto.platform.gitactionboard.fixtures.SecretsScanDetailsFixtures.SECRETS_SCAN_DETAILS_ID;
import static de.otto.platform.gitactionboard.fixtures.SecretsScanDetailsFixtures.getSecretsScanDetailsBuilder;
import static de.otto.platform.gitactionboard.fixtures.WorkflowsFixture.REPO_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.ACCESS_TOKEN;

import de.otto.platform.gitactionboard.Sequential;
import de.otto.platform.gitactionboard.domain.scan.secrets.SecretsScanDetails;
import de.otto.platform.gitactionboard.domain.service.notifications.NotificationsService;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Sequential
class SecretsScanServiceTest {

  @Mock private SecretsScanDetailsService secretsScanDetailsService;
  @Mock private NotificationsService notificationsService;

  @Test
  void shouldFetchSecretsScanAlerts() {
    final String repoName2 = "dummy";
    final SecretsScanService secretsScanService =
        new SecretsScanService(
            secretsScanDetailsService, notificationsService, List.of(REPO_NAME, repoName2));

    when(secretsScanDetailsService.fetchSecretsScanDetails(REPO_NAME, ACCESS_TOKEN))
        .thenReturn(
            CompletableFuture.completedFuture(List.of(getSecretsScanDetailsBuilder().build())));

    when(secretsScanDetailsService.fetchSecretsScanDetails(repoName2, ACCESS_TOKEN))
        .thenReturn(
            CompletableFuture.completedFuture(
                List.of(getSecretsScanDetailsBuilder().repoName(repoName2).build())));
    final List<SecretsScanDetails> expectedSecretScanDetails =
        List.of(
            getSecretsScanDetailsBuilder().build(),
            getSecretsScanDetailsBuilder().repoName(repoName2).id(SECRETS_SCAN_DETAILS_ID).build());

    assertThat(secretsScanService.fetchExposedSecrets(ACCESS_TOKEN))
        .hasSize(2)
        .containsExactlyInAnyOrderElementsOf(expectedSecretScanDetails);
    verify(notificationsService, times(1))
        .sendNotificationsForSecretScanAlerts(expectedSecretScanDetails);
  }
}
