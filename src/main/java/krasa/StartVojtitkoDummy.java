package krasa;

public class StartVojtitkoDummy {

	public static void main(String[] args) {
		System.setProperty("spring.profiles.active", "DUMMY, LOCAL_OVERNIGHT");
		System.setProperty("APPENDER", "SIFT");
		StartVojtitko.main(args);
	}
}
