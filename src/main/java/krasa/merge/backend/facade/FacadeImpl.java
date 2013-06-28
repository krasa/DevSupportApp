package krasa.merge.backend.facade;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import krasa.build.backend.domain.BuildableComponent;
import krasa.build.backend.domain.Environment;
import krasa.core.backend.dao.GenericDAO;
import krasa.core.backend.dao.GenericDaoBuilder;
import krasa.core.backend.domain.GlobalSettings;
import krasa.core.backend.service.GlobalSettingsProvider;
import krasa.core.frontend.MySession;
import krasa.merge.backend.dao.ProfileDAO;
import krasa.merge.backend.dao.SvnFolderDAO;
import krasa.merge.backend.domain.Branch;
import krasa.merge.backend.domain.Displayable;
import krasa.merge.backend.domain.Profile;
import krasa.merge.backend.domain.Repository;
import krasa.merge.backend.domain.SvnFolder;
import krasa.merge.backend.domain.Type;
import krasa.merge.backend.dto.MergeInfoResult;
import krasa.merge.backend.dto.ReportResult;
import krasa.merge.backend.service.MergeInfoService;
import krasa.merge.backend.service.ProfileProvider;
import krasa.merge.backend.service.ReportService;
import krasa.merge.backend.svn.SvnReleaseProvider;

import org.apache.wicket.util.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Vojtech Krasa
 */
@Service
@Transactional
public class FacadeImpl implements Facade {
	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	private GlobalSettingsProvider globalSettingsProvider;
	@Autowired
	private GenericDAO<GlobalSettings> globalSettingsDAO;
	@Autowired
	private SvnFolderDAO svnFolderDAO;
	@Autowired
	private MergeInfoService mergeInfoService;
	@Autowired
	private ProfileProvider profileProvider;
	@Autowired
	private ProfileDAO profileDAO;
	private GenericDAO<Environment> environmentDAO;
	private GenericDAO<BuildableComponent> branchBuildDAO;
	private GenericDAO<Branch> branchDAO;
	private GenericDAO<Repository> repositoryGenericDAO;
	@Autowired
	private ReportService reportService;
	@Autowired
	private SvnReleaseProvider svnReleaseProvider;
	@Value("${rns.directory}")
	private String rnsDirectory;
	@Value("${rns.command}")
	private String rnsCommand;
	@Value("${SvnHeadVsLastTag.command}")
	private String svnHeadVsLastTagCommand;
	@Value("${VersionsOnPrgens.command}")
	private String versionsOnPrgensCommand;
	private GenericDaoBuilder genericDAO;

	public FacadeImpl() {
	}

	@Autowired
	public void setGenericDAO(GenericDaoBuilder genericDAO) {
		this.genericDAO = genericDAO;
		this.environmentDAO = genericDAO.build(Environment.class);
		this.branchBuildDAO = genericDAO.build(BuildableComponent.class);
		this.branchDAO = genericDAO.build(Branch.class);
		this.globalSettingsDAO = genericDAO.build(GlobalSettings.class);
		this.repositoryGenericDAO = genericDAO.build(Repository.class);
	}

	@Transactional(readOnly = true)
	@Override
	public List<SvnFolder> getSubDirs(String name) {
		return svnFolderDAO.getSubDirsByParentPath(name);
	}

	@Override
	public void updateProfile(Profile modelObject) {
		Profile byId = profileDAO.findById(modelObject.getId());
		byId.setName(modelObject.getName());
		profileDAO.save(byId);
	}

	@Transactional(readOnly = true)
	@Override
	public List<String> getSelectedBranchesNames() {
		return profileProvider.getSelectedBranchesNames();
	}

	@Transactional(readOnly = true)
	@Override
	public List<SvnFolder> getSelectedBranches() {
		List<Branch> selectedBranches = profileProvider.getSelectedBranches();
		return svnFolderDAO.findBranchesByNames(selectedBranches);
	}

	@Transactional(readOnly = true)
	@Override
	public SvnFolder getSvnFolderById(Integer id) {
		return svnFolderDAO.findById(id);
	}

	@Override
	public void updateSelectionOfSvnFolder(SvnFolder object, Boolean aBoolean) {
		profileProvider.updateSelectionOfSvnFolder(object, aBoolean);
	}

	@Transactional(readOnly = true)
	@Override
	public List<SvnFolder> findBranchesByNameLike(String name) {
		return svnFolderDAO.findFoldersByNameLike(name, Type.BRANCH);
	}

	@Transactional(readOnly = true)
	@Override
	public List<Displayable> findBranchesByNameLikeAsDisplayable(String name) {
		return new ArrayList<Displayable>(findBranchesByNameLike(name));
	}

	@Transactional(readOnly = true)
	@Override
	public List<Displayable> findTagsByNameLikeAsDisplayable(String input) {
		return new ArrayList<Displayable>(svnFolderDAO.findFoldersByNameLike(input, Type.TAG));
	}

	@Transactional(readOnly = true)
	@Override
	public SvnFolder findBranchByInCaseSensitiveName(String name) {
		return svnFolderDAO.findBranchByInCaseSensitiveName(name);

	}

	@Transactional(readOnly = true)
	@Override
	public MergeInfoResult getMergeInfoForAllSelectedBranches() {
		List<Branch> selectedBranches = profileProvider.getSelectedBranches();
		List<SvnFolder> branchesByNames = svnFolderDAO.findBranchesByNames(selectedBranches);
		return mergeInfoService.findMerges(branchesByNames);
	}

	@Transactional(readOnly = true)
	@Override
	public MergeInfoResult getMergeInfoForAllSelectedBranchesInProject(String projectPath) {
		List<Branch> selectedBranches = profileProvider.getSelectedBranches();
		List<SvnFolder> branchesByNames = svnFolderDAO.findFoldersByNames(projectPath, selectedBranches);
		return mergeInfoService.findMerges(branchesByNames);
	}

