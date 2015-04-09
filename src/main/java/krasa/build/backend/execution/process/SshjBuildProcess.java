package krasa.build.backend.execution.process;

import java.io.IOException;
import java.util.*;

import krasa.build.backend.domain.*;
import krasa.build.backend.execution.ProcessStatus;
import krasa.build.backend.execution.ssh.SCPInfo;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;

public class SshjBuildProcess extends AbstractProcess {

	@Value("${ssh.username}")
	String userName;
	@Value("${ssh.password}")
	String password;
	@Value("${ssh.connectionIP}")
	String connectionIP;
	protected List<String> command;
	private Session session;
	private SSHClient ssh;

	public SshjBuildProcess(ProcessLog stringBufferTail, List<String> command, BuildJob buildJob) {
		super(buildJob);
		this.processLog = stringBufferTail;
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
			processLog.newLine().append("--- PROCESS STARTED ---\n");
			doWork();
		} catch (Exception e) {
			processLog.newLine().append("--- PROCESS FAILED ---");
			processLog.newLine().append(ExceptionUtils.getStackTrace(e));
			throw e;
		}
	}

	protected int doWork() throws IOException {
		connect();
		Session.Command cmd = session.exec(getCommand());
		processLog.receiveUntilLineEquals(cmd.getInputStream(), "logout");
		processLog.readFully(cmd.getErrorStream());

		log.info("disconnecting channel");
		session.close();

		Integer exitStatus = cmd.getExitStatus();
		if (exitStatus == null) {
			exitStatus = -1;
		}
		log.info("exit status: " + exitStatus);
		exitStatus = getExitStatusFromLog(exitStatus, processLog.getContent());

		if (exitStatus == 0) {
			processLog.newLine().append("--- PROCESS FINISHED ---");
			processStatus.setStatus(Status.SUCCESS);
		} else {
			if (processStatus.getStatus() == Status.RUNNING) {
				processStatus.setStatus(Status.FAILED);
			}
			processLog.newLine().append("--- PROCESS FAILED ---");
		}
		return exitStatus;
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
		int start1 = logContent.length() - 100;
		String substring = StringUtils.substring(logContent, start1 > 0 ? start1 : 0);
		if (substring.contains("returned code [")) {
			int start = substring.indexOf("returned code [") + "returned code [".length();
			int end = substring.indexOf("]", start);
			String substring1 = substring.substring(start, end);
			exitStatus = Integer.parseInt(substring1.trim());
			log.info("exit status from log: " + exitStatus);
		} else {
			log.warn("unknown result:	" + substring);
		}
		return exitStatus;
	}

	@Override
	protected void onFinally() {
		if (session != null) {
			try {
				session.close();
			} catch (Exception e) {
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
		super.onFinally();
	}

	@Override
	public ProcessStatus getStatus() {
		if (session != null) {
			if (!session.isOpen() && processStatus.getStatus() == Status.RUNNING) {
				log.error("channel was disconnected");
				processStatus.setStatus(Status.DISCONNECTED);
				processLog.stop();
			}
		}
		return processStatus;
	}

}
