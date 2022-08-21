package de.otto.platform.gitactionboard.domain.service;

import de.otto.platform.gitactionboard.domain.scan.secrets.SecretsScanDetails;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface SecretsScanDetailsService {
  CompletableFuture<List<SecretsScanDetails>> fetchSecretsScanDetails(
      String repoName, String accessToken);
}
