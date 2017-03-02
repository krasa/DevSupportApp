package krasa.svn.backend.domain;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;

import krasa.core.backend.domain.AbstractEntity;

/**
 * @author Vojtech Krasa
 */
@Entity
public class Repository extends AbstractEntity<Repository> implements Serializable {
	@Column
	private String url;
	@Column
	private boolean indexTrunk;
	@OneToMany(mappedBy = "repository", orphanRemoval = true)
	@Cascade(org.hibernate.annotations.CascadeType.DELETE)
	private List<SvnFolder> folders;

	private RepositoryStructure repositoryStructure = RepositoryStructure.TRUNK_IN_PROJECTS;

	public RepositoryStructure getRepositoryStructure() {
		return repositoryStructure;
	}

	public String getRepositoryStructureAsString() {
		if (repositoryStructure == null) {
			return null;
		}
		return repositoryStructure.name();
	}

	public void setRepositoryStructure(RepositoryStructure repositoryStructure) {
		this.repositoryStructure = repositoryStructure;
	}

	public void setRepositoryStructureAsString(String repositoryStructure) {
		this.repositoryStructure = RepositoryStructure.valueOf(repositoryStructure);
	}

	public Repository(String url) {
		this.url = url;
	}

	public Repository() {
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isIndexTrunk() {
		return indexTrunk;
	}

	public void setIndexTrunk(boolean indexTrunk) {
		this.indexTrunk = indexTrunk;
	}

	public List<SvnFolder> getFolders() {
		return folders;
	}

	public void setFolders(List<SvnFolder> folders) {
		this.folders = folders;
	}
}
