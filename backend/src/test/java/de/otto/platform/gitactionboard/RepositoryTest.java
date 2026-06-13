package de.otto.platform.gitactionboard;

import static org.springframework.transaction.annotation.Propagation.NOT_SUPPORTED;

import de.otto.platform.gitactionboard.config.CodecConfig;
import de.otto.platform.gitactionboard.config.SqliteDatabaseConfig;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.data.jdbc.test.autoconfigure.DataJdbcTest;
import org.springframework.boot.liquibase.autoconfigure.LiquibaseAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
// In Spring Boot 4.x, @DataJdbcTest only auto-configures DataJdbcRepositoriesAutoConfiguration.
// Liquibase moved to the separate spring-boot-liquibase module and is no longer included in the
// @DataJdbcTest slice. We must explicitly import it so the schema is created before tests run.
@IntegrationTest
@DirtiesContext
@Import({CodecConfig.class, SqliteDatabaseConfig.class})
@DataJdbcTest
@ImportAutoConfiguration(LiquibaseAutoConfiguration.class)
@Transactional(propagation = NOT_SUPPORTED)
@ExtendWith(PersistingRepositoryCleanupExtension.class)
public @interface RepositoryTest {}
