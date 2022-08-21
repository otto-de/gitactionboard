package de.otto.platform.gitactionboard.adapters.service.scan.secrets;

import de.otto.platform.gitactionboard.adapters.service.ApiService;
import de.otto.platform.gitactionboard.domain.scan.secrets.SecretsScanDetails;
import de.otto.platform.gitactionboard.domain.service.SecretsScanDetailsService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GithubSecretsScanDetailsService implements SecretsScanDetailsService {
  private final ApiService apiService;
  private final Integer perPage;

  @Autowired
  public GithubSecretsScanDetailsService(
      ApiService apiService, @Value("${app.github.alerts.page.size:100}") Integer perPage) {
    this.apiService = apiService;
    this.perPage = perPage;
  }

  @Override
  @Async
  public CompletableFuture<List<SecretsScanDetails>> fetchSecretsScanDetails(
      String repoName, String accessToken) {
    return CompletableFuture.supplyAsync(
            () ->
                fetchAllSecretAlerts(repoName, accessToken, List.of(), perPage, 1).stream()
                    .map(response -> convertToSecretsScanDetails(repoName, response))
                    .collect(Collectors.toList()))
        .exceptionally(
            throwable -> {
              log.error(throwable.getMessage(), throwable);
              return Collections.emptyList();
            });
  }

  private static SecretsScanDetails convertToSecretsScanDetails(
      String repoName, SecretsScanDetailsResponse secretsScanDetailsResponse) {
    return SecretsScanDetails.builder()
        .id(secretsScanDetailsResponse.getId())
        .url(secretsScanDetailsResponse.getUrl())
        .createdAt(secretsScanDetailsResponse.getCreatedAt())
        .name(secretsScanDetailsResponse.getDisplayName())
        .repoName(repoName)
        .build();
  }

  private List<SecretsScanDetailsResponse> fetchAllSecretAlerts(
      final String repoName,
      final String accessToken,
      final List<SecretsScanDetailsResponse> responseAccumulator,
      int perPage,
      int pageNumber) {
    log.info("Fetching security scan details for {} repo with page number %d", repoName);

    final SecretsScanDetailsResponse[] runDetailsResponse =
        apiService.getForObject(
            String.format(
                "/%s/secret-scanning/alerts?state=open&page=%d&per_page=%d",
                repoName, pageNumber, perPage),
            accessToken,
            SecretsScanDetailsResponse[].class);

    final List<SecretsScanDetailsResponse> currentResponse =
        Optional.ofNullable(runDetailsResponse)
            .map(
                secretsScanDetailsResponses ->
                    Arrays.stream(secretsScanDetailsResponses).collect(Collectors.toList()))
            .orElse(Collections.emptyList());

    final List<SecretsScanDetailsResponse> allResponses =
        Stream.concat(responseAccumulator.stream(), currentResponse.stream())
            .collect(Collectors.toList());

    if (currentResponse.size() == perPage) {
      return fetchAllSecretAlerts(repoName, accessToken, allResponses, perPage, pageNumber + 1);
    }

    return allResponses;
  }
}
