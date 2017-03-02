package krasa.build.backend.execution.process;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import krasa.build.backend.domain.BuildJob;
import krasa.build.backend.domain.Status;
import krasa.build.backend.execution.ProcessStatus;
import krasa.build.backend.execution.ssh.SCPInfo;
import krasa.core.frontend.pages.FileSystemLogUtils;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

public class SshjBuildProcess extends BuildJobProcess {
	private static final Logger log = LoggerFactory.getLogger(SshjBuildProcess.class);
	protected static final Logger sshOutput = LoggerFactory.getLogger("build.output");
	@Value("${ssh.username}")
	String userName;
	@Value("${ssh.password}")
	String password;
	@Value("${ssh.connectionIP}")
	String connectionIP;
	protected List<String> command;
	private Session session;
	private SSHClient ssh;
	private volatile boolean stop;

	public SshjBuildProcess(List<String> command, BuildJob buildJob) {
		super(buildJob);
		this.command = command;
	}

	private Properties getProperties() {
		Properties config = new Properties();
		config.put("StrictHostKeyChecking", "no");
		return config;
	}

	@Override
	public void runInternal() throws Exception {
		try {
			log.info("--- PROCESS STARTED ---\n");
			doWork();
		} catch (Throwable e) {
			log.info("--- PROCESS FAILED ---");
			log.info("", e);
			throw e;
		}
	}

	protected int doWork() throws IOException {
		connect();
		Session.Command cmd = session.exec(getCommand());
		receiveUntilLineEquals(cmd.getInputStream(), "logout");
		sshOutput.warn(IOUtils.readFully(cmd.getErrorStream()).toString());

		log.info("disconnecting channel");
		session.close();

		Integer exitStatus = cmd.getExitStatus();
		if (exitStatus == null) {
			exitStatus = -1;
		}
		log.info("exit status: " + exitStatus);
		File logFileByName = FileSystemLogUtils.getLogFileByName(buildJob.getLogFileName());
		exitStatus = getExitStatusFromLog(exitStatus, FileSystemLogUtils.readFromEnd(logFileByName, 1000));

		if (exitStatus == 0) {
			log.info("--- PROCESS FINISHED ---");
			processStatus.setStatus(Status.SUCCESS);
		} else {
			if (processStatus.getStatus() == Status.RUNNING) {
				processStatus.setStatus(Status.FAILED);
			}
			log.info("--- PROCESS FAILED ---");
		}
		return exitStatus;
	}

	public void receiveUntilLineEquals(InputStream inputStream, String until) throws IOException {
		BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
		String line;
		while (keepReceiving() && (line = r.readLine()) != null) {
			sshOutput.info(line);
			if (until.equals(line)) {
				log.trace("until condition received: " + line);
				return;
			}
		}
		log.debug("receiving done");
	}

	private String getCommand() {
		StringBuilder stringBuilder = new StringBuilder();
		for (String s : command) {
			stringBuilder.append(s);
			stringBuilder.append(" ");
		}

		// hack
		return stringBuilder.toString().replaceAll("onbuild", "sudo /data/overnight/bin/overnight");
	}

	private void connect() throws IOException {
		SCPInfo scpInfo = new SCPInfo(userName, password, connectionIP);
		ssh = new SSHClient();
		ssh.addHostKeyVerifier(new PromiscuousVerifier());
		ssh.loadKnownHosts();
		ssh.connect(scpInfo.getIP());
		ssh.authPassword(scpInfo.getUsername(), scpInfo.getPassword());

		session = ssh.startSession();
		session.allocateDefaultPTY();
	}

	protected int getExitStatusFromLog(Integer exitStatus, String logContent) {
		if (logContent.contains("returned code [")) {
			int start = logContent.indexOf("returned code [") + "returned code [".length();
			int end = logContent.indexOf("]", start);
			String substring1 = logContent.substring(start, end);
			exitStatus = Integer.parseInt(substring1.trim());
			log.info("exit status from log: " + exitStatus);
		} else {
			log.warn("unknown result:	" + logContent);
		}
		return exitStatus;
	}

	@Override
	protected void releaseResources() {
		if (session != null) {
			try {
				session.close();
			} catch (Throwable e) {
				log.error(String.valueOf(e), e);
				throw new RuntimeException(e);
			} finally {
				try {
					ssh.disconnect();
				} catch (IOException e) {
					log.error(String.valueOf(e), e);
				}
			}
		}
		super.releaseResources();
	}

	@Override
	public ProcessStatus getStatus() {
		if (session != null) {
			if (!session.isOpen() && processStatus.getStatus() == Status.RUNNING) {
				log.error("channel was disconnected");
				processStatus.setStatus(Status.DISCONNECTED);
				stop = true;
			}
		}
		return processStatus;
	}

	private boolean keepReceiving() {
		if (stop) {
			log.warn("#keepReceiving stop=true");
		}
		return !stop;
	}
}
