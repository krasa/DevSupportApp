package krasa.build.backend.facade;

import krasa.build.backend.domain.BuildableComponent;

import org.springframework.stereotype.Service;

/**
 * @author Vojtech Krasa
 */
@Service
public class TMBuildableComponentResolver {

	public BuildableComponent createComponent(String name) {
		if (name.equals("...")) {
			name = "....";// TODO
		}
		return BuildableComponent.newComponent(name);
	}
}
