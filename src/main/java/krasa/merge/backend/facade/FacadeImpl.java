package krasa.merge.backend.facade;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import krasa.build.backend.domain.ComponentBuild;
import krasa.build.backend.domain.Environment;
import krasa.core.backend.dao.GenericDAO;
import krasa.core.backend.dao.GenericDaoBuilder;
import krasa.core.backend.domain.GlobalSettings;
import krasa.core.backend.service.GlobalSettingsProvider;
import krasa.core.frontend.MySession;
import krasa.merge.backend.dao.ProfileDAO;
import krasa.merge.backend.dao.SvnFolderDAO;
import krasa.merge.backend.domain.Branch;
import krasa.merge.backend.domain.Profile;
import krasa.merge.backend.domain.SvnFolder;
import krasa.merge.backend.dto.MergeInfoResult;
import krasa.merge.backend.dto.ReportResult;
import krasa.merge.backend.service.MergeInfoService;
import krasa.merge.backend.service.ProfileProvider;
import krasa.merge.backend.service.ReportService;
import krasa.merge.backend.service.SvnFolderService;
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
	private GenericDAO<ComponentBuild> branchBuildDAO;
	private GenericDAO<Branch> branchDAO;
	@Autowired
	private SvnFolderService folderService;
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
		this.branchBuildDAO = genericDAO.build(ComponentBuild.class);
		this.branchDAO = genericDAO.build(Branch.class);
		this.globalSettingsDAO = genericDAO.build(GlobalSettings.class);
	}

	public List<SvnFolder> getSubDirs(String name) {
		return svnFolderDAO.getSubDirsByParentPath(name);
	}

	public void updateProfile(Profile modelObject) {
		Profile byId = profileDAO.findById(modelObject.getId());
		byId.setName(modelObject.getName());
		profileDAO.save(byId);
	}

	public List<String> getSelectedBranchesNames() {
		return profileProvider.getSelectedBranchesNames();
	}

	public List<SvnFolder> getSelectedBranches() {
		List<Branch> selectedBranches = profileProvider.getSelectedBranches();
		return svnFolderDAO.findBranchesByNames(selectedBranches);
	}

	public SvnFolder getSvnFolderById(Integer id) {
		return svnFolderDAO.findById(id);
	}

	public void updateSelectionOfSvnFolder(SvnFolder object, Boolean aBoolean) {
		profileProvider.updateSelectionOfSvnFolder(object, aBoolean);
	}

	public List<SvnFolder> findBranchesByNameLike(String name) {
		return svnFolderDAO.findBranchesByNameLike(name);
	}

	public SvnFolder findBranchByInCaseSensitiveName(String name) {
		return svnFolderDAO.findBranchByInCaseSensitiveName(name);

	}

	public void addSelectedBranch(String objectAsString) {
		SvnFolder branchByInCaseSensitiveName = findBranchByInCaseSensitiveName(objectAsString);
		if (branchByInCaseSensitiveName != null) {
			profileProvider.addSelectedBranch(branchByInCaseSensitiveName.getName());
		}
	}

	public MergeInfoResult getMergeInfoForAllSelectedBranches() {
		List<Branch> selectedBranches = profileProvider.getSelectedBranches();
		List<SvnFolder> branchesByNames = svnFolderDAO.findBranchesByNames(selectedBranches);
		return mergeInfoService.findMerges(branchesByNames);
	}

	public MergeInfoResult getMergeInfoForProject(String projectPath) {
		List<Branch> selectedBranches = profileProvider.getSelectedBranches();
		List<SvnFolder> branchesByNames = svnFolderDAO.findBranchesByNames(projectPath, selectedBranches);
		return mergeInfoService.findMerges(branchesByNames);
	}

	public Profile getProfileByIdOrDefault(Integer current) {
		Profile byId = profileDAO.findById(current);
		if (byId == null) {
			return profileProvider.getFirstProfile();
		}
		return byId;
	}

	public Profile createNewProfile() {
		return profileDAO.save(new Profile());
	}

	public Profile copyProfile(Profile configModel) {
		Profile object = new Profile(configModel);
		return profileDAO.save(object);
	}

	public Boolean isMergeOnSubFoldersForProject(String path) {
		GlobalSettings first = globalSettingsProvider.getGlobalSettings();
		return first.isMergeOnSubFoldersForProject(path);
	}

	public GlobalSettings getGlobalSettings() {
		return globalSettingsProvider.getGlobalSettings();
	}

	public void updateBranch(SvnFolder folder) {
		svnFolderDAO.save(folder);
	}

	public List<SvnFolder> getAllBranchesByProjectNme(String name) {
		SvnFolder projectByName = svnFolderDAO.findProjectByName(name);
		List<SvnFolder> childs = projectByName.getChilds();
		childs.size();
		return childs;
	}

	public void setMergeOnSubFoldersForProject(String path, Boolean modelObject) {
		GlobalSettings settings = globalSettingsProvider.getGlobalSettings();
		settings.setProjectsWithSubfoldersMergeSearching(path, modelObject);
		globalSettingsDAO.save(settings);
	}

	public ReportResult getReport() {
		return reportService.getReport();
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

	public void updateGlobalSettings(GlobalSettings globalSettings) {
		globalSettingsDAO.save(globalSettings);
	}

	public List<SvnFolder> getProjects() {
		return folderService.findAllProjects();
	}

	public List<Profile> getProfiles() {
		return profileDAO.findAll();
	}

	public Profile getDefaultProfile() {
		return profileProvider.getFirstProfile();
	}

	public List<SvnFolder> getBranches() {
		return svnFolderDAO.findAll();
	}

}
