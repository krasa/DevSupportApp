package krasa.merge.backend.service;

import java.util.List;

import krasa.merge.backend.dao.SvnFolderDAO;
import krasa.merge.backend.domain.SvnFolder;
import krasa.merge.backend.domain.Type;
import krasa.merge.backend.svn.connection.SVNConnector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tmatesoft.svn.core.SVNDirEntry;

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

	@Override
	public void deleteAll() {
		svnFolderDAO.deleteAll();
	}

	@Override
	public void save(SvnFolder project) {
		svnFolderDAO.save(project);
	}

	@Override
	public void saveProjects(List<SVNDirEntry> projects) {
		for (SVNDirEntry project : projects) {
			saveProject(project);
		}
	}

	@Override
	public void delete(SvnFolder svnFolder) {
		svnFolderDAO.delete(svnFolder);
	}

	@Override
	public List<SvnFolder> findAllProjects() {
		return svnFolderDAO.findAllProjects();
	}

	@Override
	public SvnFolder findProjectByName(String name) {
		try {
			return svnFolderDAO.findProjectByName(name);
		} catch (Exception e) {
			throw new RuntimeException(name, e);
		}
	}

	private void saveProject(SVNDirEntry entry) {
		// path=name
		SvnFolder project = new SvnFolder(entry, entry.getName());
		project.setType(Type.PROJECT);
		save(project);
	}

}
