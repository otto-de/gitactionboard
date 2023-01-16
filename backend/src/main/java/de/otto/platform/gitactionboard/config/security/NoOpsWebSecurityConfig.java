package de.otto.platform.gitactionboard.config.security;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@Conditional(DisableAuthentication.class)
@Slf4j
public class NoOpsWebSecurityConfig {
  @PostConstruct
  @SuppressWarnings("PMD.UnusedPrivateMethod")
  private void logInfo() {
    log.info(
        "Disabled authentication mechanism as value is missing for GITHUB_OAUTH2_CLIENT_ID and BASIC_AUTH_USER_DETAILS_FILE_PATH property");
  }

  @Bean
  public SecurityFilterChain permitAll(HttpSecurity http) throws Exception {
    http.cors().disable().formLogin().disable().authorizeHttpRequests().anyRequest().permitAll();

    return http.build();
  }
}
