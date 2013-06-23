package krasa.build.backend.execution.process;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import krasa.build.backend.domain.Status;
import krasa.build.backend.execution.ProcessStatus;
import krasa.build.backend.execution.ssh.SCPInfo;
import krasa.build.backend.execution.ssh.SSHManager;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Value;

import com.jcraft.jsch.Channel;

public class SshBuildProcess extends AbstractProcess {
	@Value("${ssh.username}")
	String userName;
	@Value("${ssh.password}")
	String password;
	@Value("${ssh.connectionIP}")
	String connectionIP;
	protected SSHManager instance;
	protected List<String> command;

	protected SshBuildProcess() {
	}

	public SshBuildProcess(ProcessLog stringBufferTail, List<String> command) {
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
			processLog.newLine().append("--- PROCESS STARTED ---");
			doWork();
		} catch (Exception e) {
			processLog.newLine().append("--- PROCESS FAILED ---");
			processLog.newLine().append(ExceptionUtils.getStackTrace(e));
			throw e;
		}
	}

	protected int doWork() throws IOException {
		instance = new SSHManager(new SCPInfo(userName, password, connectionIP));
		// call sendCommand for each command and the output
		// (without prompts) is returned
		// stringBuilder.printingThread(System.out).start();
		Channel channel = instance.runCommands(command);
		processLog.receiveUntilLineEquals(channel.getInputStream(), "logout");

		channel.disconnect();

		int exitStatus = channel.getExitStatus();

		log.info("exit status: " + exitStatus);
		exitStatus = getExitStatusFromLog(exitStatus, processLog.getContent());

		if (exitStatus == 0) {
			processLog.newLine().append("--- PROCESS FINISHED ---");
			processStatus.setStatus(Status.SUCCESS);
		} else {
			processStatus.setStatus(Status.FAILED);
			processLog.newLine().append("--- PROCESS FAILED ---");
		}
		return exitStatus;
	}

	protected int getExitStatusFromLog(int exitStatus, String logContent) {
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
		instance.close();
		super.onFinally();
	}

	@Override
	public ProcessStatus getStatus() {
		if (instance != null) {
			if (!instance.isConnected() && processStatus.getStatus() == Status.IN_PROGRESS) {
				processStatus.setStatus(Status.DISCONNECTED);
			}
		}
		return processStatus;
	}

}
