package krasa.core.backend.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.embedded.*;

@Profile("LOCAL_OVERNIGHT")
@Configuration
public class OvernightH2Config extends CommonConfig {

	@Bean
	public DataSource overnightDataSource() {
		EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
		EmbeddedDatabase db = builder.setType(EmbeddedDatabaseType.H2).setName("overnightsDb").build();
		return db;
	}

	@Bean
	public Properties overnightHibernateProperties() {
		Properties properties = new Properties();
		properties.put("hibernate.dialect", get("hibernate.dialect"));
		properties.put("hibernate.show_sql", get("hibernate.show_sql"));
		properties.put("hibernate.hbm2ddl.auto", "create");
		// properties.put("hibernate.hbm2ddl.import_files", importSql);

		return properties;
	}
}
