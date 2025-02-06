package de.otto.platform.gitactionboard.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

  @Bean
  public BufferingClientHttpRequestFactory bufferingClientHttpRequestFactory() {
    return new BufferingClientHttpRequestFactory(new HttpComponentsClientHttpRequestFactory());
  }

  @Bean
  @Primary
  private static RestClient.Builder restClientBuilder(
      ObjectMapper objectMapper, BufferingClientHttpRequestFactory requestFactory) {
    return RestClient.builder()
        .requestFactory(requestFactory)
        .messageConverters(
            converters -> {
              converters.removeIf(MappingJackson2HttpMessageConverter.class::isInstance);
              converters.add(new MappingJackson2HttpMessageConverter(objectMapper));
            });
  }
}
