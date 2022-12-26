package de.otto.platform.gitactionboard.adapters.controller;

import static de.otto.platform.gitactionboard.adapters.controller.Utils.createResponseEntityBodyBuilder;
import static de.otto.platform.gitactionboard.adapters.controller.Utils.decodeUrlEncodedText;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import de.otto.platform.gitactionboard.domain.service.CodeStandardViolationsScanService;
import de.otto.platform.gitactionboard.domain.service.SecretsScanService;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@Validated
@RestController
@RequestMapping("/v1/alerts")
public class GithubAlertsController {
  private final SecretsScanService secretsScanService;
  private final CodeStandardViolationsScanService codeStandardViolationsScanService;
  private final boolean enableSecretsScanMonitoring;
  private final boolean enableCodeScanMonitoring;

  @Autowired
  public GithubAlertsController(
      SecretsScanService secretsScanService,
      CodeStandardViolationsScanService codeStandardViolationsScanService,
      @Qualifier("enableSecretsScanMonitoring") Boolean secretsScanEnabled,
      @Qualifier("enableCodeScanMonitoring") Boolean codeScanEnabled) {
    this.secretsScanService = secretsScanService;
    this.codeStandardViolationsScanService = codeStandardViolationsScanService;
    this.enableSecretsScanMonitoring = secretsScanEnabled;
    this.enableCodeScanMonitoring = codeScanEnabled;
  }

  @Cacheable(
      cacheNames = "securityScanAlerts",
      sync = true,
      keyGenerator = "sharedCacheKeyGenerator")
  @GetMapping(value = "/secrets", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<SecretsScanAlert>> getSecretsScanAlerts(
      @RequestHeader(value = AUTHORIZATION, required = false) String accessToken) {

    if (!enableSecretsScanMonitoring) return ResponseEntity.notFound().build();

    final List<SecretsScanAlert> secretsScanAlerts =
        secretsScanService.fetchExposedSecrets(decodeUrlEncodedText(accessToken)).stream()
            .map(SecretsScanAlert::from)
            .toList();

    return createResponseEntityBodyBuilder().body(secretsScanAlerts);
  }

  @Cacheable(cacheNames = "codeScanAlerts", sync = true, keyGenerator = "sharedCacheKeyGenerator")
  @GetMapping(value = "/code-standard-violations", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<CodeStandardViolationAlert>> getCodeStandardViolationAlerts(
      @RequestHeader(value = AUTHORIZATION, required = false) String accessToken) {
    if (!enableCodeScanMonitoring) return ResponseEntity.notFound().build();

    final List<CodeStandardViolationAlert> codeStandardViolationAlerts =
        codeStandardViolationsScanService
            .fetchCodeViolations(decodeUrlEncodedText(accessToken))
            .stream()
            .map(CodeStandardViolationAlert::from)
            .toList();

    return createResponseEntityBodyBuilder().body(codeStandardViolationAlerts);
  }
}
