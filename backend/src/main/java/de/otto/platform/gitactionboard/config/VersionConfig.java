package de.otto.platform.gitactionboard.config;

import java.io.InputStream;
import java.util.Objects;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class VersionConfig {

  private static final String IMPLEMENTATION_VERSION = "Implementation-Version";

  @Bean(name = "projectVersion")
  public String projectVersion(ApplicationContext applicationContext) {
    return applicationContext.getBeansWithAnnotation(SpringBootApplication.class).values().stream()
        .map(es -> es.getClass().getPackage().getImplementationVersion())
        .filter(Objects::nonNull)
        .findFirst()
        .orElseGet(this::getVersionFromManifest);
  }

  private String getVersionFromManifest() {
    return Optional.ofNullable(getManifestAttributes().getValue(IMPLEMENTATION_VERSION))
        .orElse("unknown");
  }

  @SneakyThrows
  private Attributes getManifestAttributes() {
    final ClassLoader classLoader = this.getClass().getClassLoader();
    try (InputStream resourceAsStream = classLoader.getResourceAsStream("META-INF/MANIFEST.MF")) {
      final Manifest manifest = new Manifest(resourceAsStream);
      return manifest.getMainAttributes();
    }
  }
}
