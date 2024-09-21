package de.otto.platform.gitactionboard;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Optional;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.springframework.context.ApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@Slf4j
public class PersistingRepositoryCleanupExtension
    implements AfterAllCallback, AfterEachCallback, BeforeEachCallback {

  @Override
  public void afterAll(ExtensionContext context) throws Exception {
    context
        .getTestClass()
        .map(testClass -> isAnnotatedWithCleanupExtension(testClass) || isRootTestClass(context))
        .filter(Boolean::booleanValue)
        .ifPresent(persistingRepositoryCleanupExtension -> removeSqliteDB(context));
  }

  private boolean isAnnotatedWithCleanupExtension(Class<?> testClass) {
    return Optional.ofNullable(testClass.getAnnotation(ExtendWith.class))
        .map(
            extendWith ->
                Arrays.asList(extendWith.value())
                    .contains(PersistingRepositoryCleanupExtension.class))
        .orElse(false);
  }

  private boolean isRootTestClass(ExtensionContext context) {
    return context.getTestClass().map(this::isNestedClass).filter(Boolean::booleanValue).isEmpty();
  }

  private boolean isNestedClass(Class<?> currentClass) {
    return currentClass.isAnnotationPresent(Nested.class);
  }

  @SneakyThrows
  private static void removeSqliteDB(ExtensionContext context) {
    final Path dbAbsolutePath = getSqliteFilePath(context);

    log.info(
        "Deletion of Sqlite db file {} is {}",
        dbAbsolutePath,
        Files.deleteIfExists(dbAbsolutePath) ? "completed" : "failed");
  }

  private static Path getSqliteFilePath(ExtensionContext context) {
    return Optional.ofNullable(
            getApplicationContext(context).getEnvironment().getProperty("sqlite.db.file.path"))
        .map(filePath -> Path.of(filePath).toAbsolutePath())
        .orElseThrow(
            () ->
                new IllegalStateException(
                    "Unable to read `sqlite.db.file.path` from properties file"));
  }

  private static JdbcTemplate getJdbcTemplate(ExtensionContext context) {
    return getApplicationContext(context).getBean(JdbcTemplate.class);
  }

  private static ApplicationContext getApplicationContext(ExtensionContext context) {
    return SpringExtension.getApplicationContext(context);
  }

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    truncateApplicationTables(context);
  }

  @Override
  public void afterEach(ExtensionContext context) throws Exception {
    truncateApplicationTables(context);
  }

  private static void truncateApplicationTables(ExtensionContext context) {
    final JdbcTemplate jdbcTemplate = getJdbcTemplate(context);

    jdbcTemplate
        .queryForList(
            "select name from sqlite_master where type in ('table') and name not in ('DATABASECHANGELOG', 'DATABASECHANGELOGLOCK') order by rootpage desc;",
            String.class)
        .stream()
        .map("delete from %s;"::formatted)
        .peek(query -> log.info("Running query `{}` to cleanup", query))
        .forEach(jdbcTemplate::execute);
    log.info("Cleanup completed");
  }
}
