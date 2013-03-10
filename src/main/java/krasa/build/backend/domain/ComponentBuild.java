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
public class ComponentBuild extends AbstractEntity implements Serializable {
	private String name;
	private Date builded;
	private Status status;
	@ManyToOne(optional = false)
	private Environment environment;

	public Environment getEnvironment() {
		return environment;
	}

	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	public static ComponentBuild inProgress(String name) {
		ComponentBuild componentBuild = new ComponentBuild();
		componentBuild.setBuilded(new Date());
		componentBuild.setName(name);
		componentBuild.setStatus(Status.IN_PROGRESS);
		return componentBuild;
	}

	public static ComponentBuild newComponent(String name) {
		ComponentBuild componentBuild = new ComponentBuild();
		componentBuild.setName(name);
		return componentBuild;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public ComponentBuild() {
	}

	public Date getBuilded() {
		return builded;
	}

	public void setBuilded(Date builded) {
		this.builded = builded;
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

	public static class ComponentBuildComparator implements Comparator<ComponentBuild> {
		@Override
		public int compare(ComponentBuild o1, ComponentBuild o2) {
			return o1.getName().compareTo(o2.getName());
		}
	}
}
