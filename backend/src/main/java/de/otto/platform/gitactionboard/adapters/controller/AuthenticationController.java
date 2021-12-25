package de.otto.platform.gitactionboard.adapters.controller;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class AuthenticationController {
  private final boolean isBasicAuthEnabled;
  private final boolean isOauth2Enabled;

  public AuthenticationController(
      @Value("${BASIC_AUTH_USER_DETAILS_FILE_PATH:}") String basicAuthDetailsFilePath,
      @Value("${spring.security.oauth2.client.registration.github.client-id:-}")
          String githubClientId) {
    this.isBasicAuthEnabled = StringUtils.hasText(basicAuthDetailsFilePath);
    this.isOauth2Enabled = !"-".equals(githubClientId);
  }

  @GetMapping(path = "/available-auths")
  public List<AuthenticationMechanism> getAvailableAuthMechanism() {
    final ArrayList<AuthenticationMechanism> authenticationMechanisms = new ArrayList<>();

    if (isBasicAuthEnabled) authenticationMechanisms.add(AuthenticationMechanism.BASIC_AUTH);
    if (isOauth2Enabled) authenticationMechanisms.add(AuthenticationMechanism.OAUTH2);

    return authenticationMechanisms;
  }

  public enum AuthenticationMechanism {
    OAUTH2,
    BASIC_AUTH
  }
}
