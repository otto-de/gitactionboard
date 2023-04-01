package de.otto.platform.gitactionboard.adapters.controller;

import static de.otto.platform.gitactionboard.domain.AuthenticationMechanism.BASIC_AUTH;
import static de.otto.platform.gitactionboard.domain.AuthenticationMechanism.OAUTH2;
import static org.assertj.core.api.Assertions.assertThat;

import de.otto.platform.gitactionboard.Parallel;
import de.otto.platform.gitactionboard.domain.AuthenticationMechanism;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@Parallel
class ConfigurationControllerTest {

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
            authenticationMechanisms, secretsScanEnabled, codeScanEnabled, projectVersion);
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
  }
}
