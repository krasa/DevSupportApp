package krasa.merge.backend.svn;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNDirEntry;

/**
 * @author Vojtech Krasa
 */
public class SvnUtils {

	protected static final Logger log = LoggerFactory.getLogger(SvnUtils.class);

	public static void printInfo(SVNDirEntry project) {
		log.debug("/" + project.getName() + " ( author: '" + project.getAuthor() + "'; revision: "
				+ project.getRevision() + "; date: " + project.getDate() + ")");
	}

	static void printSubDir(String pathToBranchesMainDir, SVNDirEntry entry) {
		log.debug("/" + (pathToBranchesMainDir.equals("") ? "" : pathToBranchesMainDir + "/") + entry.getName()
				+ " ( author: '" + entry.getAuthor() + "'; revision: " + entry.getRevision() + "; date: "
				+ entry.getDate() + ")" + "relativePath " + entry.getRelativePath());
	}
}
