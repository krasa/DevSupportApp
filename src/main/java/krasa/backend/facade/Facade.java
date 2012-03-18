package krasa.backend.facade;

import krasa.backend.domain.Profile;
import krasa.backend.domain.ReportResult;
import krasa.backend.domain.SvnFolder;
import krasa.backend.dto.MergeInfoResult;

import java.util.List;
import java.util.Set;

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

    Set<String> getSelectedBranchesName();

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

    List<SvnFolder> getSuggestions(String parentName, String input);
}
