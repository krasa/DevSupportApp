package krasa.build.backend.domain;

import java.util.Date;

import javax.persistence.*;

import krasa.build.backend.dto.Result;
import krasa.build.backend.execution.ProcessStatus;
import krasa.build.backend.execution.process.*;
import krasa.build.backend.execution.process.Process;
import krasa.build.backend.facade.BuildFacade;
import krasa.core.backend.SpringApplicationContext;
import krasa.core.backend.domain.AbstractEntity;

import org.slf4j.*;

import com.google.common.base.Objects;

@Entity
public class BuildJob extends AbstractEntity implements ProcessStatusListener {

	protected static final Logger log = LoggerFactory.getLogger(BuildJob.class);
	@ManyToOne(optional = false, cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
	private BuildableComponent buildableComponent;
	@Column
	@Enumerated(EnumType.STRING)
	public volatile Status status = Status.PENDING;
	@Column
	public Date scheduledTime;
	@Column
	public Date startTime;
	@Column
	public Date endTime;
	@Column(length = 1000)
	public String command;
	@OneToOne(cascade = CascadeType.ALL, mappedBy = "buildJob")
	private BuildLog buildLog;
	@Column
	private String caller;
	@Transient
	protected Process process;

	protected BuildJob() {
	}

	public BuildJob(BuildableComponent buildableComponent) {
		this.process = process;
		this.buildableComponent = buildableComponent;
		scheduledTime = new Date();
	}

	public String getLogFileName() {
		return "build_" + id;
	}

	public Date getScheduledTime() {
		return scheduledTime;
	}

	public void setScheduledTime(Date scheduledTime) {
		this.scheduledTime = scheduledTime;
	}

	public String getCaller() {
		return caller;
	}

	public void setCaller(String caller) {
		this.caller = caller;
	}

	public BuildLog getBuildLog() {
		return buildLog;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public void setBuildLog(BuildLog buildLog) {
		this.buildLog = buildLog;
	}

	public void setProcess(Process process) {
		this.process = process;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public BuildableComponent getBuildableComponent() {
		return buildableComponent;
	}

	public void setBuildableComponent(BuildableComponent buildableComponent) {
		this.buildableComponent = buildableComponent;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public Process getProcess() {
		return process;
	}

	public boolean isProcessAlive() {
		if (process == null) {
			return false;
		}
		return process.getStatus().isAlive();
	}

	public boolean isNotFinished() {
		return status == Status.RUNNING || status == Status.PENDING;
	}

	public void kill() {
		log.info("Killing");
		process.stop();
		log.info("Process stopped");
	}

	@Override
	public void onStatusChanged(ProcessStatus processStatus) {
		try {
			SpringApplicationContext.getBean(BuildFacade.class).onStatusChanged(this, processStatus);
		} catch (Throwable e) {
			log.error("#onStatusChanged failed", e);
		}
	}

	public Result getLog() {
		if (process == null) {
			if (buildLog == null) {
				return Result.empty(0);
			}
			return new Result(buildLog.getLogContent().length(), buildLog.getLogContent());
		}
		return process.getProcessLog().getNext(0);
	}

	public Result getNextLog(int length) {
		if (process == null) {
			return Result.empty(length);
		}
		return process.getProcessLog().getNext(length);
	}

	public void onBeforeSave() {
		fillBuildLogFromProcessLog();
	}

	private void fillBuildLogFromProcessLog() {
		if (getProcess() != null) {
			BuildLog buildLog = this.buildLog;
			if (buildLog == null) {
				buildLog = new BuildLog();
				buildLog.setBuildJob(this);
				setBuildLog(buildLog);
			}
			buildLog.setLogContent(getProcess().getProcessLog().getContent());
		}
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("id", id).add("command", command).add("buildableComponent",
				buildableComponent).add("process", process).add("status", status).add("startTime", startTime).add(
				"endTime", endTime).toString();
	}
}
