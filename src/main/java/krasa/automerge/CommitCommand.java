package krasa.automerge;

import java.io.File;

import krasa.core.backend.EnvironmentHolder;

import org.slf4j.*;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.auth.BasicAuthenticationManager;
import org.tmatesoft.svn.core.wc.*;

public class CommitCommand {

	protected static final Logger log = LoggerFactory.getLogger(CommitCommand.class);

	public void commit(SVNClientManager clientManager, File workingCopy, String commitMessage) throws SVNException {
		if (!SVNWCUtil.isWorkingCopyRoot(workingCopy)) {
			throw new IllegalArgumentException("Not working copy root:" + workingCopy.getAbsolutePath());
		}
		SVNCommitClient commitClient = clientManager.getCommitClient();
		String password = EnvironmentHolder.getEnvironment().getProperty("svn.password");
		String client = EnvironmentHolder.getEnvironment().getProperty("svn.client", password);
		clientManager.setAuthenticationManager(new BasicAuthenticationManager(client, password));
		commitClient.doCommit(new File[] { workingCopy }, false, commitMessage, null, null, false, false,
				SVNDepth.INFINITY);
	}

}
