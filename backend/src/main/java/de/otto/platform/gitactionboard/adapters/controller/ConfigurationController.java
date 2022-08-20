package de.otto.platform.gitactionboard.adapters.controller;

import de.otto.platform.gitactionboard.domain.AuthenticationMechanism;
import java.util.List;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ConfigurationController {
  private final List<AuthenticationMechanism> authenticationMechanisms;

  @GetMapping(path = "/config")
  public Config getAvailableAuthMechanism() {
    return Config.builder().availableAuths(List.copyOf(authenticationMechanisms)).build();
  }

  @Value
  @Builder
  public static class Config {
    List<AuthenticationMechanism> availableAuths;
  }
}
