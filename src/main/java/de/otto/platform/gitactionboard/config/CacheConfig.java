package de.otto.platform.gitactionboard.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableCaching
@Configuration
public class CacheConfig {
  @Bean
  public Caffeine caffeineConfig(@Value("${CACHE_EXPIRES_AFTER:60}") int expiresAfter) {
    return Caffeine.newBuilder().expireAfterWrite(expiresAfter, TimeUnit.SECONDS);
  }
}
