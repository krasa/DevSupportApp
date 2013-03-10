package krasa.merge.backend.service;

import java.util.List;

import krasa.merge.backend.domain.SvnFolder;

import org.tmatesoft.svn.core.SVNDirEntry;

/**
 * @author Vojtech Krasa
 */
public interface SvnFolderService {
	void deleteAll();

	void save(SvnFolder project);

	void saveProjects(List<SVNDirEntry> projects);

	void delete(SvnFolder svnFolder);

	List<SvnFolder> findAllProjects();

	SvnFolder findProjectByName(String name);
}
