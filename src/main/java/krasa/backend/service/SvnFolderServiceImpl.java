package krasa.backend.service;

import krasa.backend.dao.SvnFolderDAO;
import krasa.backend.domain.SvnFolder;
import krasa.backend.domain.Type;
import krasa.backend.svn.SVNConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tmatesoft.svn.core.SVNDirEntry;

import java.util.List;

/**
 * @author Vojtech Krasa
 */
@Transactional
@Service
public class SvnFolderServiceImpl implements SvnFolderService {
    @Autowired
    private SvnFolderDAO svnFolderDAO;

    @Autowired
    private SVNConnector svnConnection;

    public void deleteAll() {
        svnFolderDAO.deleteAll();
    }

    public void save(SvnFolder project) {
        svnFolderDAO.save(project);
    }

    public void saveProjects(List<SVNDirEntry> projects) {
        for (SVNDirEntry project : projects) {
            saveProject(project);
        }
    }

    public void delete(SvnFolder svnFolder) {
        svnFolderDAO.delete(svnFolder);
    }

    public List<SvnFolder> findAllProjects() {
        return svnFolderDAO.findAllProjects();
    }

    public SvnFolder findProjectByName(String name) {
        return svnFolderDAO.findProjectByName(name);
    }

    private void saveProject(SVNDirEntry entry) {
        //path=name
        SvnFolder project = new SvnFolder(entry, entry.getName());
        project.setType(Type.PROJECT);
        save(project);
    }

}
