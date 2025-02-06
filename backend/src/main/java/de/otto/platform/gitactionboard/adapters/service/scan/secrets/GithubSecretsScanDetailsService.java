package de.otto.platform.gitactionboard.adapters.service.scan.secrets;

import de.otto.platform.gitactionboard.adapters.service.GithubApiService;
import de.otto.platform.gitactionboard.adapters.service.scan.GithubAlertsService;
import de.otto.platform.gitactionboard.domain.scan.secrets.SecretsScanDetails;
import de.otto.platform.gitactionboard.domain.service.SecretsScanDetailsService;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class GithubSecretsScanDetailsService
    extends GithubAlertsService<SecretsScanDetailsResponse, SecretsScanDetails>
    implements SecretsScanDetailsService {

  private static final String ALERTS_TYPE = "secret";

  @Autowired
  public GithubSecretsScanDetailsService(
      GithubApiService apiService, @Value("${app.github.alerts.page.size:100}") Integer perPage) {
    super(apiService, perPage, ALERTS_TYPE);
  }

  @Override
  @Async
  public CompletableFuture<List<SecretsScanDetails>> fetchSecretsScanDetails(
      String repoName, String accessToken) {
    return fetchAlerts(repoName, accessToken);
  }

  @Override
  protected Function<SecretsScanDetailsResponse, SecretsScanDetails> getMapper(String repoName) {
    return secretsScanDetailsResponse -> secretsScanDetailsResponse.toSecretsScanDetails(repoName);
  }

  @Override
  protected SecretsScanDetailsResponse[] getDefaultResponse() {
    return new SecretsScanDetailsResponse[0];
  }

  @Override
  protected Class<SecretsScanDetailsResponse[]> getResponseType() {
    return SecretsScanDetailsResponse[].class;
  }
}
