package krasa.core.backend.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.*;
import org.springframework.context.annotation.*;
import org.springframework.orm.hibernate4.*;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@Configuration
public class OvernightConfig extends CommonConfig {

	public static final String TX_MANAGER = "overnightTxManager";

	@Autowired
	@Qualifier("overnightDataSource")
	private DataSource dataSource;
	@Qualifier("overnightHibernateProperties")
	private Properties hibernateProperties;

	@Bean(name = TX_MANAGER)
	public HibernateTransactionManager overnightTxManager() {
		HibernateTransactionManager htm = new HibernateTransactionManager();
		htm.setSessionFactory(overnightSessionFactory().getObject());

		return htm;
	}

	@Bean(name = "overnightSessionFactory")
	public LocalSessionFactoryBean overnightSessionFactory() {
		return getSessionFactory(hibernateProperties, dataSource, "krasa.overnight.domain");
	}

	public LocalSessionFactoryBean getSessionFactory(Properties properties, DataSource ds, String... packagesToScan) {
		LocalSessionFactoryBean lsfb = new LocalSessionFactoryBean();
		lsfb.setDataSource(ds);
		lsfb.setHibernateProperties(properties);
		lsfb.setPackagesToScan(packagesToScan);

		return lsfb;
	}

}
