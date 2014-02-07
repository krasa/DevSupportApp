package krasa.merge.backend.service.automerge;

import java.io.File;

import krasa.core.backend.utils.LogOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNDiffClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

public class DiffCommand {

	protected static final Logger log = LoggerFactory.getLogger(DiffCommand.class);

	public void diff(SVNClientManager clientManager, File workingCopy) throws SVNException {
		log.info("Diff " + workingCopy.getAbsolutePath() + ":");
		if (!SVNWCUtil.isWorkingCopyRoot(workingCopy)) {
			throw new IllegalArgumentException("Not working copy root:" + workingCopy.getAbsolutePath());
		}
		SVNDiffClient diffClient = clientManager.getDiffClient();
		diffClient.setShowCopiesAsAdds(true);
		diffClient.doDiff(workingCopy, SVNRevision.UNDEFINED, SVNRevision.WORKING, SVNRevision.COMMITTED,
				SVNDepth.INFINITY, true, new LogOutputStream(log), null);
		log.info("Diff end\n");

		// final SvnOperationFactory svnOperationFactory = new SvnOperationFactory();
		//
		// final SvnDiff diff = svnOperationFactory.createDiff();
		// diff.setSource(SvnTarget.fromFile(workingCopy, SVNRevision.BASE), SVNRevision.UNDEFINED,
		// SVNRevision.COMMITTED);
		// diff.setDiffGenerator(new SvnDiffGenerator());
		// diff.setOutput(new LogOutputStream(log));
		// diff.run();

	}
}
