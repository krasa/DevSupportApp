package krasa.backend.service;

import krasa.backend.domain.GlobalSettings;
import krasa.backend.domain.SvnFolder;
import krasa.backend.dto.MergeInfoResult;
import krasa.backend.dto.MergeInfoResultItem;
import krasa.backend.svn.SVNConnector;
import krasa.backend.svn.SvnMergeInfoProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;

import java.util.*;

/**
 * @author Vojtech Krasa
 */
@Service
public class MergeInfoServiceImpl implements MergeInfoService {
    protected final Logger log = LoggerFactory.getLogger(getClass());


    @Autowired
    private SVNConnector svnConnection;

    @Autowired
    private GlobalSettingsProvider globalSettingsProvider;

    public MergeInfoResult findMerges(List<SvnFolder> branches) {
        GlobalSettings globalSettings = globalSettingsProvider.getGlobalSettings();

        MergeInfoResult mergeInfoResult = new MergeInfoResult();
        MultiValueMap<SvnFolder, SvnFolder> branchesByProjectMap = convert(branches);
        try {

            for (SvnFolder project : branchesByProjectMap.keySet()) {
                log.debug("findMerges for project " + project.getName());
                SvnMergeInfoProvider svnMergeInfoProvider = new SvnMergeInfoProvider(svnConnection.getConnection());

                List<SvnFolder> branchesByProject = branchesByProjectMap.get(project);
                sortByName(branchesByProject);
                for (int i = 0; i < branchesByProject.size() - 1; i++) {
                    SvnFolder to = branchesByProject.get(i);
                    SvnFolder from = branchesByProject.get(i + 1);
                    if (globalSettings.isMergeOnSubFoldersForProject(project.getName())) {
                        for (String commonFolder : getCommonSubFolders(to, from)) {
                            List<SVNLogEntry> merges = svnMergeInfoProvider.getMerges(to, from, commonFolder);
                            mergeInfoResult.add(new MergeInfoResultItem(to, from, commonFolder, merges));
                        }
                    } else {
                        List<SVNLogEntry> merges = svnMergeInfoProvider.getMerges(to, from);
                        mergeInfoResult.add(new MergeInfoResultItem(to, from, merges));
                    }

                }
            }
        } catch (SVNException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        return mergeInfoResult;
    }

    private Set<String> getCommonSubFolders(SvnFolder to, SvnFolder from) {
        Set<String> subFoldersTo = to.getChildNamesAsSet();
        Set<String> subFolders = from.getChildNamesAsSet();
        Set<String> commonFolders = new HashSet<String>();
        for (String s : subFoldersTo) {
            if (subFolders.contains(s)) {
                commonFolders.add(s);
            }
        }
        return commonFolders;
    }

    private void sortByName(List<SvnFolder> branchesByProject) {
        Collections.sort(branchesByProject, new Comparator<SvnFolder>() {
            public int compare(SvnFolder o1, SvnFolder o2) {
                String name = o2.getName();
                String name1 = o1.getName();
                return name.compareTo(name1);
            }
        });
    }

    private MultiValueMap<SvnFolder, SvnFolder> convert(List<SvnFolder> branches) {
        MultiValueMap<SvnFolder, SvnFolder> branchesByProjectMap = new LinkedMultiValueMap<SvnFolder, SvnFolder>();
        for (SvnFolder folder : branches) {
            branchesByProjectMap.add(folder.getParent(), folder);
        }
        return branchesByProjectMap;
    }
}
