package krasa.merge.backend.dto;

import java.io.Serializable;
import java.util.*;

import org.tmatesoft.svn.core.SVNLogEntry;

public class ReportItem implements Serializable {

	public String message;
	public Collection<SVNLogEntry> commits;

	public ReportItem(String message, Collection<SVNLogEntry> commits) {
		this.message = message;
		this.commits = new ArrayList<>(commits);
	}
}
