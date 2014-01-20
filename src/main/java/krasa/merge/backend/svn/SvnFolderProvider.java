package krasa.merge.backend.svn;

import java.util.List;

import krasa.merge.backend.domain.SvnFolder;

import org.tmatesoft.svn.core.SVNDirEntry;

/**
 * @author Vojtech Krasa
 */
public interface SvnFolderProvider {
	List<SVNDirEntry> getProjects();

	List<SvnFolder> getProjectContent(SvnFolder project, Boolean loadTags);

	List<SVNDirEntry> getTags(SvnFolder branchOrProject);

	List<SvnFolder> getSubFolders(SvnFolder branchOrProject);
}
