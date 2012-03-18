package krasa.backend.service;

import krasa.backend.domain.SvnFolder;
import org.tmatesoft.svn.core.SVNDirEntry;

import java.util.List;

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
