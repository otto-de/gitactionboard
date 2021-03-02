package de.otto.platform.gitactionboard.config;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class GithubApiConfig {
  @Bean
  public RestTemplate restTemplate(
      RestTemplateBuilder builder,
      @Qualifier("domainName") String baseUri,
      @Qualifier("ownerName") String ownerName,
      @Qualifier("authToken") String authToken) {
    return builder
        .rootUri(String.format("%s/repos/%s", baseUri, ownerName))
        .defaultHeader(AUTHORIZATION, authToken)
        .build();
  }
}
