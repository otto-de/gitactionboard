package de.otto.platform.gitactionboard.config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.scheduling.annotation.AsyncAnnotationBeanPostProcessor;
import org.springframework.scheduling.annotation.EnableAsync;

@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig {
  @Primary
  @Bean(
      name = {
        TaskExecutionAutoConfiguration.APPLICATION_TASK_EXECUTOR_BEAN_NAME,
        AsyncAnnotationBeanPostProcessor.DEFAULT_TASK_EXECUTOR_BEAN_NAME
      })
  @Conditional(SingleCoreCondition.class)
  public ExecutorService taskExecutor() {
    log.warn(
        "Single CPU core detected. Task execution may be slower, consider allocating more cores for optimal performance.");
    return Executors.newSingleThreadExecutor();
  }

  public static class SingleCoreCondition implements Condition {
    @Override
    public boolean matches(
        @NonNull ConditionContext context, @NonNull AnnotatedTypeMetadata metadata) {
      final int availableProcessors = Runtime.getRuntime().availableProcessors();
      return availableProcessors == 1;
    }
  }
}
