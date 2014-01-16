package krasa.build.backend.execution.strategy;

import java.util.Arrays;
import java.util.List;

import krasa.build.backend.domain.BuildableComponent;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class BuildCommandBuilderStrategy {

	public List<String> toCommand(BuildableComponent buildableComponent) {
		String environment = buildableComponent.getEnvironment().getName();
		String buildableComponentName = buildableComponent.getName();
		String buildMode = buildableComponent.getBuildMode();

		StringBuilder sb = new StringBuilder();
		sb.append("echo \"ENV ").append(environment);
		if (buildableComponentName.startsWith("BUILD") || buildableComponentName.startsWith("RUN")) {
			sb.append("\n").append(buildableComponentName);
		} else {
			String[] split = buildableComponentName.split("_");
			String name = getName(split);
			sb.append("\nBUILD ").append(name.toLowerCase()).append(" BRANCH ").append(split[split.length - 1]);
			if (StringUtils.isNotBlank(buildMode)) {
				sb.append(" MODE ").append(buildMode.toUpperCase());
			}
		}
		sb.append("\"|onbuild -c /dev/stdin");

		return Arrays.asList(sb.toString());
	}

	private String getName(String[] split) {
		String name = "";
		for (int i = 0; i < split.length - 1; i++) {
			if (i == 0) {
				name = split[i];
			} else {
				name = name + "-" + split[i];
			}
		}
		return name;
	}
}
