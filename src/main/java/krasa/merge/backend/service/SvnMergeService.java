package krasa.merge.backend.service;

import java.io.File;
import java.util.Collections;

import krasa.merge.backend.dto.MergeInfoResultItem;

import org.springframework.stereotype.Service;
import org.tmatesoft.svn.core.SVNCancelException;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.ISVNEventHandler;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNCommitClient;
import org.tmatesoft.svn.core.wc.SVNDiffClient;
import org.tmatesoft.svn.core.wc.SVNEvent;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNRevisionRange;
import org.tmatesoft.svn.core.wc.SVNStatusType;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;

@Service
public class SvnMergeService {

	public void merge(MergeInfoResultItem mergeInfoResultItem, SVNLogEntry svnLogEntry) {
		try {
			SVNURL from = getSvPath(mergeInfoResultItem.getRepository(), mergeInfoResultItem.getFromPath());
			File workingCopy = new File("temp/" + mergeInfoResultItem.getFrom());
			SVNRevisionRange rangeToMerge = getSvnRevisionRange(svnLogEntry.getRevision());

			processMerge(from, workingCopy, rangeToMerge);

		} catch (SVNException e) {
			new RuntimeException(e);
		}
	}

	protected void processMerge(SVNURL from, File workingCopy, SVNRevisionRange rangeToMerge) throws SVNException {
		SVNClientManager clientManager = getSvnClientManager();
		checkoutOrUpdate(workingCopy, from, clientManager.getUpdateClient());
		SVNDiffClient diffClient = clientManager.getDiffClient();

		merge(from, workingCopy, rangeToMerge, diffClient);
		// commit local changes
		SVNCommitClient commitClient = clientManager.getCommitClient();
		commit(workingCopy, commitClient);
	}

	protected void commit(File workingCopy, SVNCommitClient commitClient) throws SVNException {
		commitClient.doCommit(new File[] { workingCopy }, false, "##merge", null, null, false, false, SVNDepth.INFINITY);
	}

	protected static SVNRevisionRange getSvnRevisionRange(long revision) {
		return new SVNRevisionRange(SVNRevision.create(revision - 1), SVNRevision.create(revision));
	}

	protected void merge(SVNURL from, File workingCopy, SVNRevisionRange rangeToMerge, SVNDiffClient diffClient)
			throws SVNException {
		System.err.println("merging from " + from.getPath() + " into " + workingCopy.getAbsolutePath() + " revision "
				+ rangeToMerge.getStartRevision() + "-" + rangeToMerge.getEndRevision());
		diffClient.doMerge(from, rangeToMerge.getStartRevision(), Collections.singleton(rangeToMerge), workingCopy,
				SVNDepth.INFINITY, true, false, true, false);
		diffClient.doMerge(from, rangeToMerge.getStartRevision(), Collections.singleton(rangeToMerge), workingCopy,
				SVNDepth.INFINITY, true, false, false, false);
	}

	protected static SVNURL getSvPath(String repository, String path) throws SVNException {
		SVNURL reposURL = SVNURL.parseURIEncoded(repository);
		return reposURL.appendPath(path, true);
	}

	protected SVNClientManager getSvnClientManager() {
		SVNClientManager svnClientManager = SVNClientManager.newInstance();
		svnClientManager.setEventHandler(getHandler());
		return svnClientManager;
	}

	protected ISVNEventHandler getHandler() {
		return new ISVNEventHandler() {

			@Override
			public void handleEvent(SVNEvent event, double progress) throws SVNException {
				SVNStatusType contentsStatus = event.getContentsStatus();
				event.getPropertiesStatus();
				contentsStatus.getID();
				if (SVNStatusType.CONFLICTED.getID() == contentsStatus.getID()) {
					throw new RuntimeException("CONFLICT");
				}
				System.err.println(event.toString());
			}

			@Override
			public void checkCancelled() throws SVNCancelException {

			}
		};
	}

	protected void checkoutOrUpdate(File wcRoot, SVNURL from, SVNUpdateClient updateClient) throws SVNException {
		updateClient.setIgnoreExternals(false);
		if (wcRoot.exists()) {
			System.err.println("updating " + wcRoot.getAbsolutePath());
			updateClient.doUpdate(wcRoot, SVNRevision.HEAD, SVNDepth.INFINITY, false, false);
		} else {
			System.err.println("checkouting to " + wcRoot.getAbsolutePath());
			updateClient.doCheckout(from, wcRoot, SVNRevision.HEAD, SVNRevision.HEAD, SVNDepth.INFINITY, false);
		}
	}
}
