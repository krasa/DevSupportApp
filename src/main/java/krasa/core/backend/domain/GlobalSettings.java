package krasa.core.backend.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;

/**
 * @author Vojtech Krasa
 */
@Entity
public class GlobalSettings extends AbstractEntity {
	@ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
	private Set<String> projectsWithSubfoldersMergeSearching = new HashSet<String>();

	@ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
	private Set<String> branchesForFindingOlderMerges = new HashSet<String>();

	public Set<String> getProjectsWithSubfoldersMergeSearching() {
		return projectsWithSubfoldersMergeSearching;
	}

	public void setProjectsWithSubfoldersMergeSearching(Set<String> projectsWithSubfoldersMergeSearching) {
		this.projectsWithSubfoldersMergeSearching = projectsWithSubfoldersMergeSearching;
	}

	public Boolean isMergeOnSubFoldersForProject(String path) {
		return projectsWithSubfoldersMergeSearching.contains(path.toLowerCase());
	}

	public Set<String> getBranchesForFindingOlderMerges() {
		return branchesForFindingOlderMerges;
	}

	public boolean isBranchesForFindingOlderMerges(String path) {
		return branchesForFindingOlderMerges.contains(path.toLowerCase());
	}

	public void setBranchesForFindingOlderMerges(Set<String> branchesForFindingOlderMerges) {
		this.branchesForFindingOlderMerges = branchesForFindingOlderMerges;
	}

	public void addMergeOnSubFoldersForProject(String path) {
		projectsWithSubfoldersMergeSearching.add(path.toLowerCase());
	}

	public void setProjectsWithSubfoldersMergeSearching(String path, Boolean modelObject) {
		if (modelObject) {
			projectsWithSubfoldersMergeSearching.add(path.toLowerCase());
		} else {
			projectsWithSubfoldersMergeSearching.remove(path.toLowerCase());
		}
	}

	public void setBranchesForFindingOlderMerges(String path, Boolean object) {
		if (object) {
			branchesForFindingOlderMerges.add(path.toLowerCase());
		} else {
			branchesForFindingOlderMerges.remove(path.toLowerCase());
		}
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
