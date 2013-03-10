package krasa.merge.backend.facade;

import java.util.List;

import krasa.core.backend.domain.GlobalSettings;
import krasa.merge.backend.domain.Profile;
import krasa.merge.backend.domain.SvnFolder;
import krasa.merge.backend.dto.MergeInfoResult;
import krasa.merge.backend.dto.ReportResult;

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

	SvnFolder findBranchByInCaseSensitiveName(String objectAsString);

	void addSelectedBranch(String objectAsString);

	MergeInfoResult getMergeInfo();

	MergeInfoResult getMergeInfo(String path);

	Profile getProfileByIdOrDefault(Integer id);

	Profile createNewProfile();

	Profile copyProfile(Profile configModel);

	Boolean isMergeOnSubFoldersForProject(String path);

	void setMergeOnSubFoldersForProject(String path, Boolean modelObject);

	ReportResult getReport();

	List<String> getSuggestions(String parentName, String input);

	List<Profile> getReleasesFromSvn();

	void refreshReleasesFromSvn();

	void delete(Profile modelObject);

	String runRns(String profileName);

	String runVersionsOnPrgens();

	String runSvnHeadVsLastTag(String name);

	void updateGlobalSettings(GlobalSettings globalSettings);

	GlobalSettings getGlobalSettings();

	void updateBranch(SvnFolder folder);

	List<SvnFolder> getAllBranchesByProjectNme(String name);
}
