package de.otto.platform.gitactionboard.domain.service;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@SuppressFBWarnings("EI_EXPOSE_REP2")
public class ConfigurationService {
  private final BranchesService branchesService;
  private final List<String> repoNames;

  public ConfigurationService(
      BranchesService branchesService, @Qualifier("repoNames") List<String> repoNames) {
    this.branchesService = branchesService;
    this.repoNames = repoNames;
  }

  @Cacheable(cacheNames = "miscellaneous", sync = true, keyGenerator = "sharedCacheKeyGenerator")
  public List<String> getBranchNames(String accessToken) {
    return repoNames.stream()
        .parallel()
        .map(repoName -> getBranchNames(repoName, accessToken))
        .map(CompletableFuture::join)
        .flatMap(Collection::stream)
        .distinct()
        .sorted()
        .toList();
  }

  private CompletableFuture<List<String>> getBranchNames(String repoName, String accessToken) {
    return branchesService
        .getBranchNames(repoName, accessToken)
        .exceptionally(
            throwable -> {
              log.error(
                  "Error fetching branch names for repository {}: {}",
                  repoName,
                  throwable.getMessage(),
                  throwable);
              return List.of();
            });
  }
}
