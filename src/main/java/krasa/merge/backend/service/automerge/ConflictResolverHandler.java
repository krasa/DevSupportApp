package krasa.merge.backend.service.automerge;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.ISVNConflictHandler;
import org.tmatesoft.svn.core.wc.SVNConflictChoice;
import org.tmatesoft.svn.core.wc.SVNConflictDescription;
import org.tmatesoft.svn.core.wc.SVNConflictReason;
import org.tmatesoft.svn.core.wc.SVNConflictResult;
import org.tmatesoft.svn.core.wc.SVNMergeFileSet;

/**
 * @author Vojtech Krasa
 */
public class ConflictResolverHandler implements ISVNConflictHandler {

	public SVNConflictResult handleConflict(SVNConflictDescription conflictDescription) throws SVNException {
		SVNConflictReason reason = conflictDescription.getConflictReason();
		SVNMergeFileSet mergeFiles = conflictDescription.getMergeFiles();

		SVNConflictChoice choice = SVNConflictChoice.MINE_FULL;
		System.out.println("Automatically resolving conflict for " + mergeFiles.getWCFile() + ", choosing "
				+ (choice == SVNConflictChoice.MINE_FULL ? "local file" : "repository file"));
		return new SVNConflictResult(choice, mergeFiles.getResultFile());
	}

}
