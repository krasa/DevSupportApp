package krasa.merge.backend.service.automerge;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.util.Collections;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNCancelException;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
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

public class AutoMergeProcess implements Runnable {

	protected final Logger log = LoggerFactory.getLogger(getClass());

	protected StringBuilder processLog = new StringBuilder();
	private AutoMergeJob autoMergeJob;
	private AutoMergeExecutor autoMergeExecutor;

	public AutoMergeProcess(@NotNull AutoMergeJob autoMergeJob, AutoMergeExecutor autoMergeExecutor) {
		this.autoMergeJob = autoMergeJob;
		this.autoMergeExecutor = autoMergeExecutor;
	}

	public AutoMergeJob getAutoMergeJob() {
		return autoMergeJob;
	}

	@Override
	public void run() {
		try {
			run(autoMergeJob);
			autoMergeExecutor.jobFinished(this, null);
		} catch (Throwable e) {
			final ByteArrayOutputStream out = new ByteArrayOutputStream();
			e.printStackTrace(new PrintStream(out));
			append(out.toString());
			autoMergeExecutor.jobFinished(this, e);
		}
	}

	protected void append(String str) {
		log.debug(str);
		appendNewLine();
		processLog.append(str);
	}

	protected void appendNewLine() {
		processLog.append("\n");
	}

	protected void run(AutoMergeJob autoMergeJob) {
		SVNClientManager clientManager = getSvnClientManager(autoMergeJob);
		File workingCopy = autoMergeJob.getWorkingCopy();
		try {
			SVNURL from = autoMergeJob.getFromUrl();
			SVNURL to = autoMergeJob.getToUrl();

			SVNRevisionRange rangeToMerge = autoMergeJob.getSvnRevisionRange();

			cleanup(clientManager, workingCopy);

			checkoutOrUpdate(workingCopy, to, clientManager.getUpdateClient());
			SVNDiffClient diffClient = getDiffClient(clientManager, autoMergeJob);

			merge(from, workingCopy, rangeToMerge, diffClient);

			diffClient.setShowCopiesAsAdds(true);
			diffClient.doDiff(workingCopy, SVNRevision.UNDEFINED, SVNRevision.WORKING, SVNRevision.COMMITTED,
					SVNDepth.INFINITY, true, System.out, null);

			SVNCommitClient commitClient = clientManager.getCommitClient();
			commit(autoMergeJob, workingCopy, commitClient, diffClient);
		} catch (SVNException e) {
			throw new RuntimeException(e);
		}
	}

	private void cleanup(SVNClientManager clientManager, File workingCopy) throws SVNException {
		if (workingCopy.exists()) {
			clientManager.getWCClient().doCleanup(workingCopy);
			clientManager.getWCClient().doRevert(new File[] { workingCopy }, SVNDepth.INFINITY, null);
		}
	}

	private SVNDiffClient getDiffClient(SVNClientManager clientManager, AutoMergeJob autoMergeJob) {
		final SVNDiffClient diffClient = clientManager.getDiffClient();
		// if (autoMergeJob.getJobMode() == AutoMergeJobMode.ONLY_SVN_MERGE_INFO) {
		// DefaultSVNOptions options = (DefaultSVNOptions) diffClient.getOptions();
		// //This way we set a conflict handler which will automatically resolve conflicts for those
		// //cases that we would like
		// options.setConflictHandler(new ConflictResolverHandler());
		// }
		return diffClient;
	}

	protected void commit(AutoMergeJob autoMergeJob, File workingCopy, SVNCommitClient commitClient,
			SVNDiffClient diffClient) throws SVNException {
		String commitMessage;
		if (autoMergeJob.getJobMode() == AutoMergeJobMode.ONLY_MERGE_INFO) {
			commitMessage = "##merge mergeinfo";
		} else {
			commitMessage = "##merge";
		}
		commitClient.doCommit(new File[] { workingCopy }, false, commitMessage, null, null, false, false,
				SVNDepth.INFINITY);
	}

	protected void merge(SVNURL from, File workingCopy, SVNRevisionRange rangeToMerge, SVNDiffClient diffClient)
			throws SVNException {
		final boolean recordOnly = autoMergeJob.getJobMode() == AutoMergeJobMode.ONLY_MERGE_INFO;
		append("merging from " + from.getPath() + " into " + workingCopy.getAbsolutePath() + " revision "
				+ rangeToMerge.getStartRevision() + "-" + rangeToMerge.getEndRevision() + " recordOnly=" + recordOnly);

		diffClient.doMerge(from, SVNRevision.HEAD, Collections.singleton(rangeToMerge), workingCopy, SVNDepth.INFINITY,
				true, false, true, recordOnly);
		diffClient.doMerge(from, SVNRevision.HEAD, Collections.singleton(rangeToMerge), workingCopy, SVNDepth.INFINITY,
				true, false, false, recordOnly);
	}

	public SVNClientManager getSvnClientManager(AutoMergeJob autoMergeJob) {
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
				append(event.toString());
			}

			@Override
			public void checkCancelled() throws SVNCancelException {

			}
		};
	}

	protected void checkoutOrUpdate(File wcRoot, SVNURL svnurl, SVNUpdateClient updateClient) throws SVNException {
		updateClient.setIgnoreExternals(false);
		if (wcRoot.exists()) {
			appendNewLine();
			append("updating " + wcRoot.getAbsolutePath());
			updateClient.doUpdate(wcRoot, SVNRevision.HEAD, SVNDepth.INFINITY, false, false);
		} else {
			appendNewLine();
			append("checkouting to " + wcRoot.getAbsolutePath());
			updateClient.doCheckout(svnurl, wcRoot, SVNRevision.HEAD, SVNRevision.HEAD, SVNDepth.INFINITY, false);
		}
	}
}
