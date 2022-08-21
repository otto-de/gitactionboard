package de.otto.platform.gitactionboard.adapters.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN;
import static org.springframework.http.HttpStatus.OK;

import de.otto.platform.gitactionboard.domain.scan.SecurityScanAlert;
import de.otto.platform.gitactionboard.domain.service.SecretsScanService;
import de.otto.platform.gitactionboard.fixtures.SecurityScanAlertFixtures;
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
    final List<SecurityScanAlert> alerts =
        List.of(SecurityScanAlertFixtures.getSecurityScanAlertBuilder().build());

    when(secretsScanService.fetchSecurityScanAlerts(accessToken)).thenReturn(alerts);

    final ResponseEntity<List<SecurityScanAlert>> securityScanAlertsResponse =
        githubAlertsController.getSecurityScanAlerts(accessToken);

    assertThat(securityScanAlertsResponse.getStatusCode()).isEqualTo(OK);
    assertThat(securityScanAlertsResponse.getHeaders())
        .containsEntry(ACCESS_CONTROL_ALLOW_ORIGIN, List.of("*"));
    assertThat(securityScanAlertsResponse.getBody()).isEqualTo(alerts);
  }
}
