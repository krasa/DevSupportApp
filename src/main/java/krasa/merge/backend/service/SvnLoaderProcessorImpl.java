package krasa.merge.backend.service;

import java.util.List;
import java.util.Set;

import krasa.merge.backend.domain.SvnFolder;
import krasa.merge.backend.svn.SVNConnector;
import krasa.merge.backend.svn.SvnFolderProvider;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.tmatesoft.svn.core.SVNDirEntry;

/**
 * @author Vojtech Krasa
 */
@Component
@Transactional
public class SvnLoaderProcessorImpl implements SvnLoaderProcessor {

	@Autowired
	private SVNConnector svnConnection;

	@Autowired
	private SvnFolderService folderService;

	public void refreshProjectBranches(SvnFolder project, boolean force) {
		if (force) {
			for (SvnFolder svnFolder : project.getChilds()) {
				folderService.delete(svnFolder);
			}
		}

		SvnFolderProvider provider = new SvnFolderProvider(svnConnection.getBaseRepositoryConnection());

		List<SvnFolder> branches = provider.getBranchesByProjectName(project.getName());
		for (SvnFolder branch : branches) {
			Set<String> branchNamesAsSet = project.getBranchNamesAsSet();
			if (!branchNamesAsSet.contains(branch.getName())) {
				project.add(branch);
				folderService.save(branch);
				List<SvnFolder> branchSubFolders = provider.getBranchSubFolders(branch);
				saveSubFolders(branch, branchSubFolders);
			}
		}
		folderService.save(project);
	}

	private void saveSubFolders(SvnFolder branch1, List<SvnFolder> projectSubFolder) {
		for (SvnFolder subFolder : projectSubFolder) {
			branch1.add(subFolder);
			folderService.save(subFolder);
		}
	}

	public void refreshProjects(boolean force) {
		SvnFolderProvider svnFolderProvider = new SvnFolderProvider(svnConnection.getBaseRepositoryConnection());
		if (force)
			folderService.deleteAll();

		List<SVNDirEntry> projects = svnFolderProvider.getProjects();
		for (SVNDirEntry project : projects) {
			if (folderService.findProjectByName(project.getName()) == null) {
				folderService.saveProjects(projects);
			}
		}
	}

	public void refreshAllBranches() {
		for (SvnFolder svnFolder : folderService.findAllProjects()) {
			refreshProjectBranches(svnFolder, false);
		}
	}

	public void refreshBranchesByProjectName(String name) {
		SvnFolder project = folderService.findProjectByName(name);
		refreshProjectBranches(project, false);
	}
}
