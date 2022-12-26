package de.otto.platform.gitactionboard.adapters.service.scan;

import de.otto.platform.gitactionboard.adapters.service.ApiService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class GithubAlertsService<T, U> {
  private final ApiService apiService;
  private final Integer perPage;
  private final String alertsType;

  protected CompletableFuture<List<U>> fetchAlerts(String repoName, String accessToken) {
    final Function<T, U> mapper = getMapper(repoName);
    return CompletableFuture.supplyAsync(
            () ->
                fetchAllCodeAlerts(repoName, accessToken, List.of(), perPage, 1).stream()
                    .map(mapper)
                    .toList())
        .exceptionally(
            throwable -> {
              log.error(throwable.getMessage(), throwable);
              return Collections.emptyList();
            });
  }

  protected abstract Function<T, U> getMapper(String repoName);

  private List<T> fetchAllCodeAlerts(
      final String repoName,
      final String accessToken,
      final List<T> responseAccumulator,
      int perPage,
      int pageNumber) {
    log.info(
        "Fetching {} scan alerts details for {} repo with page number {}",
        repoName,
        alertsType,
        pageNumber);

    final T[] runDetailsResponse =
        apiService.getForObject(
            String.format(
                "/%s/%s-scanning/alerts?state=open&page=%d&per_page=%d",
                repoName, alertsType, pageNumber, perPage),
            accessToken,
            getResponseType());

    final List<T> currentResponse =
        Arrays.stream(Optional.ofNullable(runDetailsResponse).orElse(getDefaultResponse()))
            .toList();

    final List<T> allResponses =
        Stream.concat(responseAccumulator.stream(), currentResponse.stream()).toList();

    if (currentResponse.size() == perPage) {
      return fetchAllCodeAlerts(repoName, accessToken, allResponses, perPage, pageNumber + 1);
    }

    return allResponses;
  }

  protected abstract T[] getDefaultResponse();

  protected abstract Class<T[]> getResponseType();
}
