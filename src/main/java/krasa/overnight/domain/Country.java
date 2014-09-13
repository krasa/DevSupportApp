package krasa.overnight.domain;

import javax.persistence.*;

import krasa.core.backend.domain.AbstractEntity;

@Entity(name = "countries")
public class Country extends AbstractEntity {

	@Column(name = "code", columnDefinition = "CHAR(255)")
	String code;
	@Column(name = "name", columnDefinition = "CHAR(255)")
	String name;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
