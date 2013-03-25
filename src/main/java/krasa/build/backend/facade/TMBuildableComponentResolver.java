package krasa.build.backend.facade;

import krasa.build.backend.domain.BuildableComponent;

/**
 * @author Vojtech Krasa
 */
public class TMBuildableComponentResolver {

	public BuildableComponent createComponent(String name) {
		if (name.equals("...")) {
			name = "....";// TODO
		}
		return BuildableComponent.newComponent(name);
	}
}
