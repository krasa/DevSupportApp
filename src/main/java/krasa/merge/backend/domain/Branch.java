package krasa.merge.backend.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;

import krasa.core.backend.domain.AbstractEntity;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * @author Vojtech Krasa
 */
@Entity
public class Branch extends AbstractEntity implements Serializable, Comparable {

	@Column
	private String name;
	@ElementCollection(targetClass = String.class)
	private List<String> includedFolders;

	@Column
	private Boolean checkOlderBranches;

	public Branch() {
	}

	public Branch(String[] s) {
		name = s[0].trim();
		if (s.length > 1) {
			includedFolders = new ArrayList<String>();
		} else {
			includedFolders = Collections.emptyList();
		}
		for (int i = 1; i < s.length; i++) {
			includedFolders.add(s[i].trim());
		}
	}

	public Boolean isCheckOlderBranches() {
		return checkOlderBranches;
	}

	public void setCheckOlderBranches(Boolean checkOlderBranches) {
		if (checkOlderBranches == null) {
			checkOlderBranches = false;
		}
		this.checkOlderBranches = checkOlderBranches;
	}

	public Branch(String name) {

		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getIncludedFolders() {
		return includedFolders;
	}

	public void setIncludedFolders(List<String> includedFolders) {
		this.includedFolders = includedFolders;
	}

	@Override
	public int compareTo(Object o) {
		return this.name.compareTo(((Branch) o).getName());
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

}