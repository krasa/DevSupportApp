package krasa.merge.backend.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import krasa.core.backend.dao.GenericDAO;
import krasa.core.backend.dao.GenericDaoBuilder;
import krasa.core.backend.service.GlobalSettingsProvider;
import krasa.merge.backend.SvnException;
import krasa.merge.backend.dao.SvnFolderDAO;
import krasa.merge.backend.domain.Repository;
import krasa.merge.backend.domain.SvnFolder;
import krasa.merge.backend.service.conventions.ConventionsStrategyHolder;
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
public class SvnFolderRefreshService {

	@Autowired
	private SvnFolderDAO svnFolderDAO;
	private GenericDAO<Repository> repositoryGenericDAO;

	@Autowired
	private GlobalSettingsProvider globalSettingsProvider;

	public SvnFolderRefreshService() {
	}

	@Autowired
	public SvnFolderRefreshService(GenericDaoBuilder genericDaoBuilder) {
		repositoryGenericDAO = genericDaoBuilder.build(Repository.class);
	}

	public void reloadProjects() {
		List<Repository> all = repositoryGenericDAO.findAll();
		Set<String> projectSet = new HashSet<>();
		for (Repository repository : all) {
			SvnFolderProvider svnFolderProvider = new SvnFolderProvider(repository);
			List<SVNDirEntry> projects = svnFolderProvider.getProjects();
			for (SVNDirEntry project : projects) {
				projectSet.add(project.getName());
				if (svnFolderDAO.findProjectByName(project.getName()) == null) {
					svnFolderDAO.saveProject(project, repository);
				}
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

	public void refreshAllProjects() {
		for (SvnFolder svnFolder : svnFolderDAO.findAllProjects()) {
			refreshProject(svnFolder, true);
		}
	}

	public void refreshProjectByName(String name) {
		SvnFolder project = svnFolderDAO.findProjectByName(name);
		refreshProject(project, false);
	}

	private void refreshProject(SvnFolder project, boolean deleteProjectIfNotExistsInRepo) {
		Repository repository = project.getRepository();
		if (repository == null) {
			repository = globalSettingsProvider.getGlobalSettings().getDefaultRepository();
		}
		SvnFolderProvider provider = new SvnFolderProvider(repository);
		try {
			Boolean loadTags = globalSettingsProvider.getGlobalSettings().isLoadTags(project.getPath());
			List<SvnFolder> childs = provider.getProjectContent(project.getName(), loadTags);
			Set<String> childSet = new HashSet<>();
			for (SvnFolder child : childs) {
				childSet.add(child.getName());
				if (!project.childAlreadyExists(child)) {
					project.add(child);
					svnFolderDAO.save(child);
					List<SvnFolder> branchSubFolders = provider.getSubFolders(child);
					saveSubFolders(child, branchSubFolders);
				}
			}
			deleteNotExisting(project, childSet);
			postProcessAfterRefresh(project);
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

	private void postProcessAfterRefresh(SvnFolder project) {
		List<SvnFolder> svnFolders = ConventionsStrategyHolder.getStrategy().postProcessAllBranches(
				project.getChildsAsMapByName());
		svnFolderDAO.save(project);
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
