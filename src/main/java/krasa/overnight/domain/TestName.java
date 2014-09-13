package krasa.overnight.domain;

import javax.persistence.*;

import krasa.core.backend.domain.AbstractEntity;

@Entity(name = "testnames")
public class TestName extends AbstractEntity {

	@ManyToOne
	@JoinColumn(name = "id_component")
	Component component;
	@Column(name = "name", columnDefinition = "CHAR(255)")
	String name;

	public Component getComponent() {
		return component;
	}

	public void setComponent(Component component) {
		this.component = component;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
