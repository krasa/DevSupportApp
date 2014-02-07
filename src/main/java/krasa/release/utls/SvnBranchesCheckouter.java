package krasa.release.utls;

import java.io.File;
import java.util.List;

import krasa.merge.backend.domain.Repository;
import krasa.merge.backend.domain.SvnFolder;
import krasa.merge.backend.domain.Type;
import krasa.merge.backend.svn.SvnFolderProvider;
import krasa.merge.backend.svn.SvnFolderProviderImpl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNDepth;
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

	public void checkout(String svnUrl, final File baseDir, final String branchNamePattern,
			CheckoutCallback... checkoutCallback) throws SVNException {
		SVNClientManager svnClientManager = SVNClientManager.newInstance();
		SvnFolderProvider svnFolderProvider = new SvnFolderProviderImpl(new Repository(svnUrl));
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
						log.info("checking out: " + url + " into " + pathname);
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
