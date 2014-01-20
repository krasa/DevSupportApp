package krasa.merge.backend.service;

import krasa.merge.backend.service.automerge.AutoMergeJob;
import krasa.merge.backend.service.automerge.AutoMergeJobMode;
import krasa.merge.backend.service.automerge.AutoMergeProcess;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.SVNClientManager;

public class AutoMergeProcessTest {

	public static final String SVN_PATH = "NewFolder";
	public static final String SVN_PATH2 = "NewFolder2";
	public static final String REPOSITORY = "file:///D:/svn/";

	public static void main(String[] args) throws SVNException {
		AutoMergeJob autoMergeJob = new AutoMergeJob(SVN_PATH, SVN_PATH2, SVN_PATH, SVN_PATH2, REPOSITORY, 6,
				AutoMergeJobMode.ALL);

		AutoMergeProcess svnMergeService = new AutoMergeProcess(autoMergeJob, null);
		SVNClientManager svnClientManager = svnMergeService.getSvnClientManager(autoMergeJob);
		// mergeService.processMerge(autoMergeJob., workingCopy, autoMergeJob.getSvnRevisionRange());

	}
}
