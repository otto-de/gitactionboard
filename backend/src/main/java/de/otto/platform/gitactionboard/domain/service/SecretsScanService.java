package de.otto.platform.gitactionboard.domain.service;

import de.otto.platform.gitactionboard.domain.scan.secrets.SecretsScanDetails;
import de.otto.platform.gitactionboard.domain.service.notifications.NotificationsService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SecretsScanService {
  private final SecretsScanDetailsService secretsScanDetailsService;
  private final NotificationsService notificationsService;

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  private final List<String> repoNames;

  @Autowired
  public SecretsScanService(
      SecretsScanDetailsService secretsScanDetailsService,
      NotificationsService notificationsService,
      @Qualifier("repoNames") List<String> repoNames) {
    this.secretsScanDetailsService = secretsScanDetailsService;
    this.notificationsService = notificationsService;
    this.repoNames = repoNames;
  }

  @Cacheable(cacheNames = "exposedSecrets", sync = true, keyGenerator = "sharedCacheKeyGenerator")
  public List<SecretsScanDetails> fetchExposedSecrets(String accessToken) {
    final List<SecretsScanDetails> secretsScanDetails =
        repoNames.stream()
            .parallel()
            .peek(repoName -> log.info("Fetching security scan alerts for {} repo", repoName))
            .map(
                repoName ->
                    secretsScanDetailsService.fetchSecretsScanDetails(repoName, accessToken))
            .map(CompletableFuture::join)
            .flatMap(Collection::stream)
            .toList();

    notificationsService.sendNotificationsForSecretScanAlerts(secretsScanDetails);

    return secretsScanDetails;
  }
}
