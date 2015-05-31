package krasa.release.utls;

import java.io.File;
import java.util.*;

import krasa.svn.backend.domain.*;
import krasa.svn.backend.service.SvnFolderProvider;

import org.slf4j.*;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.wc.*;

public class SvnBranchesCheckouter {

	protected static final Logger log = LoggerFactory.getLogger(SvnBranchesCheckouter.class);

	public static final String SVN = "http://svn/sdp";
	public static final String TARGET = "D:/workspace/_projekty/_T-Mobile/";
	public static final int INT = 90001;

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
