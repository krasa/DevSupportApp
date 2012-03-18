package krasa.backend.service;

import krasa.backend.domain.SvnFolder;
import krasa.backend.svn.SVNConnector;
import krasa.backend.svn.SvnFolderProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.tmatesoft.svn.core.SVNDirEntry;

import java.util.List;

/**
 * @author Vojtech Krasa
 */
@Component
@Transactional
public class SvnLoaderProcessorImpl implements SvnLoaderProcessor {

    @Autowired
    private SVNConnector svnConnection;

    @Autowired
    private SvnFolderService folderService;

    public void refreshProjectBranches(SvnFolder project) {
        for (SvnFolder svnFolder : project.getChilds()) {
            folderService.delete(svnFolder);
        }

        SvnFolderProvider provider = new SvnFolderProvider(svnConnection.getConnection());

        List<SvnFolder> branches = provider.getBranches(project.getName());
        for (SvnFolder branch : branches) {
            project.add(branch);
            folderService.save(branch);
            List<SvnFolder> branchSubFolders = provider.getBranchSubFolders(branch);
            saveSubFolders(branch, branchSubFolders);
        }
        folderService.save(project);
    }

    private void saveSubFolders(SvnFolder branch1, List<SvnFolder> projectSubFolder) {
        for (SvnFolder subFolder : projectSubFolder) {
            branch1.add(subFolder);
            folderService.save(subFolder);
        }
    }

    public void refreshProjects() {
        SvnFolderProvider svnFolderProvider = new SvnFolderProvider(svnConnection.getConnection());
        folderService.deleteAll();
        List<SVNDirEntry> projects = svnFolderProvider.getProjects();
        folderService.saveProjects(projects);
    }

    public void refreshAllBranches() {
        for (SvnFolder svnFolder : folderService.findAllProjects()) {
            refreshProjectBranches(svnFolder);
        }
    }

    public void refreshBranchesByProjectName(String name) {
        SvnFolder project = folderService.findProjectByName(name);
        refreshProjectBranches(project);
    }
}
