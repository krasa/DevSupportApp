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
	private Set<String> projectsWithLoadTags = new HashSet<String>();

	public Boolean isLoadTags(String path) {
		return projectsWithLoadTags.contains(path.toLowerCase());
	}

	public void setLoadTagsForProject(String path, Boolean modelObject) {
		if (modelObject) {
			projectsWithLoadTags.add(path.toLowerCase());
		} else {
			projectsWithLoadTags.remove(path.toLowerCase());
		}
	}

	public Boolean isMergeOnSubFoldersForProject(String path) {
		return projectsWithSubfoldersMergeSearching.contains(path.toLowerCase());
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
