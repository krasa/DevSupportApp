package krasa.build.backend.execution.strategy;

import java.util.*;

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
		if (buildableComponentName.startsWith("BUILD") || buildableComponentName.startsWith("RUN")) {
			sb.append("echo \"ENV ").append(environment);
			sb.append("\n").append(buildableComponentName);
			sb.append("\"|onbuild -c /dev/stdin");
		} else if (buildableComponentName.startsWith("PORTAL_")){
			String[] split = buildableComponentName.split("_");
			int i = Integer.parseInt(split[1]);
			sb.append("echo \"RUN_LOCAL '/tdev15/bin/deployPortalApp.sh -b " + i + " -e " + environment + "'");
			sb.append("\"|onbuild -c /dev/stdin");
		}else {
			sb.append("echo \"ENV ").append(environment);
			String[] split = buildableComponentName.split("_");
			String name = getName(split);
			sb.append("\nBUILD ").append(name.toLowerCase()).append(" BRANCH ").append(split[split.length - 1]);
			if (StringUtils.isNotBlank(buildMode)) {
				sb.append(" MODE ").append(buildMode.toUpperCase());
			}
			sb.append("\"|onbuild -c /dev/stdin");
		}
		return Collections.singletonList(sb.toString());
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
