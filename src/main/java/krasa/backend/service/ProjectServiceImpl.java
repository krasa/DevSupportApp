package krasa.backend.service;

import krasa.backend.dao.SvnFolderDAO;
import krasa.backend.domain.SvnFolder;
import krasa.backend.domain.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tmatesoft.svn.core.SVNDirEntry;

import java.util.List;

/**
 * @author Vojtech Krasa
 */

@Service
public class ProjectServiceImpl implements ProjectService {

    @Autowired
    private SvnFolderDAO projectDAO;

    @Transactional
    public void replaceProjects(List<SVNDirEntry> svnDirEntries) {
        projectDAO.replaceProjects(svnDirEntries);
    }

    public void replaceBranches(SvnFolder project, List<SVNDirEntry> branches) {
        project = projectDAO.findById(project.getId());
        for (SvnFolder child : project.getChilds()) {
            if (child.getType() == Type.BRANCH) {
                projectDAO.delete(child);
            }
        }

        for (SVNDirEntry child : branches) {
            SvnFolder branch = new SvnFolder(child, Type.BRANCH);
            project.add(branch);
            projectDAO.save(branch);
        }
    }
}
