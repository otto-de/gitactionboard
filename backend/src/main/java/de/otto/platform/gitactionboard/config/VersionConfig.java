package de.otto.platform.gitactionboard.config;

import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class VersionConfig {

  @Bean(name = "projectVersion")
  public String projectVersion(ApplicationContext applicationContext) {
    return applicationContext.getBeansWithAnnotation(SpringBootApplication.class).values().stream()
        .findFirst()
        .flatMap(es -> Optional.ofNullable(es.getClass().getPackage().getImplementationVersion()))
        .orElse("unknown");
  }
}
