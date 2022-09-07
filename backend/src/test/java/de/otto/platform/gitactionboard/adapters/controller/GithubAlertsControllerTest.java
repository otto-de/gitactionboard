package de.otto.platform.gitactionboard.adapters.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN;
import static org.springframework.http.HttpStatus.OK;

import de.otto.platform.gitactionboard.Sequential;
import de.otto.platform.gitactionboard.domain.scan.secrets.SecretsScanDetails;
import de.otto.platform.gitactionboard.domain.service.SecretsScanService;
import de.otto.platform.gitactionboard.fixtures.SecretsScanDetailsFixtures;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
@Sequential
class GithubAlertsControllerTest {
  @Mock private SecretsScanService secretsScanService;
  private GithubAlertsController githubAlertsController;

  @BeforeEach
  void setUp() {
    githubAlertsController = new GithubAlertsController(secretsScanService);
  }

  @ParameterizedTest
  @NullSource
  @CsvSource(value = {"accessToken"})
  void shouldFetchSecurityScanAlertsAsJson(String accessToken) {
    final SecretsScanDetails scanDetails =
        SecretsScanDetailsFixtures.getSecretsScanDetailsBuilder().build();
    final List<SecretsScanDetails> secretsScanDetails = List.of(scanDetails);

    when(secretsScanService.fetchExposedSecrets(accessToken)).thenReturn(secretsScanDetails);

    final ResponseEntity<List<SecretsScanAlert>> securityScanAlertsResponse =
        githubAlertsController.getSecretsScanAlerts(accessToken);

    assertThat(securityScanAlertsResponse.getStatusCode()).isEqualTo(OK);
    assertThat(securityScanAlertsResponse.getHeaders())
        .containsEntry(ACCESS_CONTROL_ALLOW_ORIGIN, List.of("*"));
    assertThat(securityScanAlertsResponse.getBody())
        .hasSameSizeAs(secretsScanDetails)
        .allSatisfy(
            secretsScanAlert -> {
              assertThat(secretsScanAlert.getId())
                  .isEqualTo(
                      "%s::%s::%d",
                      scanDetails.getRepoName(), scanDetails.getName(), scanDetails.getId());
              assertThat(secretsScanAlert.getUrl()).isEqualTo(scanDetails.getUrl());
              assertThat(secretsScanAlert.getName())
                  .isEqualTo("%s :: %s", scanDetails.getRepoName(), scanDetails.getName());
              assertThat(secretsScanAlert.getCreatedAt()).isEqualTo(scanDetails.getCreatedAt());
            });
  }
}
