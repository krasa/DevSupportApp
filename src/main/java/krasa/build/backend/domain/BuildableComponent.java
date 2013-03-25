package krasa.build.backend.domain;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import krasa.core.backend.domain.AbstractEntity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

@Entity
public class BuildableComponent extends AbstractEntity implements Serializable {
	private String name;
	private Date lastSuccessBuild;
	private Status status;
	@ManyToOne(optional = false)
	private Environment environment;

	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	public static BuildableComponent newComponent(String name) {
		BuildableComponent buildableComponent = new BuildableComponent();
		buildableComponent.setName(name);
		return buildableComponent;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public BuildableComponent() {
	}

	public Date getLastSuccessBuild() {
		return lastSuccessBuild;
	}

	public void setLastSuccessBuild(Date lastSuccessBuild) {
		this.lastSuccessBuild = lastSuccessBuild;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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

	public static class ComponentBuildComparator implements Comparator<BuildableComponent> {
		@Override
		public int compare(BuildableComponent o1, BuildableComponent o2) {
			return o1.getName().compareTo(o2.getName());
		}
	}
}
