package krasa;

import java.io.IOException;

/**
 * -agentpath:yourkit/win64yjpagent.dll
 */
public class StartVojtitkoDummy {

	public static void main(String[] args) throws IOException {
		System.setProperty("spring.profiles.active", "DUMMY, LOCAL_OVERNIGHT");
		System.setProperty("APPENDER", "SIFT");
		StartVojtitko.main(args);
	}
}
