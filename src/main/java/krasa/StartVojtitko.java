package krasa;

import java.io.IOException;

import org.apache.wicket.protocol.ws.javax.MyWicketServerEndpointConfig;
import org.eclipse.jetty.server.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.system.*;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.*;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.context.embedded.*;
import org.springframework.boot.context.embedded.jetty.*;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.config.annotation.*;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

@Configuration
@EnableAutoConfiguration(exclude = { HibernateJpaAutoConfiguration.class, DataSourceAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class })
@ComponentScan(basePackages = "krasa")
public class StartVojtitko extends SpringBootServletInitializer implements WebSocketConfigurer {

	public static void main(String[] args) throws IOException {
		System.setProperty("APPENDER", "SIFT");
		SpringApplication springApplication = new SpringApplication(StartVojtitko.class);
		springApplication.addListeners(new ApplicationPidFileWriter());
		springApplication.addListeners(new EmbeddedServerPortFileWriter());
		ConfigurableApplicationContext run = springApplication.run(args);
		System.in.read();
		SpringApplication.exit(run);
		// System.out.println("Let's inspect the beans provided by Spring Boot:");
		//
		// String[] beanNames = ctx.getBeanDefinitionNames();
		// Arrays.sort(beanNames);
		// for (String beanName : beanNames) {
		// System.out.println(beanName);
		// }
	}

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {

	}

	@Component
	public static class JettyCustomizer implements EmbeddedServletContainerCustomizer {

		@Override
		public void customize(ConfigurableEmbeddedServletContainer container) {
			if (container instanceof JettyEmbeddedServletContainerFactory) {
				configureJetty((JettyEmbeddedServletContainerFactory) container);
			}
		}

		private void configureJetty(JettyEmbeddedServletContainerFactory jettyFactory) {
			jettyFactory.addServerCustomizers(new JettyServerCustomizer() {

				@Override
				public void customize(Server server) {
					ServerConnector serverConnector = new ServerConnector(server);
					serverConnector.setPort(8765);
					server.addConnector(serverConnector);
				}
			});
		}

	}

	@Bean
	public ServerEndpointExporter serverEndpointExporter() {
		return new ServerEndpointExporter();
	}

	@Bean
	public MyWicketServerEndpointConfig myWicketServerEndpointConfig() {
		return new MyWicketServerEndpointConfig();
	}

}