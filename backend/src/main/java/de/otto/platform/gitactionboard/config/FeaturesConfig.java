package de.otto.platform.gitactionboard.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeaturesConfig {
  @Bean(name = "enableSecretsScanMonitoring")
  public Boolean enableSecretsScanMonitoring(
      @Value("${ENABLE_GITHUB_SECRETS_SCAN_ALERTS_MONITORING:false}")
          String githubSecretsScanMonitoringEnabled) {
    return Boolean.valueOf(githubSecretsScanMonitoringEnabled);
  }

  @Bean(name = "enableCodeScanMonitoring")
  public Boolean enableCodeScanMonitoring(
      @Value("${ENABLE_GITHUB_CODE_SCAN_ALERTS_MONITORING:false}")
          String githubCodeScanMonitoringEnabled) {
    return Boolean.valueOf(githubCodeScanMonitoringEnabled);
  }
}
