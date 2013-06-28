package krasa.merge.backend.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import krasa.merge.backend.domain.SvnFolder;

import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNLogEntry;

/**
 * @author Vojtech Krasa
 */
public class ReportResult implements Serializable {
	Map<String, List<SVNLogEntry>> svnFolderListMap = new HashMap<>();
	Map<String, List<SVNDirEntry>> tags = new HashMap<>();

	public void add(SvnFolder branchesByName, List<SVNLogEntry> svnLogEntries) {
		svnFolderListMap.put(branchesByName.getName(), svnLogEntries);
	}

	public Map<String, List<SVNLogEntry>> getSvnFolderListMap() {
		return svnFolderListMap;
	}

	public List<SVNLogEntry> getRevisionsByBranchName(String branchName) {
		return svnFolderListMap.get(branchName);
	}

	public List<SVNDirEntry> getTagsByBranchaName(String branchName) {
		return tags.get(branchName);
	}

	public void addTags(SvnFolder branchesByName, List<SVNDirEntry> tags) {
		this.tags.put(branchesByName.getName(), tags);
	}

	public List<SVNLogEntry> getDifferenceBetweenTags(SVNDirEntry tag, SVNDirEntry tag2, String branchName) {
		ArrayList<SVNLogEntry> result = new ArrayList<>();
		List<SVNLogEntry> revisionsByBranchName = getRevisionsByBranchName(branchName);
		long revision = tag.getRevision();
		long revision2 = tag2.getRevision();
		for (SVNLogEntry svnLogEntry : revisionsByBranchName) {
			long svnLogEntryRevision = svnLogEntry.getRevision();
			if (revision <= svnLogEntryRevision && svnLogEntryRevision <= revision2) {
				result.add(svnLogEntry);
			}
		}
		return result;
	}

	public List<SVNLogEntry> getDifferenceBetweenTagAndBranch(SVNDirEntry tag, String branchName) {
		ArrayList<SVNLogEntry> result = new ArrayList<>();

		List<SVNLogEntry> revisionsByBranchName = getRevisionsByBranchName(branchName);
		long revision = tag.getRevision();
		for (SVNLogEntry svnLogEntry : revisionsByBranchName) {
			long svnLogEntryRevision = svnLogEntry.getRevision();
			if (revision <= svnLogEntryRevision) {
				result.add(svnLogEntry);
			}
		}
		return result;
	}

	public List<SVNLogEntry> getDifferenceBetweenTagAndBranchAsStrings(SVNDirEntry tag, String branchName) {
		return getDifferenceBetweenTagAndBranch(tag, branchName);
	}

	public List<SVNLogEntry> getIssues(String branchName) {
		List<SVNLogEntry> revisionsByBranchName = getRevisionsByBranchName(branchName);
		return revisionsByBranchName;
	}

	public static int nthOccurrence(String str, String c, int n) {
		int pos = str.indexOf(c, 0);
		while (n-- > 0 && pos != -1)
			pos = str.indexOf(c, pos + 1);
		return pos;
	}

	public List<SVNLogEntry> getIssuesByTag(String branch, SVNDirEntry tag) {
		ArrayList<SVNLogEntry> result = new ArrayList<>();

		List<SVNLogEntry> revisionsByBranchName = getRevisionsByBranchName(branch);
		long revision = tag.getRevision();
		for (SVNLogEntry svnLogEntry : revisionsByBranchName) {
			long svnLogEntryRevision = svnLogEntry.getRevision();
			if (svnLogEntryRevision <= revision) {
				result.add(svnLogEntry);
			}
		}
		return result;
	}

	public List<SVNLogEntry> getIncrementalIssuesByTag(String branchName, SVNDirEntry tag) {
		SVNDirEntry nextTag = getNextTag(branchName, tag);
		List<SVNLogEntry> differenceBetweenTags = getDifferenceBetweenTags(tag, nextTag, branchName);
		return differenceBetweenTags;
	}

	public SVNDirEntry getNextTag(String branchName, SVNDirEntry tag) {
		List<SVNDirEntry> tagsByBranchaName = getTagsByBranchaName(branchName);
		SVNDirEntry nextTag = null;
		for (int i = 0; i < tagsByBranchaName.size(); i++) {
			SVNDirEntry svnDirEntry = tagsByBranchaName.get(i);
			if (svnDirEntry.equals(tag) && tagsByBranchaName.size() > i + 1) {
				nextTag = tagsByBranchaName.get(i + 1);
			}
		}
		return nextTag;
	}

	public long getLastRevisionOfBranch(String branchName) {
		List<SVNLogEntry> revisionsByBranchName = getRevisionsByBranchName(branchName);
		long max = 0;
		for (SVNLogEntry svnLogEntry : revisionsByBranchName) {
			long revision = svnLogEntry.getRevision();
			if (max < revision) {
				max = revision;
			}
		}
		return max;

	}

	public long getFirstRevisionOfBranch(String branchName) {
		List<SVNLogEntry> revisionsByBranchName = getRevisionsByBranchName(branchName);
		long min = 0;
		for (SVNLogEntry svnLogEntry : revisionsByBranchName) {
			long revision = svnLogEntry.getRevision();
			if (min > revision || min == 0) {
				min = revision;
			}
		}
		return min;
	}

	public List<SVNLogEntry> getAllCommits(String branchName) {
		return svnFolderListMap.get(branchName);
	}
}
