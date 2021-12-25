package de.otto.platform.gitactionboard.config.security;

import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@Conditional(DisableAuthentication.class)
@Slf4j
public class NoOpsWebSecurityConfig extends WebSecurityConfigurerAdapter {

  @PostConstruct
  @SuppressWarnings("PMD.UnusedPrivateMethod")
  private void logInfo() {
    log.info("Disabled Github authentication as value is missing for GITHUB_OAUTH2_CLIENT_ID");
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.cors()
        .disable()
        .csrf()
        .disable()
        .formLogin()
        .disable()
        .authorizeRequests()
        .anyRequest()
        .permitAll();
  }
}
