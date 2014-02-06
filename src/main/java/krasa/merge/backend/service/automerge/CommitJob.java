package krasa.merge.backend.service.automerge;

import java.io.File;

import krasa.core.backend.utils.LogOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNCommitClient;
import org.tmatesoft.svn.core.wc.SVNDiffClient;
import org.tmatesoft.svn.core.wc.SVNRevision;

public class CommitJob {
	protected static final Logger log = LoggerFactory.getLogger(CommitJob.class);

	public void commit(SVNClientManager clientManager, File workingCopy, String commitMessage) throws SVNException {
		SVNDiffClient diffClient = clientManager.getDiffClient();
		diffClient.setShowCopiesAsAdds(true);
		diffClient.doDiff(workingCopy, SVNRevision.UNDEFINED, SVNRevision.WORKING, SVNRevision.COMMITTED,
				SVNDepth.INFINITY, true, new LogOutputStream(log), null);

		SVNCommitClient commitClient = clientManager.getCommitClient();

		commit(workingCopy, commitClient, commitMessage);
	}

	protected void commit(File workingCopy, SVNCommitClient commitClient, String commitMessage1) throws SVNException {
		commitClient.doCommit(new File[] { workingCopy }, false, commitMessage1, null, null, false, false,
				SVNDepth.INFINITY);
	}
}
