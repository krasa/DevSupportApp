package krasa.merge.backend.service;

import java.util.List;

import krasa.merge.backend.dao.SvnFolderDAO;
import krasa.merge.backend.domain.SvnFolder;
import krasa.merge.backend.domain.Type;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tmatesoft.svn.core.SVNDirEntry;

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
