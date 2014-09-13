package krasa.merge.backend.service;

import krasa.merge.backend.service.automerge.*;
import krasa.merge.backend.service.automerge.domain.MergeJob;

import org.tmatesoft.svn.core.SVNException;

public class AutoMergeProcessTest {

	public static final String SVN_PATH = "NewFolder";
	public static final String SVN_PATH2 = "NewFolder2";
	public static final String REPOSITORY = "file:///D:/svn/";

	public static void main(String[] args) throws SVNException {
		MergeJob mergeJob = new MergeJob(SVN_PATH, SVN_PATH2, SVN_PATH, SVN_PATH2, REPOSITORY, 6, "author",
				AutoMergeJobMode.ALL);

		AutoMergeProcess svnMergeService = new AutoMergeProcess(mergeJob, null);
		// mergeService.processMerge(autoMergeJob., workingCopy, autoMergeJob.getSvnRevisionRange());

	}
}
