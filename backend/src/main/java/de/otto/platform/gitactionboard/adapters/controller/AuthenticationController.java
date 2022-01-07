package de.otto.platform.gitactionboard.adapters.controller;

import de.otto.platform.gitactionboard.domain.AuthenticationMechanism;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthenticationController {
  private final List<AuthenticationMechanism> authenticationMechanisms;

  @GetMapping(path = "/available-auths")
  public List<AuthenticationMechanism> getAvailableAuthMechanism() {
    return List.copyOf(authenticationMechanisms);
  }
}
