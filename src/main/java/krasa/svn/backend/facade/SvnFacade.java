package krasa.svn.backend.facade;

import java.util.*;

import krasa.core.backend.config.MainConfig;
import krasa.core.backend.dao.*;
import krasa.core.backend.domain.GlobalSettings;
import krasa.core.backend.service.GlobalSettingsProvider;
import krasa.core.frontend.MySession;
import krasa.svn.backend.dao.*;
import krasa.svn.backend.domain.*;
import krasa.svn.backend.dto.*;
import krasa.svn.backend.service.*;
import krasa.svn.backend.service.conventions.ConventionsStrategyHolder;

import org.hibernate.SessionFactory;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Vojtech Krasa
 */
@Service
@Transactional(value = MainConfig.HSQLDB_TX_MANAGER)
public class SvnFacade {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private GlobalSettingsProvider globalSettingsProvider;
	private GenericDAO<GlobalSettings> globalSettingsDAO;
	@Autowired
	private SvnFolderDAO svnFolderDAO;
	@Autowired
	private MergeInfoService mergeInfoService;
	@Autowired
	private ProfileProvider profileProvider;
	@Autowired
	private ProfileDAO profileDAO;
	private GenericDAO<Branch> branchDAO;
	private GenericDAO<Repository> repositoryGenericDAO;
	@Autowired
	private ReportService reportService;
	@Autowired
	private SvnReleaseProvider svnReleaseProvider;
	private GenericDaoBuilder genericDAO;
	@Autowired
	@Qualifier("sessionFactory")
	protected SessionFactory sf;

	public SvnFacade() {

	}

