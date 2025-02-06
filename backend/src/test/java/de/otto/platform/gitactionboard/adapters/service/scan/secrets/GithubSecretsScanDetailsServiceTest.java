package de.otto.platform.gitactionboard.adapters.service.scan.secrets;

import static de.otto.platform.gitactionboard.fixtures.SecretsScanDetailsFixtures.getSecretsScanDetailsBuilder;
import static de.otto.platform.gitactionboard.fixtures.SecretsScanDetailsFixtures.getSecretsScanDetailsResponseBuilder;
import static de.otto.platform.gitactionboard.fixtures.WorkflowsFixture.REPO_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.otto.platform.gitactionboard.Sequential;
import de.otto.platform.gitactionboard.adapters.service.GithubApiService;
import de.otto.platform.gitactionboard.domain.scan.secrets.SecretsScanDetails;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Sequential
class GithubSecretsScanDetailsServiceTest {
  @Mock private GithubApiService apiService;

  private static final String ACCESS_TOKEN = "accessToken";

  private static final int PER_PAGE = 100;

  private static final String URL_FORMAT =
      "/%s/secret-scanning/alerts?state=open&page=%d&per_page=%d";

  private GithubSecretsScanDetailsService secretsScanDetailsService;

  @BeforeEach
  void setUp() {
    secretsScanDetailsService = new GithubSecretsScanDetailsService(apiService, PER_PAGE);
  }

  @Test
  @SneakyThrows
  void shouldFetchSecretScanAlertsForGivenRepo() {
    final String url2 = "https://github.com/johndoe/hello-world/security/secret-scanning/2";
    when(apiService.getForObject(
            URL_FORMAT.formatted(REPO_NAME, 1, PER_PAGE),
            ACCESS_TOKEN,
            SecretsScanDetailsResponse[].class))
        .thenReturn(
            new SecretsScanDetailsResponse[] {
              getSecretsScanDetailsResponseBuilder().build(),
              getSecretsScanDetailsResponseBuilder().id(2).url(url2).build()
            });

    final List<SecretsScanDetails> secretsScanDetails =
        secretsScanDetailsService.fetchSecretsScanDetails(REPO_NAME, ACCESS_TOKEN).get();

    assertThat(secretsScanDetails)
        .hasSize(2)
        .containsExactlyInAnyOrder(
            getSecretsScanDetailsBuilder().build(),
            getSecretsScanDetailsBuilder().id(2).url(url2).build());

    verify(apiService, times(1))
        .getForObject(
            URL_FORMAT.formatted(REPO_NAME, 1, PER_PAGE),
            ACCESS_TOKEN,
            SecretsScanDetailsResponse[].class);
  }

  @Test
  @SneakyThrows
  void shouldFetchAllSecretScanAlertsForGivenRepoRecursively() {
    final String url2 = "https://github.com/johndoe/hello-world/security/secret-scanning/2";
    final String url3 = "https://github.com/johndoe/hello-world/security/secret-scanning/3";
    final int perPage = 2;
    final GithubSecretsScanDetailsService secretsScanDetailsService =
        new GithubSecretsScanDetailsService(apiService, perPage);

    when(apiService.getForObject(
            URL_FORMAT.formatted(REPO_NAME, 1, perPage),
            ACCESS_TOKEN,
            SecretsScanDetailsResponse[].class))
        .thenReturn(
            new SecretsScanDetailsResponse[] {
              getSecretsScanDetailsResponseBuilder().build(),
              getSecretsScanDetailsResponseBuilder().id(2).url(url2).build()
            });

    when(apiService.getForObject(
            URL_FORMAT.formatted(REPO_NAME, 2, perPage),
            ACCESS_TOKEN,
            SecretsScanDetailsResponse[].class))
        .thenReturn(
            new SecretsScanDetailsResponse[] {
              getSecretsScanDetailsResponseBuilder().id(3).url(url3).build()
            });

    final List<SecretsScanDetails> secretsScanDetails =
        secretsScanDetailsService.fetchSecretsScanDetails(REPO_NAME, ACCESS_TOKEN).get();

    assertThat(secretsScanDetails)
        .hasSize(3)
        .containsExactlyInAnyOrder(
            getSecretsScanDetailsBuilder().build(),
            getSecretsScanDetailsBuilder().id(2).url(url2).build(),
            getSecretsScanDetailsBuilder().id(3).url(url3).build());

    verify(apiService, times(1))
        .getForObject(
            URL_FORMAT.formatted(REPO_NAME, 1, perPage),
            ACCESS_TOKEN,
            SecretsScanDetailsResponse[].class);
    verify(apiService, times(1))
        .getForObject(
            URL_FORMAT.formatted(REPO_NAME, 2, perPage),
            ACCESS_TOKEN,
            SecretsScanDetailsResponse[].class);
  }

  @Test
  @SneakyThrows
  void shouldReturnEmptyListIfUnableToFetchSecretScanAlertsForGivenRepo() {
    doThrow(new RuntimeException("Boom"))
        .when(apiService)
        .getForObject(
            URL_FORMAT.formatted(REPO_NAME, 1, PER_PAGE),
            ACCESS_TOKEN,
            SecretsScanDetailsResponse[].class);

    final List<SecretsScanDetails> secretsScanDetails =
        secretsScanDetailsService.fetchSecretsScanDetails(REPO_NAME, ACCESS_TOKEN).get();

    assertThat(secretsScanDetails).isEmpty();

    verify(apiService, times(1))
        .getForObject(
            URL_FORMAT.formatted(REPO_NAME, 1, PER_PAGE),
            ACCESS_TOKEN,
            SecretsScanDetailsResponse[].class);
  }
}
