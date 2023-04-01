package de.otto.platform.gitactionboard.domain.service;

import de.otto.platform.gitactionboard.domain.scan.code.violations.CodeStandardViolationDetails;
import de.otto.platform.gitactionboard.domain.service.notifications.CodeStandardViolationNotificationService;
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
public class CodeStandardViolationsScanService {
  private final CodeStandardViolationsService codeStandardViolationsService;
  private final CodeStandardViolationNotificationService notificationsService;

  @SuppressFBWarnings("EI_EXPOSE_REP2")
  private final List<String> repoNames;

  @Autowired
  public CodeStandardViolationsScanService(
      CodeStandardViolationsService codeStandardViolationsService,
      CodeStandardViolationNotificationService notificationsService,
      @Qualifier("repoNames") List<String> repoNames) {
    this.codeStandardViolationsService = codeStandardViolationsService;
    this.notificationsService = notificationsService;
    this.repoNames = repoNames;
  }

  @Cacheable(
      cacheNames = "codeStandardViolations",
      sync = true,
      keyGenerator = "sharedCacheKeyGenerator")
  public List<CodeStandardViolationDetails> fetchCodeViolations(String accessToken) {
    final List<CodeStandardViolationDetails> codeViolations =
        repoNames.stream()
            .parallel()
            .peek(repoName -> log.info("Fetching code violations for {} repo", repoName))
            .map(
                repoName ->
                    codeStandardViolationsService.fetchCodeViolations(repoName, accessToken))
            .map(CompletableFuture::join)
            .flatMap(Collection::stream)
            .toList();

    notificationsService.sendNotifications(codeViolations);

    return codeViolations;
  }
}
