package krasa.merge.backend.service;

import java.util.*;

import krasa.core.backend.config.MainConfig;
import krasa.core.backend.dao.*;
import krasa.core.backend.service.GlobalSettingsProvider;
import krasa.merge.backend.SvnException;
import krasa.merge.backend.dao.SvnFolderDAO;
import krasa.merge.backend.domain.*;
import krasa.merge.backend.service.conventions.ConventionsStrategyHolder;
import krasa.merge.backend.svn.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.tmatesoft.svn.core.SVNDirEntry;

/**
 * @author Vojtech Krasa
 */
@Component
@Transactional(value = MainConfig.HSQLDB_TX_MANAGER)
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
			SvnFolderProvider svnFolderProvider = getSvnFolderProvider(repository);
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
		SvnFolderProvider provider = getSvnFolderProvider(repository);
		try {
			Boolean loadTags = globalSettingsProvider.getGlobalSettings().isLoadTags(project.getPath());
			List<SvnFolder> childs = provider.getProjectContent(project, loadTags);
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

	private SvnFolderProvider getSvnFolderProvider(Repository repository) {
		return new SvnFolderProviderImpl(repository);
	}

	private void postProcessAfterRefresh(SvnFolder project) {
		ConventionsStrategyHolder.getStrategy().postProcessAllBranches(project.getChildsAsMapByName());
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
