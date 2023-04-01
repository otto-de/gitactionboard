package de.otto.platform.gitactionboard.adapters.service;

import de.otto.platform.gitactionboard.domain.service.CodeStandardViolationsScanService;
import de.otto.platform.gitactionboard.domain.service.PipelineService;
import de.otto.platform.gitactionboard.domain.service.SecretsScanService;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty("PERIODIC_SCAN_CRON_SCHEDULE")
@EnableScheduling
public class PeriodicScanScheduler {

  private final PipelineService pipelineService;
  private final SecretsScanService secretsScanService;
  private final CodeStandardViolationsScanService codeStandardViolationsScanService;
  private final Boolean secretsScanEnabled;
  private final Boolean codeScanEnabled;

  public PeriodicScanScheduler(
      PipelineService pipelineService,
      SecretsScanService secretsScanService,
      CodeStandardViolationsScanService codeStandardViolationsScanService,
      @Qualifier("enableSecretsScanMonitoring") Boolean secretsScanEnabled,
      @Qualifier("enableCodeScanMonitoring") Boolean codeScanEnabled) {
    this.pipelineService = pipelineService;
    this.secretsScanService = secretsScanService;
    this.codeStandardViolationsScanService = codeStandardViolationsScanService;
    this.secretsScanEnabled = secretsScanEnabled;
    this.codeScanEnabled = codeScanEnabled;
  }

  @Scheduled(cron = "${PERIODIC_SCAN_CRON_SCHEDULE}")
  public void scan() {
    pipelineService.fetchJobs(null);
    if (secretsScanEnabled) {
      secretsScanService.fetchExposedSecrets(null);
    }
    if (codeScanEnabled) {
      codeStandardViolationsScanService.fetchCodeViolations(null);
    }
  }
}