	@Override
	@Transactional(readOnly = true)
	public Profile getProfileByIdOrDefault(Integer current) {
		Profile byId = profileDAO.findById(current);
		if (byId == null) {
			return profileProvider.getFirstProfile();
		}
		return byId;
	}

	@Override
	public Profile createNewProfile() {
		return profileDAO.save(new Profile());
	}

	@Override
	public Profile copyProfile(Profile configModel) {
		Profile object = new Profile(configModel);
		return profileDAO.save(object);
	}

	@Override
	@Transactional(readOnly = true)
	public Boolean isMergeOnSubFoldersForProject(String path) {
		GlobalSettings first = globalSettingsProvider.getGlobalSettings();
		return first.isMergeOnSubFoldersForProject(path);
	}

	@Transactional(readOnly = true)
	@Override
	public GlobalSettings getGlobalSettings() {
		return globalSettingsProvider.getGlobalSettings();
	}

	@Override
	public void updateBranch(SvnFolder folder) {
		svnFolderDAO.save(folder);
	}

	@Override
	@Transactional(readOnly = true)
	public List<SvnFolder> getAllBranchesByProjectName(String name) {
		return svnFolderDAO.findByParentName(name, Type.BRANCH);
	}

	@Override
	public void addBranchIntoProfile(String objectAsString) {
		SvnFolder branchByInCaseSensitiveName = findBranchByInCaseSensitiveName(objectAsString);
		if (branchByInCaseSensitiveName != null) {
			profileProvider.addSelectedBranch(branchByInCaseSensitiveName.getName());
		}
	}

	@Override
	public void addAllMatchingBranchesIntoProfile(String fieldValue) {
		List<SvnFolder> branches = findBranchesByNameLike(fieldValue);
		profileProvider.addSelectedBranches(branches);
	}

	@Override
	@Transactional(readOnly = true)
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

	@Override
	public void setLoadTagsForProject(String path, Boolean modelObject) {
		globalSettingsProvider.getGlobalSettings().setLoadTagsForProject(path, modelObject);
	}

	@Override
	@Transactional(readOnly = true)
	public Boolean isLoadTags(String path) {
		return globalSettingsProvider.getGlobalSettings().isLoadTags(path);
	}

	@Override
	public void saveRepository(Repository modelObject) {
		repositoryGenericDAO.save(modelObject);
		GlobalSettings globalSettings = getGlobalSettings();
		if (globalSettings.getDefaultRepository() == null) {
			globalSettings.setDefaultRepository(modelObject);
			globalSettingsDAO.save(globalSettings);
		}
	}

	@Override
	public List<? extends Repository> getAllRepositories() {
		return repositoryGenericDAO.findAll();
	}

	@Override
	public void deleteRepository(Integer id) {
		Repository byId = repositoryGenericDAO.findById(id);
		repositoryGenericDAO.delete(byId);
	}

	@Override
	public void setMergeOnSubFoldersForProject(String path, Boolean modelObject) {
		GlobalSettings settings = globalSettingsProvider.getGlobalSettings();
		settings.setProjectsWithSubfoldersMergeSearching(path, modelObject);
		globalSettingsDAO.save(settings);
	}

	@Transactional(readOnly = true)
	@Override
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

	@Override
	public List<Profile> getReleasesFromSvn() {
		return profileDAO.findAllByType(Profile.Type.FROM_SVN);
	}

	@Override
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

	@Override
	public void delete(Profile modelObject) {
		profileDAO.delete(modelObject);
		MySession.get().setCurrentProfile(profileProvider.getFirstProfile().getId());
	}

	@Override
	public String runRns(String profileName) {
		ProcessBuilder pb = new ProcessBuilder(rnsCommand, "releases/" + profileName);
		pb.redirectErrorStream(true);
		pb.directory(new File(rnsDirectory));
		try {
			Process p = pb.start();
			String x = IOUtils.toString(p.getInputStream());
			String xs = IOUtils.toString(p.getErrorStream());
			p.destroy();
			return x;
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public String runVersionsOnPrgens() {
		ProcessBuilder pb = new ProcessBuilder(versionsOnPrgensCommand);
		pb.redirectErrorStream(true);
		pb.directory(new File(rnsDirectory));
		try {
			Process p = pb.start();
			String x = IOUtils.toString(p.getInputStream());
			String xs = IOUtils.toString(p.getErrorStream());
			p.destroy();
			return x;
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}

	}

	@Override
	public String runSvnHeadVsLastTag(String profileName) {
		ProcessBuilder pb = new ProcessBuilder(svnHeadVsLastTagCommand, "releases/" + profileName);
		pb.redirectErrorStream(true);
		pb.directory(new File(rnsDirectory));
		try {
			Process p = pb.start();
			String x = IOUtils.toString(p.getInputStream());
			String xs = IOUtils.toString(p.getErrorStream());
			p.destroy();
			return x;
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
	}

	@Override
	public void updateGlobalSettings(GlobalSettings globalSettings) {
		globalSettingsDAO.save(globalSettings);
	}

	@Transactional(readOnly = true)
	@Override
	public List<SvnFolder> getProjects() {
		return svnFolderDAO.findAllProjects();
	}

	@Override
	@Transactional(readOnly = true)
	public List<Profile> getProfiles() {
		return profileDAO.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public Profile getDefaultProfile() {
		return profileProvider.getFirstProfile();
	}

	@Override
	@Transactional(readOnly = true)
	public List<SvnFolder> getBranches() {
		return svnFolderDAO.findAll();
	}

}
