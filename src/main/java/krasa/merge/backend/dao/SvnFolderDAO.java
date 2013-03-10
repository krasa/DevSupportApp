package krasa.merge.backend.dao;

import java.util.List;

import krasa.core.backend.dao.DAO;
import krasa.merge.backend.domain.Branch;
import krasa.merge.backend.domain.SvnFolder;

import org.tmatesoft.svn.core.SVNDirEntry;

/**
 * @author Vojtech Krasa
 */
public interface SvnFolderDAO extends DAO<SvnFolder> {
	void replaceProjects(List<SVNDirEntry> svnDirEntries);

	void replaceBranches(List<SVNDirEntry> svnDirEntries);

	List<SvnFolder> getSubDirsByParentPath(String name);

	SvnFolder findByPath(String path);

	List<SvnFolder> findAllProjects();

	List<SvnFolder> findBranchesByNames(List<Branch> selectedBranches);

	List<SvnFolder> findBranchesByNameLike(String name);

	SvnFolder findBranchByInCaseSensitiveName(String name);

	List<SvnFolder> findBranchesByNames(String parent, List<Branch> selectedBranches);

	SvnFolder findProjectByName(String name);

	List<String> findBranchesByNameLike(String parentName, String input);

	SvnFolder findBranchByName(String name2);

	List<SvnFolder> findBranchesByNamePrefix(String branchNameForMatch);
}
