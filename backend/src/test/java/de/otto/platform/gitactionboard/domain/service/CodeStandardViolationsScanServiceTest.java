package de.otto.platform.gitactionboard.domain.service;

import static de.otto.platform.gitactionboard.fixtures.CodeStandardViolationFixture.getCodeStandardViolationDetailsBuilder;
import static de.otto.platform.gitactionboard.fixtures.WorkflowsFixture.REPO_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.ACCESS_TOKEN;

import de.otto.platform.gitactionboard.Sequential;
import de.otto.platform.gitactionboard.domain.scan.code.violations.CodeStandardViolationDetails;
import de.otto.platform.gitactionboard.domain.service.notifications.CodeStandardViolationNotificationService;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Sequential
class CodeStandardViolationsScanServiceTest {
  @Mock private CodeStandardViolationsService codeStandardViolationsService;
  @Mock private CodeStandardViolationNotificationService notificationsService;

  @Test
  void shouldFetchSecretsScanAlerts() {
    final String repoName2 = "dummy";
    final CodeStandardViolationsScanService codeStandardViolationsScanService =
        new CodeStandardViolationsScanService(
            codeStandardViolationsService, notificationsService, List.of(REPO_NAME, repoName2));

    when(codeStandardViolationsService.fetchCodeViolations(REPO_NAME, ACCESS_TOKEN))
        .thenReturn(
            CompletableFuture.completedFuture(
                List.of(getCodeStandardViolationDetailsBuilder().build())));

    when(codeStandardViolationsService.fetchCodeViolations(repoName2, ACCESS_TOKEN))
        .thenReturn(
            CompletableFuture.completedFuture(
                List.of(getCodeStandardViolationDetailsBuilder().repoName(repoName2).build())));

    final List<CodeStandardViolationDetails> expectedCodeStandardViolations =
        List.of(
            getCodeStandardViolationDetailsBuilder().build(),
            getCodeStandardViolationDetailsBuilder().repoName(repoName2).build());
    assertThat(codeStandardViolationsScanService.fetchCodeViolations(ACCESS_TOKEN))
        .hasSize(2)
        .containsExactlyInAnyOrderElementsOf(expectedCodeStandardViolations);

    verify(notificationsService, times(1)).sendNotifications(expectedCodeStandardViolations);
  }
}
