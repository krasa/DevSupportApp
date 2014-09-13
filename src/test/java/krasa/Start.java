package krasa;

import org.eclipse.jetty.server.*;
import org.eclipse.jetty.server.nio.NetworkTrafficSelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;

public class Start {

	private static Server server;

	public static void main(String[] args) throws Exception {
		System.setProperty("spring.profiles.active", "DUMMY, LOCAL_OVERNIGHT");
		// System.setProperty("spring.profiles.active", "DUMMY");
		System.setProperty("APPENDER", "SIFT");
		// System.setProperty("APPENDER2", "STDOUT");
		// System.setProperty("spring.profiles.active", "LIVE");

		startJetty("/", 8765, "src/main/webapp");
	}

	public static void startJetty(String contextPath, int port, String warPath) throws Exception {
		Server server = new Server();
		try (ServerConnector connector = new NetworkTrafficSelectChannelConnector(server, new HttpConnectionFactory(
				new HttpConfiguration()));) {
			connector.setPort(port);
			server.addConnector(connector);
			WebAppContext context = new WebAppContext();
			context.setServer(server);
			context.setContextPath(contextPath);
			context.setWar(warPath);
			server.setHandler(context);
			server.start();
			server.join();
		}
	}

}
