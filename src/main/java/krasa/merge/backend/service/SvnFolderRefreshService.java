package krasa.merge.backend.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import krasa.merge.backend.SvnException;
import krasa.merge.backend.dao.SvnFolderDAO;
import krasa.merge.backend.domain.SvnFolder;
import krasa.merge.backend.svn.SvnFolderProvider;
import krasa.merge.backend.svn.connection.SVNConnector;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.tmatesoft.svn.core.SVNDirEntry;

/**
 * @author Vojtech Krasa
 */
@Component
@Transactional
public class SvnFolderRefreshService {

	@Autowired
	private SVNConnector svnConnection;

	@Autowired
	private SvnFolderDAO svnFolderDAO;

	public void reloadProjects() {
		SvnFolderProvider svnFolderProvider = new SvnFolderProvider(svnConnection.getBaseRepositoryConnection());
		List<SVNDirEntry> projects = svnFolderProvider.getProjects();
		Set<String> projectSet = new HashSet<String>();
		for (SVNDirEntry project : projects) {
			projectSet.add(project.getName());
			if (svnFolderDAO.findProjectByName(project.getName()) == null) {
				svnFolderDAO.saveProjects(projects);
			}
		}

		deleteProjectsNotInRepo(projectSet);
	}

	private void deleteProjectsNotInRepo(Set<String> projectSet) {
		List<SvnFolder> allProjects = svnFolderDAO.findAllProjects();
		for (SvnFolder project : allProjects) {
			if (!projectSet.contains(project.getName())) {
				svnFolderDAO.delete(project);
			}
		}
	}

	public void refreshAllBranches() {
		for (SvnFolder svnFolder : svnFolderDAO.findAllProjects()) {
			refreshProjectBranches(svnFolder, true);
		}
	}

	public void refreshBranchesByProjectName(String name) {
		SvnFolder project = svnFolderDAO.findProjectByName(name);
		refreshProjectBranches(project, false);
	}

	private void refreshProjectBranches(SvnFolder project, boolean deleteProjectIfNotExistsInRepo) {
		SvnFolderProvider provider = new SvnFolderProvider(svnConnection.getBaseRepositoryConnection());
		try {
			List<SvnFolder> branches = provider.getBranchesAndTrunkByProjectName(project.getName());
			Set<String> branchesSet = new HashSet<String>();
			for (SvnFolder branch : branches) {
				branchesSet.add(branch.getName());
				if (!project.branchAlreadyExists(branch)) {
					project.add(branch);
					svnFolderDAO.save(branch);
					List<SvnFolder> branchSubFolders = provider.getSubFolders(branch);
					saveSubFolders(branch, branchSubFolders);
				}
			}
			deleteNotExisting(project, branchesSet);
			svnFolderDAO.save(project);
		} catch (SvnException e) {
			if ("160013: Filesystem has no item".equals(e.getErrorCode().toString())) {
				if (deleteProjectIfNotExistsInRepo) {
					svnFolderDAO.delete(project);
					return;
				} else {
					throw e;
				}
			}
		}
	}

	private void deleteNotExisting(SvnFolder project, Set<String> branchesSet) {
		for (SvnFolder svnFolder : project.getChilds()) {
			if (!branchesSet.contains(svnFolder.getName())) {
				svnFolderDAO.delete(svnFolder);
			}
		}
	}

	private void saveSubFolders(SvnFolder branch1, List<SvnFolder> projectSubFolder) {
		for (SvnFolder subFolder : projectSubFolder) {
			branch1.add(subFolder);
			svnFolderDAO.save(subFolder);
		}
	}
}
