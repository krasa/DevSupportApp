package krasa.build.backend.execution.ssh;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import krasa.build.backend.execution.process.ProcessLog;

import org.junit.Ignore;
import org.junit.Test;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

@Ignore
public class SSHBuildsTest {
	/**
	 * Test of sendCommand method, of class JschSSHManager.
	 */
	@Test
	public void testSendCommand() throws InterruptedException, IOException {
		System.out.println("1sendCommand");

		/**
		 * YOU MUST CHANGE THE FOLLOWING FILE_NAME: A FILE IN THE DIRECTORY USER: LOGIN USER NAME PASSWORD: PASSWORD FOR
		 * THAT USER HOST: IP ADDRESS OF THE SSH SERVER
		 **/
		String command = "xxx";
		String userName = "xxx";
		String password = "xxx";
		String connectionIP = "xxx";
		SCPInfo scpInfo = new SCPInfo();
		scpInfo.setPassword(password);
		scpInfo.setUsername(userName);
		scpInfo.setIP(connectionIP);
		runCommands(scpInfo, Arrays.asList(command));
	}

	public static void runCommands(SCPInfo scpInfo, List<String> commands) {
		try {
			JSch jsch = new JSch();
			Session session = jsch.getSession(scpInfo.getUsername(), scpInfo.getIP());
			session.setPassword(scpInfo.getPassword());
			setUpHostKey(session);
			session.connect();

			Channel channel = session.openChannel("shell");// only shell

			InputStream inputStream = channel.getInputStream();

			ProcessLog stringBufferTail = new ProcessLog();
			stringBufferTail.receivingThread(inputStream).start();
			stringBufferTail.printingThread(System.out).start();

			PrintStream shellStream = new PrintStream(channel.getOutputStream()); // printStream for convenience
			channel.connect();
			for (String command : commands) {
				shellStream.println(command);
				shellStream.flush();
			}

			Thread.sleep(60000);

			channel.disconnect();
			session.disconnect();
		} catch (Exception e) {
			System.err.println("ERROR: Connecting via shell to " + scpInfo.getIP());
			e.printStackTrace();
		}
	}

	private static void setUpHostKey(Session session) {
		// Note: There are two options to connect
		// 1: Set StrictHostKeyChecking to no
		// Create a Properties Object
		// Set StrictHostKeyChecking to no
		// session.setConfig(config);
		// 2: Use the KnownHosts File
		// Manually ssh into the appropriate machines via unix
		// Go into the .ssh\known_hosts file and grab the entries for the hosts
		// Add the entries to a known_hosts file
		// jsch.setKnownHosts(khfile);
		java.util.Properties config = new java.util.Properties();
		config.put("StrictHostKeyChecking", "no");
		session.setConfig(config);
	}
}
