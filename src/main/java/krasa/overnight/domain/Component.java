package krasa.overnight.domain;

import javax.persistence.*;

import krasa.core.backend.domain.AbstractEntity;

@Entity(name = "components")
public class Component extends AbstractEntity {

	@Column(name = "name", columnDefinition = "CHAR(255)")
	String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
