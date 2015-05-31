package krasa.svn.backend.domain;

import java.io.Serializable;
import java.util.*;

import javax.persistence.*;

import krasa.core.backend.domain.AbstractEntity;

/**
 * @author Vojtech Krasa
 */
@Entity
public class Branch extends AbstractEntity implements Serializable, Comparable, Displayable {

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
			includedFolders = new ArrayList<>();
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
	public String getDisplayableText() {
		return getName();
	}
}
