package krasa.build.backend.execution.strategy;

import java.util.ArrayList;
import java.util.List;

import krasa.build.backend.domain.BuildRequest;
import krasa.build.backend.domain.BuildableComponent;
import krasa.build.backend.execution.ProcessBuilder;

import org.junit.Test;

public class BuildCommandBuilderStrategyTest {

	protected BuildCommandBuilderStrategy buildCommandBuilderStrategy;

	@Test
	public void testToCommand() throws Exception {
		krasa.build.backend.execution.ProcessBuilder processBuilder = new ProcessBuilder();
		List<String> components = new ArrayList<String>();
		components.add("CML_13100");
		components.add("BUILD msc-data-ng BRANCH 8200 VAR ENV ref3");
		buildCommandBuilderStrategy = new BuildCommandBuilderStrategy();
		BuildableComponent components1 = new BuildableComponent();
		components1.setName("MYSQL_CML_13100");
		List<String> stringList = buildCommandBuilderStrategy.toCommand(new BuildRequest(components1));

		for (String s : stringList) {
			System.err.println(s);
		}
	}
}
