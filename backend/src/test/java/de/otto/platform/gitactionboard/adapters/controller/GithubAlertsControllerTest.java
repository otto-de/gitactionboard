package de.otto.platform.gitactionboard.adapters.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import de.otto.platform.gitactionboard.Sequential;
import de.otto.platform.gitactionboard.domain.scan.code.violations.CodeStandardViolationDetails;
import de.otto.platform.gitactionboard.domain.scan.secrets.SecretsScanDetails;
import de.otto.platform.gitactionboard.domain.service.CodeStandardViolationsScanService;
import de.otto.platform.gitactionboard.domain.service.SecretsScanService;
import de.otto.platform.gitactionboard.fixtures.CodeStandardViolationFixture;
import de.otto.platform.gitactionboard.fixtures.SecretsScanDetailsFixtures;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
  @Mock private CodeStandardViolationsScanService codeStandardViolationsScanService;
  private GithubAlertsController githubAlertsController;

  @Nested
  class ScanEnabled {

    @BeforeEach
    void setUp() {
      githubAlertsController =
          new GithubAlertsController(
              secretsScanService, codeStandardViolationsScanService, true, true);
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

    @ParameterizedTest
    @NullSource
    @CsvSource(value = {"accessToken"})
    void shouldFetchCodeViolationAlertsAsJson(String accessToken) {
      final CodeStandardViolationDetails codeStandardViolationDetails =
          CodeStandardViolationFixture.getCodeStandardViolationDetailsBuilder().build();
      final List<CodeStandardViolationDetails> codeViolations =
          List.of(codeStandardViolationDetails);

      when(codeStandardViolationsScanService.fetchCodeViolations(accessToken))
          .thenReturn(codeViolations);

      final ResponseEntity<List<CodeStandardViolationAlert>> codeViolationAlertsResponse =
          githubAlertsController.getCodeStandardViolationAlerts(accessToken);

      assertThat(codeViolationAlertsResponse.getStatusCode()).isEqualTo(OK);
      assertThat(codeViolationAlertsResponse.getHeaders())
          .containsEntry(ACCESS_CONTROL_ALLOW_ORIGIN, List.of("*"));
      assertThat(codeViolationAlertsResponse.getBody())
          .hasSameSizeAs(codeViolations)
          .allSatisfy(
              codeStandardViolationAlert -> {
                assertThat(codeStandardViolationAlert.getId())
                    .isEqualTo(
                        "%s::%s::%d",
                        codeStandardViolationDetails.getRepoName(),
                        codeStandardViolationDetails.getName(),
                        codeStandardViolationDetails.getId());
                assertThat(codeStandardViolationAlert.getUrl())
                    .isEqualTo(codeStandardViolationDetails.getUrl());
                assertThat(codeStandardViolationAlert.getName())
                    .isEqualTo(
                        "%s :: %s :: %s",
                        codeStandardViolationDetails.getRepoName(),
                        codeStandardViolationDetails.getLocation(),
                        codeStandardViolationDetails.getName());
                assertThat(codeStandardViolationAlert.getCreatedAt())
                    .isEqualTo(codeStandardViolationDetails.getCreatedAt());
              });
    }
  }

  @Nested
  class ScanDisabled {

    @BeforeEach
    void setUp() {
      githubAlertsController =
          new GithubAlertsController(
              secretsScanService, codeStandardViolationsScanService, false, false);
    }

    @ParameterizedTest
    @NullSource
    @CsvSource(value = {"accessToken"})
    void shouldFetchSecurityScanAlertsAsJson(String accessToken) {
      final ResponseEntity<List<SecretsScanAlert>> securityScanAlertsResponse =
          githubAlertsController.getSecretsScanAlerts(accessToken);

      assertThat(securityScanAlertsResponse.getStatusCode()).isEqualTo(NOT_FOUND);

      verifyNoInteractions(secretsScanService);
    }

    @ParameterizedTest
    @NullSource
    @CsvSource(value = {"accessToken"})
    void shouldFetchCodeViolationAlertsAsJson(String accessToken) {
      final ResponseEntity<List<CodeStandardViolationAlert>> codeViolationAlertsResponse =
          githubAlertsController.getCodeStandardViolationAlerts(accessToken);

      assertThat(codeViolationAlertsResponse.getStatusCode()).isEqualTo(NOT_FOUND);
      verifyNoInteractions(codeStandardViolationsScanService);
    }
  }
}
