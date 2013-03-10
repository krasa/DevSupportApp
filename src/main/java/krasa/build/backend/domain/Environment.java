package krasa.build.backend.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import krasa.core.backend.domain.AbstractEntity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@Entity
public class Environment extends AbstractEntity implements Serializable {
	public static Object NAME = "name";

	@Column(unique = true)
	protected String name;
	@OneToMany(cascade = CascadeType.ALL, mappedBy = "environment", orphanRemoval = true)
	private List<ComponentBuild> componentBuilds;

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

	public List<ComponentBuild> getComponentBuilds() {
		if (componentBuilds == null) {
			componentBuilds = new ArrayList<ComponentBuild>();
		}
		return componentBuilds;
	}

	public void setComponentBuilds(List<ComponentBuild> componentBuilds) {
		this.componentBuilds = componentBuilds;
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

	public void replaceBuilds(ArrayList<ComponentBuild> componentBuilds) {
		HashMap<String, ComponentBuild> newBuilds = toMap(componentBuilds);

		List<ComponentBuild> builds = getComponentBuilds();
		for (int i = 0; i < builds.size(); i++) {
			ComponentBuild oldBuild = builds.get(i);
			ComponentBuild newBuild = newBuilds.get(oldBuild.getName());
			if (newBuild != null) {
				builds.set(i, newBuild);
				newBuilds.remove(oldBuild.getName());
			}
		}

		for (ComponentBuild componentBuild : newBuilds.values()) {
			builds.add(componentBuild);
		}
	}

	private HashMap<String, ComponentBuild> toMap(ArrayList<ComponentBuild> componentBuilds) {
		HashMap<String, ComponentBuild> hashMap = new HashMap<String, ComponentBuild>();
		for (ComponentBuild componentBuild : componentBuilds) {
			hashMap.put(componentBuild.getName(), componentBuild);
		}
		return hashMap;
	}
}
