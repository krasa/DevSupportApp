package krasa.build.backend.execution.strategy;

import java.util.List;

import krasa.build.backend.domain.*;

import org.junit.Test;

public class BuildCommandBuilderStrategyTest {

	protected BuildCommandBuilderStrategy buildCommandBuilderStrategy;

	@Test
	public void testToCommand() throws Exception {
		buildCommandBuilderStrategy = new BuildCommandBuilderStrategy();
		BuildableComponent component = new BuildableComponent();
		component.setName("MYSQL_CML_13100");
		component.setEnvironment(new Environment("foo"));
		List<String> stringList = buildCommandBuilderStrategy.toCommand(component);

		for (String s : stringList) {
			System.err.println(s);
		}
	}
}
