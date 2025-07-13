package de.otto.platform.gitactionboard.adapters.controller;

import static de.otto.platform.gitactionboard.domain.AuthenticationMechanism.BASIC_AUTH;
import static de.otto.platform.gitactionboard.domain.AuthenticationMechanism.OAUTH2;
import static de.otto.platform.gitactionboard.fixtures.JobFixture.BRANCH;
import static de.otto.platform.gitactionboard.fixtures.WorkflowsFixture.REPO_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import de.otto.platform.gitactionboard.domain.AuthenticationMechanism;
import de.otto.platform.gitactionboard.domain.service.ConfigurationService;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ConfigurationControllerTest {

  private static final String ACCESS_TOKEN = "accessToken";

  @Mock private ConfigurationService configurationService;

  public static Stream<Arguments> getArguments() {
    final String projectVersion = "3.1.0";
    return Stream.of(
        Arguments.of(
            List.of(BASIC_AUTH),
            false,
            false,
            projectVersion,
            List.of(BASIC_AUTH),
            false,
            false,
            projectVersion),
        Arguments.of(
            List.of(OAUTH2),
            false,
            false,
            projectVersion,
            List.of(OAUTH2),
            false,
            false,
            projectVersion),
        Arguments.of(
            List.of(BASIC_AUTH, OAUTH2),
            false,
            false,
            projectVersion,
            List.of(BASIC_AUTH, OAUTH2),
            false,
            false,
            projectVersion),
        Arguments.of(
            List.of(), true, false, projectVersion, List.of(), true, false, projectVersion),
        Arguments.of(List.of(), false, true, "unknown", List.of(), false, true, "unknown"));
  }

  @ParameterizedTest
  @MethodSource("getArguments")
  void shouldGiveValidConfig(
      List<AuthenticationMechanism> authenticationMechanisms,
      Boolean secretsScanEnabled,
      Boolean codeScanEnabled,
      String projectVersion,
      List<AuthenticationMechanism> expectedAuthenticationMechanisms,
      boolean expectedSecretsScanEnabled,
      boolean expectedCodeScanEnabled,
      String expectedProjectVersion) {

    final ConfigurationController controller =
        new ConfigurationController(
            configurationService,
            authenticationMechanisms,
            secretsScanEnabled,
            codeScanEnabled,
            projectVersion,
            List.of());
    assertThat(controller.getAvailableConfig())
        .satisfies(
            config -> {
              assertThat(config.getAvailableAuths()).isEqualTo(expectedAuthenticationMechanisms);
              assertThat(config.getGithubSecretsScanMonitoringEnabled())
                  .isEqualTo(expectedSecretsScanEnabled);
              assertThat(config.getGithubCodeScanMonitoringEnabled())
                  .isEqualTo(expectedCodeScanEnabled);
              assertThat(config.getVersion()).isEqualTo(expectedProjectVersion);
            });

    verifyNoInteractions(configurationService);
  }

  @Test
  void shouldReturnListOfRepositoriesName() {
    final List<String> repoNames = List.of(REPO_NAME, "dummy");
    final ConfigurationController configurationController =
        new ConfigurationController(configurationService, List.of(), false, null, null, repoNames);

    assertThat(configurationController.getRepositoryNames()).isEqualTo(repoNames);

    verifyNoInteractions(configurationService);
  }

  @Test
  void shouldReturnListOfAvailableBranchNames() {
    final List<String> repoNames = List.of(REPO_NAME, "dummy");
    final ConfigurationController configurationController =
        new ConfigurationController(configurationService, List.of(), false, null, null, repoNames);
    final List<String> branchNames = List.of(BRANCH, "DUMMY_BRANCH");

    when(configurationService.getBranchNames(ACCESS_TOKEN)).thenReturn(branchNames);

    assertThat(configurationController.getBranchNames(ACCESS_TOKEN)).isEqualTo(branchNames);
  }
}
