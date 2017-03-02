package krasa.build.backend.domain;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;

import krasa.build.backend.execution.ProcessStatus;
import krasa.build.backend.execution.process.BuildJobProcess;
import krasa.build.backend.execution.process.ProcessStatusListener;
import krasa.build.backend.facade.BuildFacade;
import krasa.core.backend.SpringApplicationContext;
import krasa.core.backend.domain.AbstractEntity;

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
	@Column
	private String caller;
	@Transient
	protected BuildJobProcess buildJobProcess;

	protected BuildJob() {
	}

	public BuildJob(BuildableComponent buildableComponent) {
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

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public void setBuildJobProcess(BuildJobProcess buildJobProcess) {
		this.buildJobProcess = buildJobProcess;
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

	public BuildJobProcess getBuildJobProcess() {
		return buildJobProcess;
	}

	public boolean isProcessAlive() {
		if (buildJobProcess == null) {
			return false;
		}
		return buildJobProcess.getStatus().isAlive();
	}

	public boolean isNotFinished() {
		return status == Status.RUNNING || status == Status.PENDING;
	}

	public void kill(String reason) {
		log.info("Killing");
		buildJobProcess.stop(reason);
		log.info("Process stopped");
	}

	@Override
	public void onStatusChanged(BuildJob buildJob, ProcessStatus processStatus) {
		try {
			SpringApplicationContext.getBean(BuildFacade.class).onStatusChanged(this, processStatus);
		} catch (Throwable e) {
			log.error("#onStatusChanged failed", e);
		}
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("caller", caller).add("id", id).add("command", command).add("buildableComponent",
				buildableComponent).add("process", buildJobProcess).add("status", status).add("startTime", startTime).add(
				"endTime", endTime).toString();
	}
}
