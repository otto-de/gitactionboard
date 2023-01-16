package de.otto.platform.gitactionboard.config.security;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.ACCESS_TOKEN;
import static org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter.Directive.ALL;

import de.otto.platform.gitactionboard.domain.AuthenticationMechanism;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;
import org.springframework.util.StringUtils;

@EnableWebSecurity
@Configuration
public class WebSecurityConfig {

  public static final String LOGIN_PATH = "/#/login";
  public static final String DASHBOARD_PATH = "/#/workflow-jobs";

  @Bean
  public List<AuthenticationMechanism> availableAuths(
      @Value("${BASIC_AUTH_USER_DETAILS_FILE_PATH:}") String basicAuthDetailsFilePath,
      @Value("${spring.security.oauth2.client.registration.github.client-id:-}")
          String githubClientId) {
    final ArrayList<AuthenticationMechanism> authenticationMechanisms = new ArrayList<>();

    if (StringUtils.hasText(basicAuthDetailsFilePath))
      authenticationMechanisms.add(AuthenticationMechanism.BASIC_AUTH);
    if (!"-".equals(githubClientId)) authenticationMechanisms.add(AuthenticationMechanism.OAUTH2);

    return authenticationMechanisms;
  }

  @Bean(name = "servletContextPath")
  public String servletContextPath(ServletContext context) {
    final String contextPath = context.getContextPath();
    return StringUtils.hasText(contextPath) ? contextPath : "/";
  }

  @Bean
  @Order(1)
  @ConditionalOnMissingBean(NoOpsWebSecurityConfig.class)
  public SecurityFilterChain permitAllFilterChain(
      @Value("${management.endpoints.web.base-path:/actuator}") String actuatorBasePath,
      HttpSecurity http)
      throws Exception {
    final String healthEndPoint = String.format("%s/health", actuatorBasePath);

    final String[] whitelistUrls = {
      healthEndPoint, "/config", "/", "/index.html", "/assets/**", "/favicon.ico", "/login/basic"
    };

    http.cors()
        .disable()
        .csrf()
        .disable()
        .formLogin()
        .disable()
        .securityMatcher(whitelistUrls)
        .authorizeHttpRequests()
        .requestMatchers(whitelistUrls)
        .permitAll();

    return http.build();
  }

  @Slf4j
  @Order(2)
  @Configuration
  @ConditionalOnExpression(
      "T(org.springframework.util.StringUtils).hasText('${BASIC_AUTH_USER_DETAILS_FILE_PATH:}')")
  @ConditionalOnMissingBean(NoOpsWebSecurityConfig.class)
  public static class BasicAuthSecurityConfig {
    private static final String CREDENTIAL_SEPARATOR = ":";

    @Bean
    public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
    }

    @Bean(name = "basicAuthUsers")
    public List<UserDetails> getBasicAuthUsers(
        @Value("${BASIC_AUTH_USER_DETAILS_FILE_PATH}") String basicAuthFilePath)
        throws IOException {
      return Files.readAllLines(Path.of(basicAuthFilePath)).stream()
          .filter(line -> !line.isBlank())
          .map(
              line -> {
                final String[] credentials = line.split(CREDENTIAL_SEPARATOR);
                return new AbstractMap.SimpleImmutableEntry<>(credentials[0], credentials[1]);
              })
          .map(
              authDetails ->
                  User.withUsername(authDetails.getKey())
                      .password(authDetails.getValue())
                      .authorities("ROLE_USER")
                      .build())
          .toList();
    }

    @Bean("basicAuthenticationManager")
    public AuthenticationManager authenticationManager(
        AuthenticationConfiguration authenticationConfiguration) throws Exception {
      return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public InMemoryUserDetailsManager basicAuthUserDetailsManager(
        @Qualifier("basicAuthUsers") List<UserDetails> basicAuthUsers) {
      return new InMemoryUserDetailsManager(basicAuthUsers);
    }

    @PostConstruct
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private void logInfo() {
      log.info(
          "Enabled Basic authentication as value is present for BASIC_AUTH_USER_DETAILS_FILE_PATH");
    }

    @Bean
    public SecurityFilterChain basicAuthSecurityFilterChain(
        @Value("${GITHUB_OAUTH2_CLIENT_ID:}") String githubAuthClientId, HttpSecurity http)
        throws Exception {
      final boolean githubAuthDisabled = githubAuthClientId.isBlank();

      http.cors()
          .disable()
          .formLogin()
          .usernameParameter("username")
          .passwordParameter("password")
          .loginPage(LOGIN_PATH)
          .loginProcessingUrl("/login/basic")
          .failureForwardUrl(LOGIN_PATH)
          .defaultSuccessUrl(DASHBOARD_PATH, true)
          .failureUrl(LOGIN_PATH)
          .and()
          .securityMatcher(
              request -> githubAuthDisabled || getAuthToken(request).startsWith("Basic"))
          .authorizeHttpRequests()
          .anyRequest()
          .authenticated()
          .and()
          .httpBasic()
          .and()
          .logout()
          .addLogoutHandler(new HeaderWriterLogoutHandler(new ClearSiteDataHeaderWriter(ALL)))
          .invalidateHttpSession(true)
          .deleteCookies(ACCESS_TOKEN);

      return http.build();
    }

    private String getAuthToken(HttpServletRequest request) {
      return Optional.ofNullable(request.getHeader(AUTHORIZATION))
          .orElseGet(
              () ->
                  Arrays.stream(request.getCookies())
                      .filter(cookie -> ACCESS_TOKEN.equals(cookie.getName()))
                      .findFirst()
                      .map(Cookie::getValue)
                      .orElse(""));
    }
  }

  @Slf4j
  @Order(3)
  @Configuration
  @ConditionalOnExpression(
      "T(org.springframework.util.StringUtils).hasText('${GITHUB_OAUTH2_CLIENT_ID:}')")
  @ConditionalOnMissingBean(NoOpsWebSecurityConfig.class)
  public static class GithubOauthSecurityConfig {

    @PostConstruct
    @SuppressWarnings("PMD.UnusedPrivateMethod")
    private void logInfo() {
      log.info("Enabled Github authentication as value is present for GITHUB_OAUTH2_CLIENT_ID");
    }

    @Bean
    public SecurityFilterChain githubOauthSecurityFilterChain(
        GithubAuthenticationSuccessHandler authenticationSuccessHandler, HttpSecurity http)
        throws Exception {
      http.cors()
          .disable()
          .formLogin()
          .disable()
          .httpBasic()
          .disable()
          .authorizeHttpRequests()
          .requestMatchers("/login/oauth2/**", "/oauth2/**")
          .permitAll()
          .and()
          .authorizeHttpRequests()
          .anyRequest()
          .authenticated()
          .and()
          .oauth2Login()
          .loginPage(LOGIN_PATH)
          .failureUrl(LOGIN_PATH)
          .successHandler(authenticationSuccessHandler)
          .and()
          .logout()
          .addLogoutHandler(new HeaderWriterLogoutHandler(new ClearSiteDataHeaderWriter(ALL)))
          .invalidateHttpSession(true)
          .deleteCookies(ACCESS_TOKEN);

      return http.build();
    }
  }
}
