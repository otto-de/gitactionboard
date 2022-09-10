package de.otto.platform.gitactionboard.domain.service;

import de.otto.platform.gitactionboard.domain.scan.code.violations.CodeStandardViolationDetails;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface CodeStandardViolationsService {
  CompletableFuture<List<CodeStandardViolationDetails>> fetchCodeViolations(
      String repoName, String accessToken);
}
