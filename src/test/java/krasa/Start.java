package krasa;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.webapp.WebAppContext;

public class Start {

	private static Server server;

	public static void main(String[] args) throws Exception {
		System.setProperty("spring.profiles.active", "DUMMY");
		System.setProperty("APPENDER", "STDOUT");
		System.setProperty("APPENDER2", "SIFT");
		// System.setProperty("APPENDER2", "STDOUT");
		// System.setProperty("spring.profiles.active", "LIVE");

		server = new Server();
		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setPort(1111);
		server.setConnectors(new Connector[] { connector });
		WebAppContext context = new WebAppContext();
		context.setServer(server);
		context.setContextPath("/");
		context.setWar("src/main/webapp");
		server.setHandler(context);
		server.start();
		server.join();
	}

}
