package krasa.merge.backend.svn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import krasa.merge.backend.domain.SvnFolder;
import krasa.merge.backend.domain.Type;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.io.SVNRepository;

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

	public List<SvnFolder> getBranchesByProjectName(String projectName) {
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
					SvnUtils.printInfo(project);
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
				SvnUtils.printSubDir(projectName, entry);
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
			SvnUtils.printSubDir(pathToParentDir, entry);
			if (entry.getKind() == SVNNodeKind.DIR) {
				SvnUtils.printSubDir(pathToParentDir, entry);
				result.add(new SvnFolder(entry, pathToParentDir, Type.BRANCH));
			}
		}
		log.info("iteration of branches finnished");

	}

	private static boolean isBranchesDir(SVNDirEntry entry) {
		return entry.getKind() == SVNNodeKind.DIR
				&& (entry.getName().equalsIgnoreCase("branch") || entry.getName().equalsIgnoreCase("branches"));
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
					SvnUtils.printSubDir(branch.getPath() + "/Branches", entry);
					result.add(new SvnFolder(entry, branch.getPath(), Type.BRANCH_SUBFOLDER));
				}
			}
			log.info("iteration of branch subdirs finnished");
			return result;
		} catch (SVNException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public List<SVNDirEntry> getTags(SvnFolder branchesByName) {
		ArrayList<SVNDirEntry> svnDirEntries = new ArrayList<SVNDirEntry>();
		SvnFolder parent = branchesByName.getParent();
		String name = parent.getName();
		Collection tags = null;
		try {
			String path = name + "/tags";
			tags = repository.getDir(path, -1, null, (Collection) null);
		} catch (SVNException e) {
			throw new RuntimeException(e);
		}
		Iterator iterator = tags.iterator();
		while (iterator.hasNext()) {
			SVNDirEntry entry = (SVNDirEntry) iterator.next();
			if (entry.getKind() == SVNNodeKind.DIR) {
				if (entry.getName().startsWith(branchesByName.getName())) {
					SvnUtils.printSubDir(name + "/tags", entry);
					svnDirEntries.add(entry);
				}
			}
		}
		return svnDirEntries;
	}
}
