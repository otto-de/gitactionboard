package de.otto.platform.gitactionboard.adapters.service.scan.code.violations;

import de.otto.platform.gitactionboard.adapters.service.GithubApiService;
import de.otto.platform.gitactionboard.adapters.service.scan.GithubAlertsService;
import de.otto.platform.gitactionboard.domain.scan.code.violations.CodeStandardViolationDetails;
import de.otto.platform.gitactionboard.domain.service.CodeStandardViolationsService;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GithubCodeStandardViolationsService
    extends GithubAlertsService<CodeStandardViolationResponse, CodeStandardViolationDetails>
    implements CodeStandardViolationsService {

  private static final String ALERTS_TYPE = "code";

  @Autowired
  public GithubCodeStandardViolationsService(
      GithubApiService apiService, @Value("${app.github.alerts.page.size:100}") Integer perPage) {
    super(apiService, perPage, ALERTS_TYPE);
  }

  @Override
  public CompletableFuture<List<CodeStandardViolationDetails>> fetchCodeViolations(
      String repoName, String accessToken) {
    return fetchAlerts(repoName, accessToken);
  }

  @Override
  protected Function<CodeStandardViolationResponse, CodeStandardViolationDetails> getMapper(
      String repoName) {
    return codeStandardViolationResponse ->
        codeStandardViolationResponse.toCodeStandardViolationDetails(repoName);
  }

  @Override
  protected Class<CodeStandardViolationResponse[]> getResponseType() {
    return CodeStandardViolationResponse[].class;
  }

  @Override
  protected CodeStandardViolationResponse[] getDefaultResponse() {
    return new CodeStandardViolationResponse[0];
  }
}
