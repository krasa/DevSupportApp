package krasa.merge.backend.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;

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

}
