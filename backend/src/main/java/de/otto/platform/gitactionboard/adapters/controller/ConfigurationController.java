package de.otto.platform.gitactionboard.adapters.controller;

import de.otto.platform.gitactionboard.domain.AuthenticationMechanism;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class ConfigurationController {
  private final List<AuthenticationMechanism> authenticationMechanisms;
  private final Boolean secretsScanEnabled;
  private final Boolean codeScanEnabled;

  @Autowired
  public ConfigurationController(
      List<AuthenticationMechanism> authenticationMechanisms,
      @Qualifier("enableSecretsScanMonitoring") Boolean secretsScanEnabled,
      @Qualifier("enableCodeScanMonitoring") Boolean codeScanEnabled) {
    this.authenticationMechanisms = authenticationMechanisms;
    this.secretsScanEnabled = secretsScanEnabled;
    this.codeScanEnabled = codeScanEnabled;
  }

  @GetMapping(path = "/config")
  public Config getAvailableConfig() {
    return Config.builder()
        .availableAuths(List.copyOf(authenticationMechanisms))
        .githubSecretsScanMonitoringEnabled(secretsScanEnabled)
        .githubCodeScanMonitoringEnabled(codeScanEnabled)
        .build();
  }

  @lombok.Value
  @Builder
  public static class Config {
    List<AuthenticationMechanism> availableAuths;
    Boolean githubSecretsScanMonitoringEnabled;
    Boolean githubCodeScanMonitoringEnabled;
  }
}
