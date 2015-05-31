package krasa.svn.backend.dto;

import java.io.Serializable;
import java.util.List;

import krasa.svn.backend.domain.*;

import org.tmatesoft.svn.core.SVNLogEntry;

/**
 * @author Vojtech Krasa
 */
public class MergeInfoResultItem implements Serializable {

	private String from;
	private String to;
	private List<SVNLogEntry> merges;
	private String fromPath;
	private String toPath;
	private String repository;

	public MergeInfoResultItem(SvnFolder to, SvnFolder from, Repository repository, List<SVNLogEntry> merges) {
		this.repository = repository.getUrl();
		this.from = from.getName();
		this.to = to.getName();
		this.merges = merges;
		fromPath = from.getPath();
		toPath = to.getPath();
	}

	public MergeInfoResultItem(MergeInfoResultItem from) {
		this.repository = from.repository;
		this.from = from.from;
		this.to = from.to;
		this.merges = from.merges;
		this.fromPath = from.fromPath;
		this.toPath = from.toPath;
	}

	public MergeInfoResultItem(SvnFolder to, SvnFolder from, Repository repository, String commonFolder,
			List<SVNLogEntry> merges) {
		this.repository = repository.getUrl();
		this.from = from.getName() + " " + commonFolder;
		this.to = to.getName() + " " + commonFolder;
		this.merges = merges;
		fromPath = from.getPath();
		toPath = to.getPath();
	}

	public MergeInfoResultItem(List<SVNLogEntry> allCommits) {
		merges = allCommits;
	}

	public String getFrom() {
		return from;
	}

	public String getTo() {
		return to;
	}

	public String getFromPath() {
		return fromPath;
	}

	public String getToPath() {
		return toPath;
	}

	public List<SVNLogEntry> getMerges() {
		return merges;
	}

	public void setMerges(List<SVNLogEntry> merges) {
		this.merges = merges;
	}

	public boolean isMergeable() {
		return repository != null;
	}

	public String getRepository() {
		return repository;
	}
}
