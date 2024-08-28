package de.otto.platform.gitactionboard.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import de.otto.platform.gitactionboard.domain.workflow.WorkflowJob;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@EnableCaching
@Configuration
public class CacheConfig {
  @Bean
  @Primary
  public Caffeine caffeineConfig(@Value("${CACHE_EXPIRES_AFTER:60}") int expiresAfter) {
    return Caffeine.newBuilder().expireAfterWrite(expiresAfter, TimeUnit.SECONDS);
  }

  @Bean(name = "workflowJobDetailsCache")
  public Cache<String, List<WorkflowJob>> workflowJobDetailsCache() {
    return Caffeine.newBuilder().expireAfterAccess(12, TimeUnit.HOURS).build();
  }

  @Component
  @Qualifier("sharedCacheKeyGenerator")
  public static class SharedCacheKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
      return method.getName();
    }
  }
}
