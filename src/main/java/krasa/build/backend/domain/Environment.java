package krasa.build.backend.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import krasa.core.backend.domain.AbstractEntity;

import org.apache.commons.lang3.ObjectUtils;

import com.google.common.base.Objects;

@Entity
public class Environment extends AbstractEntity {
	public static Object NAME = "name";
	@Column(unique = true)
	protected String name;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "environment", orphanRemoval = true)
	private List<BuildableComponent> buildableComponents;

	public Environment() {
	}

	public Environment(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<BuildableComponent> getBuildableComponents() {
		if (buildableComponents == null) {
			buildableComponents = new ArrayList<BuildableComponent>();
		}
		return buildableComponents;
	}

	public void setBuildableComponents(List<BuildableComponent> buildableComponents) {
		this.buildableComponents = buildableComponents;
	}

	public void replaceBuilds(ArrayList<BuildableComponent> buildableComponents) {
		HashMap<String, BuildableComponent> newBuilds = toMap(buildableComponents);

		List<BuildableComponent> builds = getBuildableComponents();
		for (int i = 0; i < builds.size(); i++) {
			BuildableComponent oldBuild = builds.get(i);
			BuildableComponent newBuild = newBuilds.get(oldBuild.getName());
			if (newBuild != null) {
				builds.set(i, newBuild);
				newBuilds.remove(oldBuild.getName());
			}
		}

		for (BuildableComponent buildableComponent : newBuilds.values()) {
			builds.add(buildableComponent);
		}
	}

	private HashMap<String, BuildableComponent> toMap(ArrayList<BuildableComponent> buildableComponents) {
		HashMap<String, BuildableComponent> hashMap = new HashMap<String, BuildableComponent>();
		for (BuildableComponent buildableComponent : buildableComponents) {
			hashMap.put(buildableComponent.getName(), buildableComponent);
		}
		return hashMap;
	}

	private void add(BuildableComponent component) {
		component.setEnvironment(this);
		buildableComponents.add(component);
	}

	public BuildableComponent addBuildableComponent(String componentName) {
		for (BuildableComponent buildableComponetn : getBuildableComponents()) {
			if (buildableComponetn.getName().equals(componentName)) {
				return null;
			}
		}
		return createComponent(componentName);
	}

	private BuildableComponent createComponent(String componentName) {
		BuildableComponent buildableComponent = new BuildableComponent(componentName);
		add(buildableComponent);
		return buildableComponent;
	}

	public static List<Environment> sortByName(List<Environment> all) {
		Collections.sort(all, new Comparator<Environment>() {
			@Override
			public int compare(Environment o1, Environment o2) {
				return ObjectUtils.compare(o1.getName(), o2.getName());
			}
		});
		return all;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this).add("id", id).add("name", name).toString();
	}
}
