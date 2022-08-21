package de.otto.platform.gitactionboard.domain.service;

import de.otto.platform.gitactionboard.domain.scan.SecurityScanAlert;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SecretsScanService {
  private final SecretsScanDetailsService secretsScanDetailsService;

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  private final List<String> repoNames;

  public SecretsScanService(
      SecretsScanDetailsService secretsScanDetailsService,
      @Qualifier("repoNames") List<String> repoNames) {
    this.secretsScanDetailsService = secretsScanDetailsService;
    this.repoNames = repoNames;
  }

  public List<SecurityScanAlert> fetchSecurityScanAlerts(String accessToken) {
    return repoNames.stream()
        .parallel()
        .peek(repoName -> log.info("Fetching security scan alerts for {} repo", repoName))
        .map(repoName -> secretsScanDetailsService.fetchSecretsScanDetails(repoName, accessToken))
        .map(CompletableFuture::join)
        .flatMap(Collection::stream)
        .map(SecurityScanAlert::from)
        .toList();
  }
}
