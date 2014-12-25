package krasa.core.backend.config;

import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.sql.DataSource;

import org.springframework.context.annotation.*;

import com.jolbox.bonecp.BoneCPDataSource;

@Profile("!LOCAL_OVERNIGHT")
@Configuration
public class OvernightMysqlConfig extends CommonConfig {

	@Bean(destroyMethod = "close")
	public DataSource overnightDataSource() {
		BoneCPDataSource ds = new BoneCPDataSource();

		ds.setJdbcUrl(get("jdbc.overnight.url"));
		ds.setUsername(get("jdbc.overnight.username"));
		ds.setPassword(get("jdbc.overnight.password"));

		return setCommonProperties(ds);
	}

	private DataSource setCommonProperties(BoneCPDataSource ds) {
		ds.setDriverClass(get("jdbc.driverClassName"));
		ds.setIdleMaxAge(getLong("bonecp.idleMaxAge.mins"), TimeUnit.MINUTES);
		ds.setIdleConnectionTestPeriod(getLong("bonecp.idleConnectionTestPeriod.mins"), TimeUnit.MINUTES);

		ds.setMaxConnectionsPerPartition(getInteger("bonecp.maxConnectionsPerPartition"));
		ds.setMinConnectionsPerPartition(getInteger("bonecp.minConnectionsPerPartition"));

		ds.setPartitionCount(getInteger("bonecp.partitionCount"));
		ds.setAcquireIncrement(getInteger("bonecp.acquireIncrement"));
		ds.setStatementsCacheSize(getInteger("bonecp.statementsCacheSize"));

		ds.setConnectionTestStatement(get("bonecp.connectionTestStatement"));
		ds.setConnectionTimeout(getLong("bonecp.connectionTimeout.millis"), TimeUnit.MILLISECONDS);

		ds.setDisableJMX(true);
		ds.setLazyInit(true);

		return ds;
	}

	@Bean
	public Properties overnightHibernateProperties() {
		Properties properties = new Properties();
		properties.put("hibernate.dialect", get("hibernate.dialect"));
		properties.put("hibernate.show_sql", get("hibernate.show_sql"));
		properties.put("hibernate.hbm2ddl.auto", "validate");
		// properties.put("hibernate.hbm2ddl.import_files", importSql);

		return properties;
	}

}
