package de.otto.platform.gitactionboard.domain.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@FunctionalInterface
public interface BranchesService {
  CompletableFuture<List<String>> getBranchNames(String repoName, String accessToken);
}
