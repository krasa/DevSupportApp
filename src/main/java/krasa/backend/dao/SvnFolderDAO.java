package krasa.backend.dao;

import krasa.backend.domain.SvnFolder;
import org.tmatesoft.svn.core.SVNDirEntry;

import java.util.List;
import java.util.Set;

/**
 * @author Vojtech Krasa
 */
public interface SvnFolderDAO extends DAO<SvnFolder> {
    void replaceProjects(List<SVNDirEntry> svnDirEntries);

    void replaceBranches(List<SVNDirEntry> svnDirEntries);

    List<SvnFolder> getSubDirsByParentPath(String name);

    SvnFolder findByPath(String path);

    List<SvnFolder> findAllProjects();

    List<SvnFolder> findBranchesByNames(Set<String> selectedBranches);

    List<SvnFolder> findBranchesByNameLike(String name);

    SvnFolder findBranchByInCaseSensitiveName(String name);

    List<SvnFolder> findBranchesByNames(String parent, Set<String> selectedBranches);

    SvnFolder findProjectByName(String name);

    List<SvnFolder> findBranchesByNameLike(String input, String parentName);
}
