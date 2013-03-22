package krasa.merge.backend.svn;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import krasa.merge.backend.SvnException;
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
	@Value("${indexTrunk}")
	protected Boolean indexTrunk;
	private SVNRepository repository;

	public SvnFolderProvider(SVNRepository repository) {
		this.repository = repository;
	}

	public List<SvnFolder> getBranchesAndTrunkByProjectName(String projectName) {
		log.debug("getBranches start");
		List<SvnFolder> result = new ArrayList<SvnFolder>();
		result.addAll(getBranches(projectName));
		if (indexTrunk) {
			result.add(getTrunk(projectName));
		}
		log.debug("getBranches finished");
		return result;
	}

	public List<SVNDirEntry> getProjects() {
		log.debug("getProjects start");
		List<SVNDirEntry> result = new ArrayList<SVNDirEntry>();
		try {
			Collection projects = repository.getDir(urlPostfix, -1, null, (Collection) null);
			Iterator iterator = projects.iterator();
			while (iterator.hasNext()) {
				SVNDirEntry project = (SVNDirEntry) iterator.next();
				if (project.getKind() == SVNNodeKind.DIR) {
					SvnUtils.printInfo(project);
					result.add(project);
				}
			}
		} catch (SVNException e) {
			throw new SvnException(e);
		}
		log.debug("getProjects finished");
		return result;
	}

	private List<SvnFolder> getBranches(String projectName) {
		List<SvnFolder> result = new ArrayList<SvnFolder>();
		try {
			Collection projectDirs = repository.getDir(projectName, -1, null, (Collection) null);
			Iterator projectDirsIterator = projectDirs.iterator();
			while (projectDirsIterator.hasNext()) {
				SVNDirEntry entry = (SVNDirEntry) projectDirsIterator.next();
				SvnUtils.printSubDir(projectName, entry);
				if (isBranchesDir(entry)) {
					result.addAll(iterateFolder(projectName + "/" + entry.getName(), Type.BRANCH));
				}
			}
		} catch (SVNException e) {
			throw new SvnException(e);
		}
		return result;
	}

	private SvnFolder getTrunk(String projectName) {
		try {
			Collection projectDirs = repository.getDir(projectName, -1, null, (Collection) null);
			Iterator projectDirsIterator = projectDirs.iterator();
			while (projectDirsIterator.hasNext()) {
				SVNDirEntry entry = (SVNDirEntry) projectDirsIterator.next();
				SvnUtils.printSubDir(projectName, entry);
				if (isTrunkDir(entry)) {
					SvnFolder trunk = findTrunk(projectName, entry);
					if (trunk == null) {
						trunk = SvnFolder.createTrunk(projectName, projectName + "/" + entry.getRelativePath());
					}
					return trunk;
				}
			}
		} catch (SVNException e) {
			throw new SvnException(e);
		}
		throw new IllegalStateException("no trunk found for " + projectName);
	}

	private SvnFolder findTrunk(String projectName, SVNDirEntry entry) throws SVNException {
		List<SvnFolder> svnFolders = iterateFolder(projectName + "/" + entry.getName(), Type.BRANCH);
		SvnFolder trunk = null;
		for (SvnFolder svnFolder : svnFolders) {
			if (svnFolder.getName().equals(projectName)) {
				trunk = svnFolder;
				trunk.setNameAsTrunk(projectName);
			}
		}
		return trunk;
	}

	private boolean isTrunkDir(SVNDirEntry entry) {
		return entry.getKind() == SVNNodeKind.DIR && (entry.getName().equalsIgnoreCase("trunk"));
	}

	private List<SvnFolder> iterateFolder(final String pathToParentDir, final Type type) throws SVNException {
		List<SvnFolder> result = new ArrayList<SvnFolder>();
		Collection branches = repository.getDir(pathToParentDir, -1, null, (Collection) null);
		Iterator iterator = branches.iterator();
		while (iterator.hasNext()) {
			SVNDirEntry subFolderEntry = (SVNDirEntry) iterator.next();
			SvnUtils.printSubDir(pathToParentDir, subFolderEntry);
			if (subFolderEntry.getKind() == SVNNodeKind.DIR) {
				SvnUtils.printSubDir(pathToParentDir, subFolderEntry);
				result.add(new SvnFolder(subFolderEntry, pathToParentDir + "/" + subFolderEntry.getName(), type));
			}
		}
		log.info("iteration of branches finnished");
		return result;
	}

	private static boolean isBranchesDir(SVNDirEntry entry) {
		return entry.getKind() == SVNNodeKind.DIR
				&& (entry.getName().equalsIgnoreCase("branch") || entry.getName().equalsIgnoreCase("branches"));
	}

	public List<SvnFolder> getSubFolders(SvnFolder branch) {
		try {
			log.info("getSubFolders start");
			ArrayList<SvnFolder> result = new ArrayList<SvnFolder>();
			Collection branchSubDir = repository.getDir(branch.getPath(), -1, null, (Collection) null);
			Iterator iterator = branchSubDir.iterator();
			while (iterator.hasNext()) {
				SVNDirEntry entry = (SVNDirEntry) iterator.next();
				if (entry.getKind() == SVNNodeKind.DIR) {
					SvnUtils.printSubDir(branch.getPath() + "/Branches", entry);
					result.add(new SvnFolder(entry, branch.getPath() + "/" + entry.getName(), Type.SUBFOLDER));
				}
			}
			log.info("iteration of branch subdirs finnished");
			return result;
		} catch (SVNException e) {
			throw new SvnException(e);
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
			throw new SvnException(e);
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
