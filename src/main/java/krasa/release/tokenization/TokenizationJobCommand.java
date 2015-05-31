package krasa.release.tokenization;

import java.io.*;
import java.util.*;

import krasa.core.backend.LogNamePrefixes;
import krasa.core.backend.utils.MdcUtils;
import krasa.merge.backend.service.automerge.*;
import krasa.release.utls.SvnBranchesCheckouter;

import org.apache.commons.io.filefilter.*;
import org.apache.maven.plugin.MojoExecutionException;
import org.slf4j.*;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.wc.*;

public class TokenizationJobCommand {

	protected static final Logger log = LoggerFactory.getLogger(TokenizationJobCommand.class);
	protected final File tempDir;
	private String commitMessage;
	private Integer id;
	private final TokenizationJobParameters tokenizationJobParameters;
	protected List<String> branchNamePattern;
	private String svnRepoUrl;
	private boolean commit = false;

	public TokenizationJobCommand(Integer id, TokenizationJobParameters tokenizationJobParameters, String svnRepoUrl,
			File tempDir, List<String> branchNamePattern, String commitMessage) {
		this.id = id;
		this.tokenizationJobParameters = tokenizationJobParameters;
		this.svnRepoUrl = svnRepoUrl;
		this.branchNamePattern = branchNamePattern;
		this.tempDir = tempDir;
		this.commitMessage = commitMessage;
	}

	public void setCommit(boolean commit) {
		this.commit = commit;
	}

	public void run() {
		if (tempDir.exists()) {
			throw new IllegalStateException("temp dir already used");
		} else {
			tempDir.mkdirs();
		}
		MdcUtils.putLogName(getLogName());
		log.info("id={}", id);
		log.info("branchNamePattern={}", Arrays.toString(branchNamePattern.toArray()));
		log.info("tokenizationJobParameters={}", tokenizationJobParameters);
		log.info("svnRepoUrl={}", svnRepoUrl);
		log.info("tempDir={}", tempDir);
		log.info("commitMessage={}", commitMessage);
		log.info("Runnning");
		try {
			checkout();
			replace();
			diff();
			commit();
			log.info("Job finished");
		} catch (Exception ioe) {
			log.error("Job error", ioe);
			throw new RuntimeException(ioe);
		} finally {
			MdcUtils.removeLogName();
		}
	}

	protected void replace() throws MojoExecutionException {
		new ReplacementCommand().replace(tokenizationJobParameters.getReplacementDefinitions(),
				tokenizationJobParameters.getPlaceholderReplace(), tempDir);
	}

	protected void diff() throws SVNException {
		File[] list = FileFilterUtils.filter(DirectoryFileFilter.DIRECTORY, tempDir.listFiles());
		for (File workingCopy : list) {
			new DiffCommand().diff(getSvnClientManager(), workingCopy);
		}
	}

	public String getLogName() {
		return LogNamePrefixes.BRANCH_TOKENIZER + id;
	}

	private void checkout() throws SVNException {
		new SvnBranchesCheckouter().checkout(this.svnRepoUrl, tempDir, branchNamePattern);
		File[] files = tempDir.listFiles();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			sb.append("\t").append(file.getName());
			if (i != files.length - 1) {
				sb.append(", \n");
			}
		}
		sb.append("\n");
		log.info("Checkout done, temp folder contains: [\n{}]", sb.toString());

	}

	private void commit() throws IOException, SVNException {
		File[] list = FileFilterUtils.filter(DirectoryFileFilter.DIRECTORY, tempDir.listFiles());
		log.info("Commiting {} directories", list.length);
		for (File workingCopy : list) {
			if (commit) {
				new CommitCommand().commit(getSvnClientManager(), workingCopy, commitMessage);
				log.info("Commited {}", workingCopy.getName());
			} else {
				log.info("Commiting disabled, skipping {}", workingCopy.getName());
			}
		}
	}

	private SVNClientManager getSvnClientManager() {
		SVNClientManager svnClientManager = SVNClientManager.newInstance();
		svnClientManager.setEventHandler(new ISVNEventHandler() {

			@Override
			public void handleEvent(SVNEvent event, double progress) throws SVNException {
				SVNStatusType contentsStatus = event.getContentsStatus();
				if (contentsStatus != null && SVNStatusType.CONFLICTED.getID() == contentsStatus.getID()) {
					throw new RuntimeException("CONFLICT");
				}
				log.info(event.toString());
			}

			@Override
			public void checkCancelled() throws SVNCancelException {

			}
		});
		return svnClientManager;
	}

}
