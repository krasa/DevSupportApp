package krasa.merge.backend.svn;

import java.util.*;

import krasa.merge.backend.SvnException;
import krasa.merge.backend.domain.*;
import krasa.merge.backend.svn.connection.SVNConnector;

import org.slf4j.*;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.io.SVNRepository;

/**
 * @author Vojtech Krasa
 */
public class SvnFolderProvider {

	protected final Logger log = LoggerFactory.getLogger(getClass());
	protected final boolean indexTrunk;
	private SVNRepository repository;

	public SvnFolderProvider(Repository repository) {
		indexTrunk = repository.isIndexTrunk();
		this.repository = new SVNConnector().connect(repository);
	}

	public SvnFolderProvider(Repository repository, SVNRepository connection) {
		indexTrunk = repository.isIndexTrunk();
		this.repository = connection;
	}

	public List<SVNDirEntry> getProjects() {
		log.debug("getProjects start");
		List<SVNDirEntry> result = new ArrayList<>();
		try {
			Collection projects = repository.getDir(projetsRootDir(), -1, null, (Collection) null);
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

	protected String projetsRootDir() {
		return "";
	}

	public List<SvnFolder> getProjectContent(SvnFolder project, Boolean loadTags) {
		log.debug("getBranches start");
		List<SvnFolder> result = new ArrayList<>();
		String projectPath = project.getPath();
		result.addAll(getBranches(project));
		if (loadTags) {
			result.addAll(getTags(projectPath));
		}
		log.debug("getBranches finished");
		return result;
	}

	protected List<SvnFolder> getTags(String projectPath) {
		return getFolders(projectPath, Type.TAG, "tags");
	}

	protected List<SvnFolder> getBranches(SvnFolder project) {
		return getFolders(project.getPath(), Type.BRANCH, "branch", "branches");
	}

	private List<SvnFolder> getFolders(String projectName, Type type, String... folderNames) {
		List<SvnFolder> result = new ArrayList<>();
		try {
			Collection projectDirs = repository.getDir(projectName, -1, null, (Collection) null);
			Iterator projectDirsIterator = projectDirs.iterator();
			while (projectDirsIterator.hasNext()) {
				SVNDirEntry entry = (SVNDirEntry) projectDirsIterator.next();
				SvnUtils.printSubDir(projectName, entry);
				if (isDirWithName(entry, folderNames)) {
					result.addAll(iterateFolder(projectName + "/" + entry.getName(), type));
				}
			}
		} catch (SVNException e) {
			throw new SvnException(e);
		}
		return result;
	}

	public List<SVNDirEntry> getTags(SvnFolder branchesByName) {
		ArrayList<SVNDirEntry> svnDirEntries = new ArrayList<>();
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

	private SvnFolder getTrunk(String projectName) {
		try {
			Collection projectDirs = repository.getDir(projectName, -1, null, (Collection) null);
			Iterator projectDirsIterator = projectDirs.iterator();
			while (projectDirsIterator.hasNext()) {
				SVNDirEntry entry = (SVNDirEntry) projectDirsIterator.next();
				SvnUtils.printSubDir(projectName, entry);
				if (isDirWithName(entry, "trunk")) {
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
		List<SvnFolder> svnFolders = iterateFolder(projectName + "/" + entry.getName(), Type.TRUNK);
		SvnFolder trunk = null;
		for (SvnFolder svnFolder : svnFolders) {
			if (svnFolder.getName().equals(projectName)) {
				trunk = svnFolder;
				trunk.setNameAsTrunk(projectName);
			}
		}
		return trunk;
	}

	private List<SvnFolder> iterateFolder(String pathToParentDir, Type type) throws SVNException {
		List<SvnFolder> result = new ArrayList<>();
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
		log.debug("iteration of branches finnished");
		return result;
	}

	private static boolean isDirWithName(SVNDirEntry entry, String... folderNames) {
		boolean isDir = entry.getKind() == SVNNodeKind.DIR;
		return isDir && containsName(entry, folderNames);
	}

	private static boolean containsName(SVNDirEntry entry, String[] folderNames) {
		for (String folderName : folderNames) {
			if (entry.getName().equalsIgnoreCase(folderName)) {
				return true;
			}
		}
		return false;
	}

	public List<SvnFolder> getSubFolders(SvnFolder branch) {
		try {
			log.info("getSubFolders start");
			ArrayList<SvnFolder> result = new ArrayList<>();
			Collection branchSubDir = repository.getDir(branch.getPath(), -1, null, (Collection) null);
			Iterator iterator = branchSubDir.iterator();
			while (iterator.hasNext()) {
				SVNDirEntry entry = (SVNDirEntry) iterator.next();
				if (entry.getKind() == SVNNodeKind.DIR) {
					SvnUtils.printSubDir(branch.getPath() + "/" + branch.getType(), entry);
					result.add(new SvnFolder(entry, branch.getPath() + "/" + entry.getName(), Type.SUBFOLDER));
				}
			}
			log.info("iteration of branch subdirs finnished");
			return result;
		} catch (SVNException e) {
			throw new SvnException(e);
		}
	}
}
