package krasa.merge.backend.service.automerge;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNCommitClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class CommitCommand {

	protected static final Logger log = LoggerFactory.getLogger(CommitCommand.class);

	public void commit(SVNClientManager clientManager, File workingCopy, String commitMessage) throws SVNException {
		if (!SVNWCUtil.isWorkingCopyRoot(workingCopy)) {
			throw new IllegalArgumentException("Not working copy root:" + workingCopy.getAbsolutePath());
		}
		SVNCommitClient commitClient = clientManager.getCommitClient();
		commitClient.doCommit(new File[] { workingCopy }, false, commitMessage, null, null, false, false,
				SVNDepth.INFINITY);
	}

}
