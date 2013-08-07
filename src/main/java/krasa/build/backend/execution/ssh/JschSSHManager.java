package krasa.build.backend.execution.ssh;

import java.io.PrintStream;
import java.util.List;

import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

public class JschSSHManager {
	protected static final org.slf4j.Logger log = LoggerFactory.getLogger(JschSSHManager.class);

	protected Channel channel;
	protected static Session session;
	private SCPInfo scpInfo;

	public JschSSHManager(SCPInfo scpInfo) {
		this.scpInfo = scpInfo;
	}

	public Channel runCommands(List<String> commands) {
		try {
			JSch jsch = new JSch();
			session = jsch.getSession(scpInfo.getUsername(), scpInfo.getIP());
			session.setPassword(scpInfo.getPassword());
			setUpHostKey(session);
			session.connect();

			channel = session.openChannel("shell");

			PrintStream shellStream = new PrintStream(channel.getOutputStream());
			channel.connect();
			for (String command : commands) {
				shellStream.println(command);
				shellStream.flush();
			}
			return channel;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static void setUpHostKey(Session session) {
		java.util.Properties config = new java.util.Properties();
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);
	}

	public void close() {
		try {
			channel.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			session.disconnect();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isConnected() {
		return session.isConnected();
	}
}
