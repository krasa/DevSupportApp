package krasa;

import java.io.IOException;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

/** use StartVojtitkoDummy, this class needs some special configuration... */
@SpringBootApplication
@EnableAutoConfiguration(exclude = { HibernateJpaAutoConfiguration.class, DataSourceAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class })
@ComponentScan(basePackages = "krasa")
public class StartVojtitko {

	public static void main(String[] args) throws IOException {
		start(args);
	}

	public static ConfigurableApplicationContext start(String[] args) {
		return new SpringApplicationBuilder().sources(StartVojtitko.class).run(args);
		// System.out.println("Let's inspect the beans provided by Spring Boot:");
		//
		// String[] beanNames = ctx.getBeanDefinitionNames();
		// Arrays.sort(beanNames);
		// for (String beanName : beanNames) {
		// System.out.println(beanName);
		// }
	}

}