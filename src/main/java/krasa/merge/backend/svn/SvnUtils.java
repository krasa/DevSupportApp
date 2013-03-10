package krasa.merge.backend.svn;

import org.tmatesoft.svn.core.SVNDirEntry;

/**
 * @author Vojtech Krasa
 */
public class SvnUtils {
	public static void printInfo(SVNDirEntry project) {
		System.out.println("/" + project.getName() + " ( author: '" + project.getAuthor() + "'; revision: "
				+ project.getRevision() + "; date: " + project.getDate() + ")");
	}

	static void printSubDir(String pathToBranchesMainDir, SVNDirEntry entry) {
		System.out.println("/" + (pathToBranchesMainDir.equals("") ? "" : pathToBranchesMainDir + "/")
				+ entry.getName() + " ( author: '" + entry.getAuthor() + "'; revision: " + entry.getRevision()
				+ "; date: " + entry.getDate() + ")" + "relativePath " + entry.getRelativePath());
	}
}
