package krasa.release.tokenization;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import krasa.core.backend.utils.MdcUtils;
import krasa.merge.backend.service.automerge.CommitJob;
import krasa.release.SvnBranchesCheckouter;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.maven.plugin.MojoExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNCancelException;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.wc.ISVNEventHandler;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNEvent;
import org.tmatesoft.svn.core.wc.SVNStatusType;

import com.google.code.maven_replacer_plugin.Replacement;
import com.google.code.maven_replacer_plugin.ReplacerMojo;
import com.google.code.maven_replacer_plugin.file.FileUtils;

public class BranchesTokenReplacementJob {

	protected static final Logger log = LoggerFactory.getLogger(BranchesTokenReplacementJob.class);
	protected final File uniqueTempDir;
	private final FileUtils fileUtils = new FileUtils();
	private final BranchTokenReplacementJobParameters branchTokenReplacementJobParameters;
	protected String branchNameSuffix;
	private String svnRepoUrl;

	public BranchesTokenReplacementJob(BranchTokenReplacementJobParameters branchTokenReplacementJobParameters,
			String svnRepoUrl, File tempDir, final String branchNameSuffix) {
		this.branchTokenReplacementJobParameters = branchTokenReplacementJobParameters;
		this.svnRepoUrl = svnRepoUrl;
		this.branchNameSuffix = branchNameSuffix;
		uniqueTempDir = getUniqueTempDir(tempDir);
	}

	private File getUniqueTempDir(File tempDir) {
		int i = 0;
		File file = new File(tempDir.getAbsolutePath(), branchNameSuffix);
		while (file.exists()) {
			file = new File(tempDir.getAbsolutePath(), branchNameSuffix + "_" + ++i);
		}
		file.mkdirs();
		return file;
	}

	protected void run() {
		MdcUtils.putLogName(getLogName());
		try {
			checkout();
			replace();
			commit();
		} catch (Exception ioe) {
			throw new RuntimeException(ioe);
		} finally {
			MdcUtils.removeLogName();
		}
	}

	public String getLogName() {
		return "branchTokenizer_" + uniqueTempDir.getName();
	}

	private void checkout() throws SVNException {
		new SvnBranchesCheckouter().checkout(this.svnRepoUrl, uniqueTempDir, branchNameSuffix);
	}

	private void commit() throws IOException, SVNException {
		log.info("Commiting");
		commit(getSvnClientManager(), uniqueTempDir);
	}

	protected void replace() throws MojoExecutionException {
		for (ReplacementDefinition replacementDefinition : branchTokenReplacementJobParameters.getReplacementDefinitions()) {
			ReplacerMojo replacerMojo = new ReplacerMojo();
			replacerMojo.setBasedir(uniqueTempDir.getAbsolutePath());
			replacerMojo.getIncludes().addAll(replacementDefinition.getIncludes());
			ArrayList<Replacement> replacements = new ArrayList<>();
			for (krasa.release.tokenization.Replacement replacement : replacementDefinition.getReplacements()) {
				replacements.add(new Replacement(fileUtils, replacePlaceholders(replacement.getToken()),
						replacePlaceholders(replacement.getValue()), false, null, Charsets.UTF_8.name()));
			}
			log.info("Replacements:");
			for (Replacement replacement : replacements) {
				log.info("{} - {}", replacement.getToken(), replacement.getValue());
			}
			replacerMojo.setReplacements(replacements);
			replacerMojo.execute();
		}
	}

	private String replacePlaceholders(String value) {
		return StrSubstitutor.replace(value, branchTokenReplacementJobParameters.getPlaceholderReplace());
	}

	private SVNClientManager getSvnClientManager() {
		SVNClientManager svnClientManager = SVNClientManager.newInstance();
		svnClientManager.setEventHandler(new ISVNEventHandler() {

			@Override
			public void handleEvent(SVNEvent event, double progress) throws SVNException {
				SVNStatusType contentsStatus = event.getContentsStatus();
				event.getPropertiesStatus();
				contentsStatus.getID();
				if (SVNStatusType.CONFLICTED.getID() == contentsStatus.getID()) {
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

	private void commit(SVNClientManager svnClientManager, File baseDir) throws SVNException {
		String commitMessage = "##config version";
		String[] list = baseDir.list(DirectoryFileFilter.INSTANCE);
		for (String s : list) {
			new CommitJob().commit(svnClientManager, new File(s), commitMessage);
		}
	}
}
