package de.otto.platform.gitactionboard.config.security;

import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

public class DisableAuthentication extends AllNestedConditions {
  public DisableAuthentication() {
    super(ConfigurationPhase.PARSE_CONFIGURATION);
  }

  @ConditionalOnProperty(
      name = "spring.security.oauth2.client.registration.github.client-id",
      havingValue = "-",
      matchIfMissing = true)
  static class GithubAuthConfigMissing {}

  @ConditionalOnExpression(
      "!T(org.springframework.util.StringUtils).hasText('${BASIC_AUTH_USER_DETAILS_FILE_PATH:}')")
  static class BasicAuthConfigMissing {}
}