	@Autowired
	public void setGenericDAO(GenericDaoBuilder genericDAO) {
		this.genericDAO = genericDAO;
		this.branchDAO = genericDAO.build(Branch.class);
		this.globalSettingsDAO = genericDAO.build(GlobalSettings.class);
		this.repositoryGenericDAO = genericDAO.build(Repository.class);
	}

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER, readOnly = true)
	public List<SvnFolder> getSubDirs(String name) {
		return svnFolderDAO.getSubDirsByParentPath(name);
	}

	public void updateProfile(Profile modelObject) {
		Profile byId = profileDAO.findById(modelObject.getId());
		byId.setName(modelObject.getName());
		profileDAO.save(byId);
	}

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER, readOnly = true)
	public List<String> getSelectedBranchesNames() {
		return profileProvider.getSelectedBranchesNames();
	}

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER, readOnly = true)
	public List<SvnFolder> getSelectedBranches() {
		List<Branch> selectedBranches = profileProvider.getSelectedBranches();
		return svnFolderDAO.findBranchesByNames(selectedBranches);
	}

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER, readOnly = true)
	public SvnFolder getSvnFolderById(Integer id) {
		return svnFolderDAO.findById(id);
	}

	public void updateSelectionOfSvnFolder(SvnFolder object, Boolean aBoolean) {
		profileProvider.updateSelectionOfSvnFolder(object, aBoolean);
	}

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER, readOnly = true)
	public List<SvnFolder> findBranchesByNameLike(String name) {
		return svnFolderDAO.findFoldersByNameLike(name, Type.BRANCH);
	}

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER, readOnly = true)
	public List<Displayable> findBranchesByNameLikeAsDisplayable(String name) {
		return new ArrayList<Displayable>(findBranchesByNameLike(name));
	}

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER, readOnly = true)
	public SvnFolder findBranchByName(String name) {
		return svnFolderDAO.findBranchByName(name);
	}

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER, readOnly = true)
	public List<Displayable> findTagsByNameLikeAsDisplayable(String input) {
		return new ArrayList<Displayable>(svnFolderDAO.findFoldersByNameLike(input, Type.TAG));
	}

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER, readOnly = true)
	public SvnFolder findBranchByInCaseSensitiveName(String name) {
		return svnFolderDAO.findBranchByInCaseSensitiveName(name);

	}

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER, readOnly = true)
	public MergeInfoResult getMergeInfoForAllSelectedBranches() {
		List<Branch> selectedBranches = profileProvider.getSelectedBranches();
		List<SvnFolder> branchesByNames = svnFolderDAO.findBranchesByNames(selectedBranches);
		return mergeInfoService.findMerges(branchesByNames);
	}

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER, readOnly = true)
	public MergeInfoResult getMergeInfoForAllSelectedBranchesInProject(String projectPath) {
		List<Branch> selectedBranches = profileProvider.getSelectedBranches();
		List<SvnFolder> branchesByNames = svnFolderDAO.findFoldersByNames(projectPath, selectedBranches);
		return mergeInfoService.findMerges(branchesByNames);
	}

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER, readOnly = true)
	public Profile getProfileByIdOrDefault(Integer current) {
		Profile byId = profileDAO.findById(current);
		if (byId == null) {
			return profileProvider.getFirstProfile();
		}
		return byId;
	}

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER, readOnly = true)
	public Profile getCurrentProfile() {
		return profileDAO.findById(MySession.get().getCurrentProfileId());
	}

	public Profile createNewProfile() {
		return profileDAO.save(new Profile());
	}

	public Profile copyProfile(Profile configModel) {
		Profile object = new Profile(configModel);
		return profileDAO.save(object);
	}

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER, readOnly = true)
	public Boolean isMergeOnSubFoldersForProject(String path) {
		GlobalSettings first = globalSettingsProvider.getGlobalSettings();
		return first.isMergeOnSubFoldersForProject(path);
	}

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER, readOnly = true)
	public GlobalSettings getGlobalSettings() {
		return globalSettingsProvider.getGlobalSettings();
	}

	public void updateBranch(SvnFolder folder) {
		svnFolderDAO.save(folder);
	}

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER, readOnly = true)
	public List<SvnFolder> getAllBranchesByProjectName(String name) {
		return svnFolderDAO.findByParentName(name, Type.BRANCH);
	}

	public void addBranchIntoProfile(String objectAsString) {
		SvnFolder branchByInCaseSensitiveName = findBranchByInCaseSensitiveName(objectAsString);
		if (branchByInCaseSensitiveName != null) {
			profileProvider.addSelectedBranch(branchByInCaseSensitiveName.getName());
		}
	}

	public void addAllMatchingBranchesIntoProfile(String fieldValue) {
		List<SvnFolder> branches = findBranchesByNameLike(fieldValue);
		profileProvider.addSelectedBranches(branches);
	}

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER, readOnly = true)
	public String resolveProjectByPath(String path) {
		int endIndex = path.indexOf("/");
		if (endIndex > 0) {
			path = path.substring(0, endIndex);
		}
		SvnFolder branchByName = svnFolderDAO.findBranchByName(path);
		if (branchByName != null) {
			path = branchByName.getParent().getName();
		}
		return path;
	}

	public void setLoadTagsForProject(String path, Boolean modelObject) {
		globalSettingsProvider.getGlobalSettings().setLoadTagsForProject(path, modelObject);
	}

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER, readOnly = true)
	public Boolean isLoadTags(String path) {
		return globalSettingsProvider.getGlobalSettings().isLoadTags(path);
	}

	public void saveRepository(Repository modelObject) {
		repositoryGenericDAO.save(modelObject);
		GlobalSettings globalSettings = getGlobalSettings();
		if (globalSettings.getDefaultRepository() == null) {
			globalSettings.setDefaultRepository(modelObject);
			globalSettingsDAO.save(globalSettings);
		}
	}

	public List<Repository> getAllRepositories() {
		return repositoryGenericDAO.findAll();
	}

	public void deleteRepository(Integer id) {
		Repository byId = repositoryGenericDAO.findById(id);
		GlobalSettings globalSettings = getGlobalSettings();
		Repository defaultRepository = globalSettings.getDefaultRepository();
		if (defaultRepository != null && defaultRepository.equals(byId)) {
			globalSettings.setDefaultRepository(null);
			globalSettingsDAO.save(globalSettings);
		}
		svnFolderDAO.deleteAllBy(byId);
		repositoryGenericDAO.delete(byId);
	}

	public void deleteAllSvnBranches() {
		svnFolderDAO.deleteAll();
	}

	public void deleteAllBranchesFromProfile() {
		profileProvider.deleteAllBranchesFromProfile();
	}

	public void replaceSearchFrom() {
		List<SvnFolder> selectedBranches = getSelectedBranches();
		for (SvnFolder selectedBranch : selectedBranches) {
			ConventionsStrategyHolder.getStrategy().replaceSearchFrom(selectedBranch);
			svnFolderDAO.save(selectedBranch);
		}
	}

	public void replaceSearchFromToTrunk() {
		List<SvnFolder> selectedBranches = getSelectedBranches();
		for (SvnFolder selectedBranch : selectedBranches) {
			ConventionsStrategyHolder.getStrategy().replaceSearchFromToTrunk(selectedBranch);
			svnFolderDAO.save(selectedBranch);
		}
	}

	public void deleteProfile(Profile modelObject) {
		profileDAO.delete(modelObject);
	}

	public void setMergeOnSubFoldersForProject(String path, Boolean modelObject) {
		GlobalSettings settings = globalSettingsProvider.getGlobalSettings();
		settings.setProjectsWithSubfoldersMergeSearching(path, modelObject);
		globalSettingsDAO.save(settings);
	}

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER, readOnly = true)
	public ReportResult getReport() {
		return reportService.getReport(getDefaultRepository());
	}

	private Repository getDefaultRepository() {
		Repository defaultRepository = globalSettingsProvider.getGlobalSettings().getDefaultRepository();
		if (defaultRepository == null) {
			defaultRepository = repositoryGenericDAO.findFirst();
		}
		if (defaultRepository == null) {
			throw new IllegalStateException("no default repository exists");
		}
		return defaultRepository;
	}

	public List<Profile> getReleasesFromSvn() {
		return profileDAO.findAllByType(Profile.Type.FROM_SVN);
	}

	public void refreshReleasesFromSvn() {
		deleteAllReleasesFromSvn();
		List<Profile> releases = svnReleaseProvider.getReleases();
		for (Profile release : releases) {
			List<Branch> branches = release.getBranches();
			Collections.sort(branches);
			profileDAO.save(release);
		}
	}

	private void deleteAllReleasesFromSvn() {
		List<Profile> releases = getReleasesFromSvn();

		for (Profile release : releases) {
			profileDAO.delete(release);
		}
	}

	public void delete(Profile modelObject) {
		profileDAO.delete(modelObject);
		MySession.get().setCurrentProfile(profileProvider.getFirstProfile().getId());
	}

	public void updateGlobalSettings(GlobalSettings globalSettings) {
		globalSettingsDAO.save(globalSettings);
	}

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER, readOnly = true)
	public List<SvnFolder> getProjects() {
		return svnFolderDAO.findAllProjects();
	}

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER, readOnly = true)
	public List<Profile> getProfiles() {
		return profileDAO.findAll();
	}

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER, readOnly = false)
	public Profile getDefaultProfile() {
		return profileProvider.getFirstProfile();
	}

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER, readOnly = true)
	public List<SvnFolder> getBranches() {
		return svnFolderDAO.findAll();
	}

}
