package de.otto.platform.gitactionboard.adapters.service;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import de.otto.platform.gitactionboard.Sequential;
import de.otto.platform.gitactionboard.domain.service.CodeStandardViolationsScanService;
import de.otto.platform.gitactionboard.domain.service.PipelineService;
import de.otto.platform.gitactionboard.domain.service.SecretsScanService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Sequential
class PeriodicScanSchedulerTest {
  @Mock private PipelineService pipelineService;
  @Mock private SecretsScanService secretsScanService;
  @Mock private CodeStandardViolationsScanService codeStandardViolationsScanService;

  @Test
  void shouldPeriodicScanForAll() {
    final PeriodicScanScheduler periodicScanScheduler =
        new PeriodicScanScheduler(
            pipelineService, secretsScanService, codeStandardViolationsScanService, true, true);
    periodicScanScheduler.scan();
    verify(pipelineService, times(1)).fetchJobs(null);
    verify(secretsScanService, times(1)).fetchExposedSecrets(null);
    verify(codeStandardViolationsScanService, times(1)).fetchCodeViolations(null);
  }

  @Test
  void shouldNotScanExposedSecrets() {
    final PeriodicScanScheduler periodicScanScheduler =
        new PeriodicScanScheduler(
            pipelineService, secretsScanService, codeStandardViolationsScanService, false, true);
    periodicScanScheduler.scan();
    verify(pipelineService, times(1)).fetchJobs(null);
    verify(secretsScanService, never()).fetchExposedSecrets(any());
    verify(codeStandardViolationsScanService, times(1)).fetchCodeViolations(null);
  }

  @Test
  void shouldNotScanStandardCodeViolations() {
    final PeriodicScanScheduler periodicScanScheduler =
        new PeriodicScanScheduler(
            pipelineService, secretsScanService, codeStandardViolationsScanService, true, false);
    periodicScanScheduler.scan();
    verify(pipelineService, times(1)).fetchJobs(null);
    verify(secretsScanService, times(1)).fetchExposedSecrets(null);
    verify(codeStandardViolationsScanService, never()).fetchCodeViolations(any());
  }

  @Test
  void shouldScanOnlyWorkflows() {
    final PeriodicScanScheduler periodicScanScheduler =
        new PeriodicScanScheduler(
            pipelineService, secretsScanService, codeStandardViolationsScanService, false, false);
    periodicScanScheduler.scan();
    verify(pipelineService, times(1)).fetchJobs(null);
    verify(secretsScanService, never()).fetchExposedSecrets(any());
    verify(codeStandardViolationsScanService, never()).fetchCodeViolations(any());
  }
}
