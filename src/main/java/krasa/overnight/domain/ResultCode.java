package krasa.overnight.domain;

import javax.persistence.*;

import krasa.core.backend.domain.AbstractEntity;

@Entity(name = "resultcodes")
public class ResultCode extends AbstractEntity {

	@Column(name = "value", columnDefinition = "CHAR(255)")
	String value;
	@Column(name = "color", columnDefinition = "CHAR(255)")
	String color;

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}
}
