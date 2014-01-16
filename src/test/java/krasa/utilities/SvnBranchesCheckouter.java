package krasa.utilities;

import java.io.File;
import java.util.List;

import krasa.merge.backend.domain.Repository;
import krasa.merge.backend.domain.SvnFolder;
import krasa.merge.backend.svn.SvnFolderProvider;

import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;

public class SvnBranchesCheckouter {

	public static final int INT = 14100;
	public static final String SVN = "http://svn/sdp";
	public static final String TARGET = "D:/workspace/_projekty/_T-Mobile/";
	static SVNClientManager svnClientManager = SVNClientManager.newInstance();

	public static void main(String[] args) throws SVNException {
		SvnFolderProvider svnFolderProvider = new SvnFolderProvider(new Repository(SVN));
		List<SVNDirEntry> projects = svnFolderProvider.getProjects();
		for (SVNDirEntry project : projects) {
			List<SvnFolder> projectContent = svnFolderProvider.getProjectContent(project.getName(), false);
			for (SvnFolder svnFolder : projectContent) {
				if (svnFolder.getName().endsWith(String.valueOf(INT))) {
					String url = SVN + "/" + svnFolder.getPath();
					String pathname = TARGET + INT + "/" + svnFolder.getName();
					File destPath = new File(pathname);
					if (!destPath.exists()) {
						System.err.println("checking out: " + url + " into " + pathname);
						checkout(SVNURL.parseURIEncoded(url), SVNRevision.HEAD, destPath, true);
					}
				}
			}
		}

	}

	private static long checkout(SVNURL url, SVNRevision revision, File destPath, boolean isRecursive)
			throws SVNException {

		SVNUpdateClient updateClient = svnClientManager.getUpdateClient();
		/*
		 * sets externals not to be ignored during the checkout
		 */
		updateClient.setIgnoreExternals(false);
		/*
		 * returns the number of the revision at which the working copy is
		 */
		return updateClient.doCheckout(url, destPath, revision, revision, isRecursive);
	}
}
