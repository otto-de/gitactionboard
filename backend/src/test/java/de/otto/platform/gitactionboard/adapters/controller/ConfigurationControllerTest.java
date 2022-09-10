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
    return Stream.of(
        Arguments.of(List.of(BASIC_AUTH), false, false, List.of(BASIC_AUTH), false, false),
        Arguments.of(List.of(OAUTH2), false, false, List.of(OAUTH2), false, false),
        Arguments.of(
            List.of(BASIC_AUTH, OAUTH2), false, false, List.of(BASIC_AUTH, OAUTH2), false, false),
        Arguments.of(List.of(), true, false, List.of(), true, false),
        Arguments.of(List.of(), false, true, List.of(), false, true));
  }

  @ParameterizedTest
  @MethodSource("getArguments")
  void shouldGiveValidConfig(
      List<AuthenticationMechanism> authenticationMechanisms,
      Boolean secretsScanEnabled,
      Boolean codeScanEnabled,
      List<AuthenticationMechanism> expectedAuthenticationMechanisms,
      boolean expectedSecretsScanEnabled,
      boolean expectedCodeScanEnabled) {

    final ConfigurationController controller =
        new ConfigurationController(authenticationMechanisms, secretsScanEnabled, codeScanEnabled);
    assertThat(controller.getAvailableConfig())
        .satisfies(
            config -> {
              assertThat(config.getAvailableAuths()).isEqualTo(expectedAuthenticationMechanisms);
              assertThat(config.getGithubSecretsScanMonitoringEnabled())
                  .isEqualTo(expectedSecretsScanEnabled);
              assertThat(config.getGithubCodeScanMonitoringEnabled())
                  .isEqualTo(expectedCodeScanEnabled);
            });
  }
}
