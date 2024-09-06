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
import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;

@Inherited
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@IntegrationTest
@DirtiesContext
@Import({CodecConfig.class, SqliteDatabaseConfig.class})
@DataJdbcTest
@Transactional(propagation = NOT_SUPPORTED)
@ExtendWith(PersistingRepositoryCleanupExtension.class)
public @interface RepositoryTest {}
