package krasa.backend.facade;

import krasa.backend.dao.GlobalSettingsDAO;
import krasa.backend.dao.ProfileDAO;
import krasa.backend.dao.SvnFolderDAO;
import krasa.backend.domain.GlobalSettings;
import krasa.backend.domain.Profile;
import krasa.backend.domain.ReportResult;
import krasa.backend.domain.SvnFolder;
import krasa.backend.dto.MergeInfoResult;
import krasa.backend.service.GlobalSettingsProvider;
import krasa.backend.service.MergeInfoService;
import krasa.backend.service.ProfileProvider;
import krasa.backend.service.SvnFolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * @author Vojtech Krasa
 */
@Service
@Transactional
public class FacadeImpl implements Facade {

    @Autowired
    private GlobalSettingsProvider globalSettingsProvider;
    @Autowired
    private GlobalSettingsDAO globalSettingsDAO;
    @Autowired
    private SvnFolderDAO svnFolderDAO;
    @Autowired
    private MergeInfoService mergeInfoService;
    @Autowired
    private ProfileProvider profileProvider;
    @Autowired
    private ProfileDAO profileDAO;
    @Autowired
    private SvnFolderService folderService;


    public List<SvnFolder> getSubDirs(String name) {
        return svnFolderDAO.getSubDirsByParentPath(name);
    }

    public void updateProfile(Profile modelObject) {
        Profile byId = profileDAO.findById(modelObject.getId());
        byId.setName(modelObject.getName());
        profileDAO.save(modelObject);
    }


    public Set<String> getSelectedBranchesName() {
        Set<String> selectedBranches = profileProvider.getSelectedBranches();
        selectedBranches.size();
        return selectedBranches;
    }

    public List<SvnFolder> getSelectedBranches() {
        Set<String> selectedBranches = profileProvider.getSelectedBranches();
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

    public MergeInfoResult getMergeInfo() {
        Set<String> selectedBranches = profileProvider.getSelectedBranches();
        List<SvnFolder> branchesByNames = svnFolderDAO.findBranchesByNames(selectedBranches);
        return mergeInfoService.findMerges(branchesByNames);
    }

    public MergeInfoResult getMergeInfo(String path) {
        Set<String> selectedBranches = profileProvider.getSelectedBranches();
        List<SvnFolder> branchesByNames = svnFolderDAO.findBranchesByNames(path, selectedBranches);
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

    public void setMergeOnSubFoldersForProject(String path, Boolean modelObject) {
        GlobalSettings settings = globalSettingsProvider.getGlobalSettings();
        settings.setProjectsWithSubfoldersMergeSearching(path, modelObject);
        globalSettingsDAO.save(settings);
    }

    public ReportResult getReport() {
        return null;//todo
    }

    public List<SvnFolder> getSuggestions(String parentName, String input) {
        List<SvnFolder> branchesByNameLike = svnFolderDAO.findBranchesByNameLike(input, parentName);
        return branchesByNameLike;
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
