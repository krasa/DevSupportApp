package krasa.build.backend.execution.strategy;

import java.util.Arrays;
import java.util.List;

import krasa.merge.backend.dto.BuildRequest;

import org.springframework.stereotype.Component;

@Component
public class BuildCommandBuilderStrategy {
	public List<String> toCommand(BuildRequest request) {
		String environment = request.getEnvironmentName();
		StringBuilder sb = new StringBuilder();
		sb.append("echo \"ENV ").append(environment);
		for (String ss : request.getComponents()) {
			if (ss.startsWith("BUILD") || ss.startsWith("RUN")) {
				sb.append("\n").append(ss);
			} else {
				String[] split = ss.split("_");
				String name = getName(split);
				sb.append("\nBUILD ").append(name.toLowerCase()).append(" BRANCH ").append(split[split.length - 1]);
			}
		}
		sb.append("\"|onbuild -c /dev/stdin");

		return Arrays.asList(sb.toString(), "exit");
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
