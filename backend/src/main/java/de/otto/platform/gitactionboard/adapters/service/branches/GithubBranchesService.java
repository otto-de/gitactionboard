package de.otto.platform.gitactionboard.adapters.service.branches;

import de.otto.platform.gitactionboard.adapters.service.GithubApiService;
import de.otto.platform.gitactionboard.domain.service.BranchesService;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class GithubBranchesService implements BranchesService {
  private final GithubApiService apiService;

  @Override
  public CompletableFuture<List<String>> getBranchNames(String repoName, String accessToken) {
    return CompletableFuture.supplyAsync(
        () -> {
          log.info("Fetching branch names for {} repository", repoName);
          return Arrays.stream(
                  apiService.getForObject(
                      "/%s/branches?per_page=100".formatted(repoName),
                      accessToken,
                      BranchesResponse[].class))
              .map(BranchesResponse::getName)
              .toList();
        });
  }
}
