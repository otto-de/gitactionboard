package de.otto.platform.gitactionboard.adapters.controller;

import static de.otto.platform.gitactionboard.adapters.controller.Utils.createResponseEntityBodyBuilder;
import static de.otto.platform.gitactionboard.adapters.controller.Utils.decodeUrlEncodedText;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import de.otto.platform.gitactionboard.domain.scan.SecurityScanAlert;
import de.otto.platform.gitactionboard.domain.service.SecretsScanService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
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
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/alerts")
@ConditionalOnProperty(
    value = "ENABLE_GITHUB_SECRETS_SCAN_ALERTS_MONITORING",
    havingValue = "true",
    matchIfMissing = false)
public class GithubAlertsController {
  private final SecretsScanService secretsScanService;

  @Cacheable(cacheNames = "securityScanAlerts", sync = true)
  @GetMapping(value = "/secrets", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<SecurityScanAlert>> getSecurityScanAlerts(
      @RequestHeader(value = AUTHORIZATION, required = false) String accessToken) {
    return createResponseEntityBodyBuilder()
        .body(secretsScanService.fetchSecurityScanAlerts(decodeUrlEncodedText(accessToken)));
  }
}
