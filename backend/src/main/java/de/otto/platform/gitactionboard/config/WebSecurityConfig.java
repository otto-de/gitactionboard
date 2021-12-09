package de.otto.platform.gitactionboard.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
@Slf4j
@ConditionalOnProperty(name = "spring.security.oauth2.enable", havingValue = "true")
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
  private final String actuatorBasePath;

  public WebSecurityConfig(
      @Value("${management.endpoints.web.base-path:/actuator}") String actuatorBasePath) {
    this.actuatorBasePath = actuatorBasePath;
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
        .antMatchers(String.format("%s/health", actuatorBasePath), "/login/oauth2/**", "/oauth2/**")
        .permitAll()
        .and()
        .authorizeRequests()
        .anyRequest()
        .authenticated()
        .and()
        .oauth2Login();
  }
}
