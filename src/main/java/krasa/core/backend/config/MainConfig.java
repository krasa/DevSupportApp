package krasa.core.backend.config;

import krasa.StartVojtitko;

import org.springframework.context.annotation.*;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@EnableAspectJAutoProxy(proxyTargetClass = true)
@Configuration
@ImportResource("classpath:spring.xml")
@ComponentScan(value = "krasa", excludeFilters = @ComponentScan.Filter(
		value = StartVojtitko.class,
		type = FilterType.ASSIGNABLE_TYPE
		))
public class MainConfig extends CommonConfig {

	public static final String HSQLDB_TX_MANAGER = "txManager";

	@Bean
	static public PropertySourcesPlaceholderConfigurer myPropertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
}
