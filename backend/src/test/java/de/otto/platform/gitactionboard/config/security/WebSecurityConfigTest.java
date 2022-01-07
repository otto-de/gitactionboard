package de.otto.platform.gitactionboard.config.security;

import static de.otto.platform.gitactionboard.domain.AuthenticationMechanism.BASIC_AUTH;
import static de.otto.platform.gitactionboard.domain.AuthenticationMechanism.OAUTH2;
import static org.assertj.core.api.Assertions.assertThat;

import de.otto.platform.gitactionboard.domain.AuthenticationMechanism;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class WebSecurityConfigTest {
  public static Stream<Arguments> getConfig() {
    return Stream.of(
        Arguments.of("basic_details", "random-client-id", List.of(OAUTH2, BASIC_AUTH)),
        Arguments.of(null, "random-client-id", List.of(OAUTH2)),
        Arguments.of("basic_details", "-", List.of(BASIC_AUTH)),
        Arguments.of("", "-", List.of()),
        Arguments.of("     ", "-", List.of()),
        Arguments.of(null, "-", List.of()));
  }

  @ParameterizedTest
  @MethodSource("getConfig")
  void shouldGiveAvailableAuths(
      String basicAuthFilePath,
      String githubClientId,
      List<AuthenticationMechanism> expectedAuths) {
    final WebSecurityConfig webSecurityConfig = new WebSecurityConfig();
    assertThat(webSecurityConfig.availableAuths(basicAuthFilePath, githubClientId))
        .hasSameSizeAs(expectedAuths)
        .containsAll(expectedAuths);
  }
}
