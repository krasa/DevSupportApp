package krasa.merge.backend.facade;

import java.io.*;
import java.util.*;

import krasa.build.backend.domain.*;
import krasa.core.backend.config.MainConfig;
import krasa.core.backend.dao.*;
import krasa.core.backend.domain.GlobalSettings;
import krasa.core.backend.service.GlobalSettingsProvider;
import krasa.core.frontend.MySession;
import krasa.merge.backend.dao.*;
import krasa.merge.backend.domain.*;
import krasa.merge.backend.dto.*;
import krasa.merge.backend.service.*;
import krasa.merge.backend.service.conventions.ConventionsStrategyHolder;
import krasa.merge.backend.svn.SvnReleaseProvider;

import org.apache.wicket.util.io.IOUtils;
import org.hibernate.*;
import org.hibernate.criterion.Restrictions;
import org.slf4j.*;
import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author Vojtech Krasa
 */
@Service
@Transactional(value = MainConfig.HSQLDB_TX_MANAGER)
public class Facade {

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
	@Autowired
	@Qualifier("sessionFactory")
	protected SessionFactory sf;

	public Facade() {

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
		final GlobalSettings globalSettings = getGlobalSettings();
		final Repository defaultRepository = globalSettings.getDefaultRepository();
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

	public void cleanHsqldb() {
		log.info("cleaning Hsqldb");
		deleteOldBuildJobs();
		limitNumberOfBuildJobs();
	}

	private void limitNumberOfBuildJobs() {
		List<Long> ids = sf.getCurrentSession().createQuery("select d.id from BuildJob  d order by d.id desc").setMaxResults(
				200).list();
		int deleted = 0;
		List<BuildJob> list = sf.getCurrentSession().createQuery("from BuildJob b where b.id not in :ids").setParameterList(
				"ids", ids).setMaxResults(1000).list();
		for (BuildJob o : list) {
			BuildableComponent buildableComponent = o.getBuildableComponent();
			BuildJob lastBuildJob = buildableComponent.getLastBuildJob();
			if (lastBuildJob != o) {
				log.info("deleting {}", o);
				sf.getCurrentSession().delete(o);
				deleted++;
			} else {
				buildableComponent.setLastBuildJob(null);
				sf.getCurrentSession().save(buildableComponent);
				log.info("deleting {}", o);
				sf.getCurrentSession().delete(o);
				deleted++;
			}
		}
		log.info("deleted: {}", deleted);
	}

	private void deleteOldBuildJobs() {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_WEEK, -7);

		final Session session = sf.getCurrentSession();
		final Criteria crit = session.createCriteria(BuildJob.class);
		crit.add(Restrictions.le("endTime", cal.getTime()));
		List<BuildJob> list = crit.list();
		log.info("found: {}", list.size());
		int deleted = 0;
		for (BuildJob o : list) {
			BuildableComponent buildableComponent = o.getBuildableComponent();
			BuildJob lastBuildJob = buildableComponent.getLastBuildJob();
			if (lastBuildJob != o) {
				log.info("deleting {}", o);
				session.delete(o);
				deleted++;
			}
		}
		log.info("deleted: {}", deleted);
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
			System.err.println(xs);
			p.destroy();
			return x;
		} catch (IOException e) {
			throw new IllegalArgumentException(e);
		}
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
