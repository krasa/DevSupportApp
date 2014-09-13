package krasa.merge.backend.facade;

import java.util.List;

import krasa.core.backend.config.MainConfig;
import krasa.core.backend.domain.GlobalSettings;
import krasa.merge.backend.domain.*;
import krasa.merge.backend.dto.*;

import org.springframework.transaction.annotation.Transactional;

/**
 * @author Vojtech Krasa
 */
public interface Facade {

	List<SvnFolder> getBranches();

	List<SvnFolder> getProjects();

	List<Profile> getProfiles();

	Profile getDefaultProfile();

	List<SvnFolder> getSubDirs(String name);

	void updateProfile(Profile modelObject);

	List<String> getSelectedBranchesNames();

	SvnFolder getSvnFolderById(Integer id);

	void updateSelectionOfSvnFolder(SvnFolder object, Boolean booleanIModelObject);

	List<SvnFolder> getSelectedBranches();

	List<SvnFolder> findBranchesByNameLike(String name);

	List<Displayable> findBranchesByNameLikeAsDisplayable(String name);

	List<Displayable> findTagsByNameLikeAsDisplayable(String input);

	SvnFolder findBranchByInCaseSensitiveName(String objectAsString);

	void addBranchIntoProfile(String objectAsString);

	MergeInfoResult getMergeInfoForAllSelectedBranches();

	MergeInfoResult getMergeInfoForAllSelectedBranchesInProject(String path);

	Profile getProfileByIdOrDefault(Integer id);

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER, readOnly = true)
	Profile getCurrentProfile();

	Profile createNewProfile();

	Profile copyProfile(Profile configModel);

	Boolean isMergeOnSubFoldersForProject(String path);

	void setMergeOnSubFoldersForProject(String path, Boolean modelObject);

	ReportResult getReport();

	List<Profile> getReleasesFromSvn();

	void refreshReleasesFromSvn();

	void delete(Profile modelObject);

	String runRns(String profileName);

	String runVersionsOnPrgens();

	String runSvnHeadVsLastTag(String name);

	void updateGlobalSettings(GlobalSettings globalSettings);

	GlobalSettings getGlobalSettings();

	void updateBranch(SvnFolder folder);

	List<SvnFolder> getAllBranchesByProjectName(String name);

	void addAllMatchingBranchesIntoProfile(String fieldValue);

	String resolveProjectByPath(String path);

	void setLoadTagsForProject(String path, Boolean modelObject);

	Boolean isLoadTags(String path);

	void saveRepository(Repository modelObject);

	List<Repository> getAllRepositories();

	void deleteRepository(Integer modelObject);

	void deleteAllSvnBranches();

	void deleteAllBranchesFromProfile();

	void replaceSearchFrom();

}
