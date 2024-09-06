package de.otto.platform.gitactionboard.config;

import static org.sqlite.JDBC.PREFIX;

import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

@Slf4j
@Configuration
@EnableJdbcRepositories(basePackages = "de.otto.platform.gitactionboard")
public class SqliteDatabaseConfig {

  @Bean
  @Primary
  public DataSource dataSource(@Value("${sqlite.db.file.path}") String databaseFilePath) {
    final SQLiteConfig sqLiteConfig = new SQLiteConfig();
    sqLiteConfig.enforceForeignKeys(true);

    final SQLiteDataSource sqLiteDataSource = new SQLiteDataSource(sqLiteConfig);
    sqLiteDataSource.setUrl("%s%s".formatted(PREFIX, databaseFilePath));
    log.warn(
        "The SQLite database file is located at {}. Please ensure it is properly preserved for use across deployments.",
        databaseFilePath);

    return sqLiteDataSource;
  }
}
