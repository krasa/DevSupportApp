package krasa.build.backend.execution.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.constraints.Null;

import krasa.build.backend.exception.ProcessAlreadyRunning;
import krasa.merge.backend.dto.BuildRequest;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;

@Component
public class ProcessAdapterHolder {
	protected final Logger log = LoggerFactory.getLogger(getClass());

	private static ListMultimap<String, ComponentToProcess> progressMap = Multimaps.synchronizedListMultimap(ArrayListMultimap.<String, ComponentToProcess> create());

	@Null
	public ProcessAdapter get(BuildRequest request) {
		Set<String> componentsAsSet = request.getComponentsAsSet();
		List<ComponentToProcess> buildProgresses = progressMap.get(getKey(request));
		for (ComponentToProcess componentToProcess : buildProgresses) {
			if (componentsAsSet.contains(componentToProcess.component)) {
				return componentToProcess.processAdapter;
			}
		}
		return null;
	}

	public void put(ProcessAdapter progress) {
		BuildRequest buildRequest = progress.getRequest();
		for (String s : buildRequest.getComponents()) {
			progressMap.put(getKey(buildRequest), new ComponentToProcess(s, progress));
		}
	}

	private static String getKey(BuildRequest request) {
		return request.getEnvironmentName();
	}

	public void checkPreviousBuilds(BuildRequest request) {
		ArrayList<ComponentToProcess> affected = getComponentToProcesses(request);
		throwExceptionIfAlive(affected);
		remove(affected, request);
	}

	private void remove(ArrayList<ComponentToProcess> affected, BuildRequest request) {
		for (ComponentToProcess componentToProcess : affected) {
			ProcessAdapter processAdapter = componentToProcess.processAdapter;
			log.debug("previous process found dead, removing " + processAdapter);
			progressMap.remove(getKey(request), componentToProcess);

		}
	}

	private void throwExceptionIfAlive(ArrayList<ComponentToProcess> affected) {
		for (ComponentToProcess componentToProcess : affected) {
			ProcessAdapter processAdapter = componentToProcess.processAdapter;
			if (processAdapter.isAlive()) {
				log.debug("process already running" + processAdapter);
				throw new ProcessAlreadyRunning(processAdapter);
			}
		}
	}

	private ArrayList<ComponentToProcess> getComponentToProcesses(BuildRequest request) {
		ArrayList<ComponentToProcess> affected = new ArrayList<ComponentToProcess>();
		Set<String> componentsAsSet = request.getComponentsAsSet();
		List<ComponentToProcess> buildProgresses = progressMap.get(getKey(request));
		for (ComponentToProcess componentToProcess : buildProgresses) {
			if (componentsAsSet.contains(componentToProcess.component)) {
				affected.add(componentToProcess);
			}
		}
		return affected;
	}

	class ComponentToProcess {
		String component;
		ProcessAdapter processAdapter;

		ComponentToProcess(String component, ProcessAdapter processAdapter) {
			this.component = component;
			this.processAdapter = processAdapter;
		}

		@Override
		public boolean equals(Object obj) {
			return EqualsBuilder.reflectionEquals(this, obj);
		}

		@Override
		public int hashCode() {
			return HashCodeBuilder.reflectionHashCode(this);
		}
	}

}
