package de.otto.platform.gitactionboard.adapters.controller;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import de.otto.platform.gitactionboard.domain.AuthenticationMechanism;
import de.otto.platform.gitactionboard.domain.service.ConfigurationService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.List;
import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/config")
@SuppressFBWarnings({"EI_EXPOSE_REP", "EI_EXPOSE_REP2"})
public class ConfigurationController {
  private final ConfigurationService configurationService;
  private final List<AuthenticationMechanism> authenticationMechanisms;
  private final Boolean secretsScanEnabled;
  private final Boolean codeScanEnabled;
  private final String projectVersion;
  private final List<String> repoNames;

  @Autowired
  public ConfigurationController(
      ConfigurationService configurationService,
      List<AuthenticationMechanism> authenticationMechanisms,
      @Qualifier("enableSecretsScanMonitoring") Boolean secretsScanEnabled,
      @Qualifier("enableCodeScanMonitoring") Boolean codeScanEnabled,
      @Qualifier("projectVersion") String projectVersion,
      @Qualifier("repoNames") List<String> repoNames) {
    this.configurationService = configurationService;
    this.authenticationMechanisms = authenticationMechanisms;
    this.secretsScanEnabled = secretsScanEnabled;
    this.codeScanEnabled = codeScanEnabled;
    this.projectVersion = projectVersion;
    this.repoNames = repoNames;
  }

  @GetMapping
  public Config getAvailableConfig() {
    return Config.builder()
        .availableAuths(List.copyOf(authenticationMechanisms))
        .githubSecretsScanMonitoringEnabled(secretsScanEnabled)
        .githubCodeScanMonitoringEnabled(codeScanEnabled)
        .version(projectVersion)
        .build();
  }

  @GetMapping("/repository-names")
  public List<String> getRepositoryNames() {
    return repoNames;
  }

  @GetMapping("/branch-names")
  public List<String> getBranchNames(
      @RequestHeader(value = AUTHORIZATION, required = false) String accessToken) {
    return configurationService.getBranchNames(accessToken);
  }

  @Value
  @Builder
  public static class Config {
    List<AuthenticationMechanism> availableAuths;
    Boolean githubSecretsScanMonitoringEnabled;
    Boolean githubCodeScanMonitoringEnabled;
    String version;
  }
}
