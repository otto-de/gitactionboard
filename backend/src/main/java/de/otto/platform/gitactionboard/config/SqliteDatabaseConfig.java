package de.otto.platform.gitactionboard.config;

import static org.sqlite.JDBC.PREFIX;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.sqlite.SQLiteConfig;
import org.sqlite.SQLiteDataSource;

@Configuration
@EnableJdbcRepositories(basePackages = "de.otto.platform.gitactionboard")
public class SqliteDatabaseConfig {

  @Bean
  @Primary
  public DataSource dataSource(@Value("${sqlite.db.file.path}") String databaseName) {
    final SQLiteConfig sqLiteConfig = new SQLiteConfig();
    sqLiteConfig.enforceForeignKeys(true);

    final SQLiteDataSource sqLiteDataSource = new SQLiteDataSource(sqLiteConfig);
    sqLiteDataSource.setUrl(PREFIX + databaseName);
    return sqLiteDataSource;
  }
}
