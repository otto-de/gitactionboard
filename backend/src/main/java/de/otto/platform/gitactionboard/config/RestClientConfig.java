package de.otto.platform.gitactionboard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.json.JsonMapper;

@Configuration
public class RestClientConfig {

  @Bean
  public BufferingClientHttpRequestFactory bufferingClientHttpRequestFactory() {
    return new BufferingClientHttpRequestFactory(new HttpComponentsClientHttpRequestFactory());
  }

  @Bean
  @Primary
  public RestClient.Builder restClientBuilder(
      JsonMapper jsonMapper, BufferingClientHttpRequestFactory requestFactory) {
    return RestClient.builder()
        .requestFactory(requestFactory)
        .configureMessageConverters(
            builder -> builder.withJsonConverter(new JacksonJsonHttpMessageConverter(jsonMapper)));
  }
}
