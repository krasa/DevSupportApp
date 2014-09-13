package krasa.core.backend.config;

import org.springframework.context.annotation.*;

@Configuration
@ImportResource("classpath:spring.xml")
@ComponentScan("krasa")
public class MainConfig extends CommonConfig {

	public static final String HSQLDB_TX_MANAGER = "txManager";
}
