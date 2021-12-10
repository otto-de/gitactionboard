package de.otto.platform.gitactionboard.config;

import static org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter.Directive.ALL;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;

@Configuration
@EnableWebSecurity
@Slf4j
@ConditionalOnMissingBean(NoOpsWebSecurityConfig.class)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
  private final String actuatorBasePath;
  private final GithubAuthenticationSuccessHandler authenticationSuccessHandler;

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  public WebSecurityConfig(
      @Value("${management.endpoints.web.base-path:/actuator}") String actuatorBasePath,
      GithubAuthenticationSuccessHandler authenticationSuccessHandler) {
    this.actuatorBasePath = actuatorBasePath;
    this.authenticationSuccessHandler = authenticationSuccessHandler;
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.cors()
        .disable()
        .csrf()
        .disable()
        .formLogin()
        .disable()
        .httpBasic()
        .disable()
        .authorizeRequests()
        .antMatchers(String.format("%s/health", actuatorBasePath), "/login/oauth2/**", "/oauth2/**")
        .permitAll()
        .and()
        .authorizeRequests()
        .anyRequest()
        .authenticated()
        .and()
        .oauth2Login()
        .successHandler(authenticationSuccessHandler)
        .and()
        .logout()
        .addLogoutHandler(new HeaderWriterLogoutHandler(new ClearSiteDataHeaderWriter(ALL)))
        .invalidateHttpSession(true);
  }
}
