package de.otto.platform.gitactionboard.adapters.controller;

import static de.otto.platform.gitactionboard.domain.AuthenticationMechanism.BASIC_AUTH;
import static de.otto.platform.gitactionboard.domain.AuthenticationMechanism.OAUTH2;
import static org.assertj.core.api.Assertions.assertThat;

import de.otto.platform.gitactionboard.domain.AuthenticationMechanism;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ConfigurationControllerTest {

  private static final String FALSE_TEXT_VALUE = "false";

  public static Stream<Arguments> getArguments() {
    return Stream.of(
        Arguments.of(List.of(BASIC_AUTH), FALSE_TEXT_VALUE, List.of(BASIC_AUTH), false),
        Arguments.of(List.of(OAUTH2), FALSE_TEXT_VALUE, List.of(OAUTH2), false),
        Arguments.of(
            List.of(BASIC_AUTH, OAUTH2), FALSE_TEXT_VALUE, List.of(BASIC_AUTH, OAUTH2), false),
        Arguments.of(List.of(), FALSE_TEXT_VALUE, List.of(), false),
        Arguments.of(List.of(), "true", List.of(), true),
        Arguments.of(List.of(), "", List.of(), false),
        Arguments.of(List.of(), "null", List.of(), false));
  }

  @ParameterizedTest
  @MethodSource("getArguments")
  void shouldGiveValidConfig(
      List<AuthenticationMechanism> authenticationMechanisms,
      String githubSecretsScanMonitoringEnabled,
      List<AuthenticationMechanism> expectedAuthenticationMechanisms,
      boolean expectedGithubSecretsScanMonitoringEnabled) {

    final ConfigurationController controller =
        new ConfigurationController(authenticationMechanisms, githubSecretsScanMonitoringEnabled);
    assertThat(controller.getAvailableConfig())
        .satisfies(
            config -> {
              assertThat(config.getAvailableAuths()).isEqualTo(expectedAuthenticationMechanisms);
              assertThat(config.getGithubSecretsScanMonitoringEnabled())
                  .isEqualTo(expectedGithubSecretsScanMonitoringEnabled);
            });
  }
}
