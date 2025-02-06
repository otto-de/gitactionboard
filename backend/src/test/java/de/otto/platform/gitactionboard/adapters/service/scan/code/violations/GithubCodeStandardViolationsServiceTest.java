package de.otto.platform.gitactionboard.adapters.service.scan.code.violations;

import static de.otto.platform.gitactionboard.fixtures.CodeStandardViolationFixture.getCodeStandardViolationDetailsBuilder;
import static de.otto.platform.gitactionboard.fixtures.CodeStandardViolationFixture.getCodeStandardViolationResponseBuilder;
import static de.otto.platform.gitactionboard.fixtures.WorkflowsFixture.REPO_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import de.otto.platform.gitactionboard.Sequential;
import de.otto.platform.gitactionboard.adapters.service.GithubApiService;
import de.otto.platform.gitactionboard.domain.scan.code.violations.CodeStandardViolationDetails;
import java.util.List;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@Sequential
class GithubCodeStandardViolationsServiceTest {
  @Mock private GithubApiService apiService;

  private static final String ACCESS_TOKEN = "accessToken";

  private static final int PER_PAGE = 100;

  private static final String URL_FORMAT =
      "/%s/code-scanning/alerts?state=open&page=%d&per_page=%d";

  private GithubCodeStandardViolationsService githubCodeStandardViolationsService;

  @BeforeEach
  void setUp() {
    githubCodeStandardViolationsService =
        new GithubCodeStandardViolationsService(apiService, PER_PAGE);
  }

  @Test
  @SneakyThrows
  void shouldFetchCodeStandardViolationAlertsForGivenRepo() {
    final String url2 = "https://github.com/johndoe/hello-world/code-scanning/alerts/2";
    when(apiService.getForObject(
            URL_FORMAT.formatted(REPO_NAME, 1, PER_PAGE),
            ACCESS_TOKEN,
            CodeStandardViolationResponse[].class))
        .thenReturn(
            new CodeStandardViolationResponse[] {
              getCodeStandardViolationResponseBuilder().build(),
              getCodeStandardViolationResponseBuilder().id(2).url(url2).build()
            });

    final List<CodeStandardViolationDetails> codeStandardViolationDetails =
        githubCodeStandardViolationsService.fetchCodeViolations(REPO_NAME, ACCESS_TOKEN).get();

    assertThat(codeStandardViolationDetails)
        .hasSize(2)
        .containsExactlyInAnyOrder(
            getCodeStandardViolationDetailsBuilder().build(),
            getCodeStandardViolationDetailsBuilder().id(2).url(url2).build());

    verify(apiService, times(1))
        .getForObject(
            URL_FORMAT.formatted(REPO_NAME, 1, PER_PAGE),
            ACCESS_TOKEN,
            CodeStandardViolationResponse[].class);
  }

  @Test
  @SneakyThrows
  void shouldFetchAllCodeStandardViolationAlertsForGivenRepoRecursively() {
    final String url2 = "https://github.com/johndoe/hello-world/code-scanning/alerts/2";
    final String url3 = "https://github.com/johndoe/hello-world/code-scanning/alerts/3";
    final int perPage = 2;
    final GithubCodeStandardViolationsService codeViolationsService =
        new GithubCodeStandardViolationsService(apiService, perPage);

    when(apiService.getForObject(
            URL_FORMAT.formatted(REPO_NAME, 1, perPage),
            ACCESS_TOKEN,
            CodeStandardViolationResponse[].class))
        .thenReturn(
            new CodeStandardViolationResponse[] {
              getCodeStandardViolationResponseBuilder().build(),
              getCodeStandardViolationResponseBuilder().id(2).url(url2).build()
            });

    when(apiService.getForObject(
            URL_FORMAT.formatted(REPO_NAME, 2, perPage),
            ACCESS_TOKEN,
            CodeStandardViolationResponse[].class))
        .thenReturn(
            new CodeStandardViolationResponse[] {
              getCodeStandardViolationResponseBuilder().id(3).url(url3).build()
            });

    final List<CodeStandardViolationDetails> codeStandardViolationDetails =
        codeViolationsService.fetchCodeViolations(REPO_NAME, ACCESS_TOKEN).get();

    assertThat(codeStandardViolationDetails)
        .hasSize(3)
        .containsExactlyInAnyOrder(
            getCodeStandardViolationDetailsBuilder().build(),
            getCodeStandardViolationDetailsBuilder().id(2).url(url2).build(),
            getCodeStandardViolationDetailsBuilder().id(3).url(url3).build());

    verify(apiService, times(1))
        .getForObject(
            URL_FORMAT.formatted(REPO_NAME, 1, perPage),
            ACCESS_TOKEN,
            CodeStandardViolationResponse[].class);
    verify(apiService, times(1))
        .getForObject(
            URL_FORMAT.formatted(REPO_NAME, 2, perPage),
            ACCESS_TOKEN,
            CodeStandardViolationResponse[].class);
  }

  @Test
  @SneakyThrows
  void shouldReturnEmptyListIfUnableToFetchCodeStandardViolationAlertsForGivenRepo() {
    doThrow(new RuntimeException("Boom"))
        .when(apiService)
        .getForObject(
            URL_FORMAT.formatted(REPO_NAME, 1, PER_PAGE),
            ACCESS_TOKEN,
            CodeStandardViolationResponse[].class);

    final List<CodeStandardViolationDetails> codeStandardViolationDetails =
        githubCodeStandardViolationsService.fetchCodeViolations(REPO_NAME, ACCESS_TOKEN).get();

    assertThat(codeStandardViolationDetails).isEmpty();

    verify(apiService, times(1))
        .getForObject(
            URL_FORMAT.formatted(REPO_NAME, 1, PER_PAGE),
            ACCESS_TOKEN,
            CodeStandardViolationResponse[].class);
  }
}
