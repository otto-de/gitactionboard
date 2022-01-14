package de.otto.platform.gitactionboard.config.security;

import static de.otto.platform.gitactionboard.domain.AuthenticationMechanism.BASIC_AUTH;
import static de.otto.platform.gitactionboard.domain.AuthenticationMechanism.OAUTH2;
import static org.assertj.core.api.Assertions.assertThat;

import de.otto.platform.gitactionboard.domain.AuthenticationMechanism;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

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

  @Nested
  @SuppressFBWarnings("SIC_INNER_SHOULD_BE_STATIC")
  class BasicAuthSecurityConfigTest {

    private WebSecurityConfig.BasicAuthSecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
      securityConfig = new WebSecurityConfig.BasicAuthSecurityConfig("");
    }

    @Test
    @SneakyThrows
    void shouldCreateUserDetailsFromBasicAuthFile() {
      final List<UserDetails> basicAuthUsers =
          securityConfig.getBasicAuthUsers("src/test/resources/.htpasswd");
      assertThat(basicAuthUsers)
          .hasSize(1)
          .first()
          .satisfies(
              userDetails -> {
                assertThat(userDetails.getUsername()).isEqualTo("admin");
                assertThat(userDetails.getPassword())
                    .isEqualTo("$2y$10$07K4E.DTA5vr/9lp/UAx3.dIgCsY9UK6iA/IW.fdebRjiFxgGAmTm");
                assertThat(userDetails.getAuthorities())
                    .hasSize(1)
                    .first()
                    .extracting(GrantedAuthority::getAuthority)
                    .isEqualTo("ROLE_USER");
              });
    }

    @Test
    void shouldUseBCryptPasswordEncoder() {
      assertThat(securityConfig.passwordEncoder()).isExactlyInstanceOf(BCryptPasswordEncoder.class);
    }

    @Test
    void shouldUseInMemoryUserDetailsManager() {
      assertThat(securityConfig.basicAuthUserDetailsManager(List.of()))
          .isExactlyInstanceOf(InMemoryUserDetailsManager.class);
    }
  }
}
