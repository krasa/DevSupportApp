package krasa.merge.backend.service;

import java.io.File;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNClientManager;

public class SvnMergeServiceTest {

	public static final String SVN_PATH = "NewFolder";
	public static final String SVN_PATH2 = "NewFolder2";

	public static void main(String[] args) throws SVNException {

		SvnMergeService svnMergeService;
		svnMergeService = new SvnMergeService();
		SVNClientManager svnClientManager = svnMergeService.getSvnClientManager();
		SVNURL branch = SvnMergeService.getSvPath("file:///D:/svn/", SVN_PATH);
		SVNURL branch2 = SvnMergeService.getSvPath("file:///D:/svn/", SVN_PATH2);
		File workingCopy = new File("temp/" + SVN_PATH);
		svnMergeService.processMerge(branch2, workingCopy, svnMergeService.getSvnRevisionRange(6));

	}
}
