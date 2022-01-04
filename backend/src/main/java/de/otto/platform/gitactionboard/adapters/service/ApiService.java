package de.otto.platform.gitactionboard.adapters.service;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ApiService {
  private final RestTemplateBuilder restTemplateBuilder;
  private final String authToken;

  public ApiService(
      RestTemplateBuilder restTemplateBuilder,
      @Qualifier("domainName") String baseUri,
      @Qualifier("ownerName") String ownerName,
      @Qualifier("authToken") String authToken) {
    this.restTemplateBuilder =
        restTemplateBuilder.rootUri(String.format("%s/repos/%s", baseUri, ownerName));
    this.authToken = authToken;
  }

  private RestTemplate getRestTemplate(String accessToken) {
    return restTemplateBuilder
        .defaultHeader(
            AUTHORIZATION,
            Optional.ofNullable(accessToken)
                .filter(token -> !token.startsWith("Basic"))
                .orElse(authToken))
        .build();
  }

  public <T> T getForObject(String url, String accessToken, Class<T> responseType) {
    return getRestTemplate(accessToken).getForObject(url, responseType);
  }
}
