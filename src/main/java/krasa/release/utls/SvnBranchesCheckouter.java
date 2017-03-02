package krasa.release.utls;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;

import krasa.svn.backend.domain.Repository;
import krasa.svn.backend.domain.SvnFolder;
import krasa.svn.backend.domain.Type;
import krasa.svn.backend.service.SvnFolderProvider;

public class SvnBranchesCheckouter {

	protected static final Logger log = LoggerFactory.getLogger(SvnBranchesCheckouter.class);

	public static final String SVN = "http://svn.tmdev/sdp";
	public static final String TARGET = "C:/workspace/_projekty/_tmobile/";
	public static final int INT = 17100;

	public static void main(String[] args) throws SVNException {
		new SvnBranchesCheckouter().checkout(SVN, new File(TARGET + INT), Arrays.asList(".*_" + INT));
	}

	public void checkout(String svnUrl, File baseDir, List<String> branchNamePattern,
			CheckoutCallback... checkoutCallback) throws SVNException {
		log.info("Checkouting from {}, to {}, branches with patter {}", svnUrl, baseDir.getAbsolutePath(),
				branchNamePattern);

		SVNClientManager svnClientManager = SVNClientManager.newInstance();
		SvnFolderProvider svnFolderProvider = new SvnFolderProvider(new Repository(svnUrl));
		List<SVNDirEntry> projects = svnFolderProvider.getProjects();
		for (SVNDirEntry project : projects) {
			List<SvnFolder> projectContent = svnFolderProvider.getProjectContent(
					new SvnFolder(project, project.getName(), Type.PROJECT), false);
			for (SvnFolder svnFolder : projectContent) {
				if (svnFolder.nameMatches(branchNamePattern)) {
					String url = svnUrl + "/" + svnFolder.getPath();
					String pathname = baseDir + "/" + svnFolder.getName();
					File destPath = new File(pathname);
					if (!destPath.exists()) {
						checkoutSingleFolder(svnClientManager, url, destPath);
						for (CheckoutCallback callback : checkoutCallback) {
							callback.checkouted(url, destPath);
						}
					} else {
						svnClientManager.getUpdateClient().doUpdate(destPath, SVNRevision.HEAD, SVNDepth.INFINITY,
								false, false);
					}
				}
			}
		}
	}

	public void checkoutSingleFolder(SVNClientManager svnClientManager, String url, File destPath) throws SVNException {
		SVNUpdateClient updateClient = svnClientManager.getUpdateClient();
		updateClient.setIgnoreExternals(false);
		log.info("checkouting: {} into {}", url, destPath.getAbsolutePath());
		updateClient.doCheckout(SVNURL.parseURIEncoded(url), destPath, SVNRevision.HEAD, SVNRevision.HEAD,
				SVNDepth.INFINITY, true);
		log.info("Branch checkouted: {}", destPath.getAbsolutePath());
	}

	public abstract class CheckoutCallback {

		public abstract void checkouted(String url, File destPath);
	}
}
