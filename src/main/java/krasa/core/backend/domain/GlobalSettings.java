package krasa.core.backend.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import krasa.merge.backend.domain.Repository;

import com.sun.istack.internal.Nullable;

/**
 * @author Vojtech Krasa
 */
@Entity
public class GlobalSettings extends AbstractEntity {
	@ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
	private Set<String> projectsWithSubfoldersMergeSearching = new HashSet<>();

	@ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
	private Set<String> projectsWithLoadTags = new HashSet<>();
	@ElementCollection(targetClass = String.class, fetch = FetchType.EAGER)
	@ManyToOne
	private Repository defaultRepository;

	@Nullable
	public Repository getDefaultRepository() {
		return defaultRepository;
	}

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

	public void setDefaultRepository(Repository defaultRepository) {
		this.defaultRepository = defaultRepository;
	}
}
