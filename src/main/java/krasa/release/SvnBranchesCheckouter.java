package krasa.release;

import java.io.File;
import java.util.List;

import krasa.merge.backend.domain.Repository;
import krasa.merge.backend.domain.SvnFolder;
import krasa.merge.backend.domain.Type;
import krasa.merge.backend.svn.SvnFolderProvider;
import krasa.merge.backend.svn.SvnFolderProviderImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;

public class SvnBranchesCheckouter {

	protected static final Logger log = LoggerFactory.getLogger(SvnBranchesCheckouter.class);

	public static final int INT = 14100;
	public static final String SVN = "http://svn/sdp";
	public static final String TARGET = "D:/workspace/_projekty/_T-Mobile/";

	public static void main(String[] args) throws SVNException {
		new SvnBranchesCheckouter().checkout(SVN, new File(TARGET + INT), String.valueOf(INT));
	}

	public void checkout(String svnUrl, final File baseDir, final String branchNameSuffix) throws SVNException {
		SVNClientManager svnClientManager = SVNClientManager.newInstance();
		SvnFolderProvider svnFolderProvider = new SvnFolderProviderImpl(new Repository(svnUrl));
		List<SVNDirEntry> projects = svnFolderProvider.getProjects();
		for (SVNDirEntry project : projects) {
			List<SvnFolder> projectContent = svnFolderProvider.getProjectContent(
					new SvnFolder(project, project.getName(), Type.PROJECT), false);
			for (SvnFolder svnFolder : projectContent) {
				if (svnFolder.nameEndsWith(branchNameSuffix)) {
					String url = svnUrl + "/" + svnFolder.getPath();
					String pathname = baseDir + "/" + svnFolder.getName();
					File destPath = new File(pathname);
					if (!destPath.exists()) {
						log.info("checking out: " + url + " into " + pathname);
						checkoutSingleFolder(svnClientManager, url, destPath);
					}
				}
			}
		}
	}

	public void checkoutSingleFolder(SVNClientManager svnClientManager, String url, File destPath) throws SVNException {

		SVNUpdateClient updateClient = svnClientManager.getUpdateClient();
		/*
		 * sets externals not to be ignored during the checkout
		 */
		updateClient.setIgnoreExternals(false);
		/*
		 * returns the number of the revision at which the working copy is
		 */
		log.info("checkouting: {} into {}", url, destPath.getAbsolutePath());
		updateClient.doCheckout(SVNURL.parseURIEncoded(url), destPath, SVNRevision.HEAD, SVNRevision.HEAD, true);
		log.info("Branch checkouted: {}", destPath.getAbsolutePath());
	}

}
