package krasa.backend.svn;

import krasa.backend.domain.SvnFolder;
import krasa.backend.domain.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.io.SVNRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * @author Vojtech Krasa
 */
public class SvnFolderProvider {
    protected final Logger log = LoggerFactory.getLogger(getClass());
    @Value("${svn.url.postfix}")
    protected String urlPostfix;
    private SVNRepository repository;

    public SvnFolderProvider(SVNRepository repository) {
        this.repository = repository;
    }

    public List<SvnFolder> getBranches(String projectName) {
        log.debug("getBranches start");
        ArrayList<SvnFolder> result = new ArrayList<SvnFolder>();
        iterateProjectDirs(projectName, result);
        log.debug("getBranches finished");
        return result;
    }

    public List<SVNDirEntry> getProjects() {
        log.debug("getProjects start");
        List<SVNDirEntry> result = new ArrayList<SVNDirEntry>();
        try {
            Collection projects = null;
            projects = repository.getDir(urlPostfix, -1, null, (Collection) null);
            Iterator iterator = projects.iterator();
            while (iterator.hasNext()) {
                SVNDirEntry project = (SVNDirEntry) iterator.next();
                if (project.getKind() == SVNNodeKind.DIR) {
                    printInfo(project);
                    result.add(project);
                }
            }
        } catch (SVNException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
        log.debug("getProjects finished");
        return result;
    }

    private void iterateProjectDirs(String projectName, List<SvnFolder> result) {
        Collection projectDirs = null;
        try {
            projectDirs = repository.getDir(projectName, -1, null, (Collection) null);

            Iterator projectDirsIterator = projectDirs.iterator();
            while (projectDirsIterator.hasNext()) {
                SVNDirEntry entry = (SVNDirEntry) projectDirsIterator.next();
                printSubDir(projectName, entry);
                if (isBranchesDir(entry)) {
                    iterateBranches(projectName + "/" + entry.getName(), result);
                }
            }
        } catch (SVNException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void iterateBranches(String pathToParentDir, List<SvnFolder> result) throws SVNException {
        Collection branches = repository.getDir(pathToParentDir, -1, null, (Collection) null);
        Iterator iterator = branches.iterator();
        while (iterator.hasNext()) {
            SVNDirEntry entry = (SVNDirEntry) iterator.next();
            printSubDir(pathToParentDir, entry);
            if (entry.getKind() == SVNNodeKind.DIR) {
                printSubDir(pathToParentDir, entry);
                result.add(new SvnFolder(entry, pathToParentDir, Type.BRANCH));
            }
        }
        log.info("iteration of branches finnished");

    }

    private static boolean isBranchesDir(SVNDirEntry entry) {
        return entry.getKind() == SVNNodeKind.DIR && (entry.getName().equalsIgnoreCase("branch") || entry.getName().equalsIgnoreCase("branches"));
    }

    private void printSubDir(String pathToBranchesMainDir, SVNDirEntry entry) {
        System.out.println("/" + (pathToBranchesMainDir.equals("") ? "" : pathToBranchesMainDir + "/") + entry.getName() +
                " ( author: '" + entry.getAuthor() + "'; revision: " + entry.getRevision() +
                "; date: " + entry.getDate() + ")" + "relativePath " + entry.getRelativePath());
    }

    private void printInfo(SVNDirEntry project) {
        System.out.println("/" + project.getName() +
                " ( author: '" + project.getAuthor() + "'; revision: " + project.getRevision() +
                "; date: " + project.getDate() + ")");
    }

    public List<SvnFolder> getBranchSubFolders(SvnFolder branch) {
        try {
            log.info("getBranchSubFolders start");
            ArrayList<SvnFolder> result = new ArrayList<SvnFolder>();
            Collection branchSubDir = repository.getDir(branch.getPath(), -1, null, (Collection) null);
            Iterator iterator = branchSubDir.iterator();
            while (iterator.hasNext()) {
                SVNDirEntry entry = (SVNDirEntry) iterator.next();
                if (entry.getKind() == SVNNodeKind.DIR) {
                    printSubDir(branch.getPath() + "/Branches", entry);
                    result.add(new SvnFolder(entry, branch.getPath(), Type.BRANCH_SUBFOLDER));
                }
            }
            log.info("iteration of branch subdirs finnished");
            return result;
        } catch (SVNException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

}
