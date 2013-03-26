package krasa.build.backend.execution.adapter;

import java.util.HashSet;

import krasa.build.backend.dto.Result;
import krasa.build.backend.execution.ProcessLog;
import krasa.build.backend.execution.ProcessStatus;
import krasa.build.backend.execution.process.Process;
import krasa.build.backend.execution.process.ProcessResultListener;
import krasa.build.backend.facade.BuildFacade;
import krasa.core.backend.utils.Utils;
import krasa.merge.backend.dto.BuildRequest;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

public class ProcessAdapter implements ProcessResultListener {
	protected static final Logger log = LoggerFactory.getLogger(ProcessAdapter.class);
	@Autowired
	BuildFacade buildFacade;

	private BuildRequest request;
	private ProcessLog bufferTail;
	protected Process process;

	public ProcessAdapter() {
	}

	public ProcessAdapter(Process process, BuildRequest request, ProcessLog bufferTail) {
		this.process = process;
		this.request = request;
		this.bufferTail = bufferTail;
	}

	public BuildRequest getRequest() {
		return request;
	}

	public ProcessLog getBufferTail() {
		return bufferTail;
	}

	public Process getProcess() {
		return process;
	}

	public String getEnvironmentName() {
		return request.getEnvironmentName();
	}

	public boolean isAlive() {
		return process.getStatus().isAlive();
	}

	public Result getNextLog(int position) {
		Result result;
		synchronized (bufferTail) {
			result = bufferTail.getNext(position);
		}
		if (result.isNotEmpty()) {
			log.debug("getNextLog:" + position + " " + Utils.toLogFormat(result.getText()));
		}
		return result;
	}

	public void kill() {
		log.info("Killing");
		process.stop();
		buildFacade.onResult(getRequest(), process.getStatus());
		log.info("Process stopped");
	}

	public static ProcessAdapter dummy() {
		return new ProcessAdapter();
	}

	public HashSet<String> getComponentsAsList() {
		return new HashSet<String>(getRequest().getComponents());
	}

	@SuppressWarnings("ThrowableResultOfMethodCallIgnored")
	@Override
	public void onResult(ProcessStatus processStatus) {
		buildFacade.onResult(request, processStatus);
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsBuilder.reflectionEquals(this, obj);
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

}
