package krasa;

import java.io.IOException;

import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * -agentpath:yourkit/win64yjpagent.dll
 */
public class StartVojtitkoDummy {

	public static void main(String[] args) throws IOException {
		// System.setProperty("spring.profiles.active", "DUMMY, LOCAL_OVERNIGHT");
		System.setProperty("spring.profiles.active", "LOCAL_OVERNIGHT");
		ConfigurableApplicationContext applicationContext = StartVojtitko.start(args);
		System.in.read();
		SpringApplication.exit(applicationContext);
	}
}
