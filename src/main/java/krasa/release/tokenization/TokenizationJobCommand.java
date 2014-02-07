package krasa.release.tokenization;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import krasa.core.backend.utils.MdcUtils;
import krasa.merge.backend.service.automerge.CommitCommand;
import krasa.merge.backend.service.automerge.DiffCommand;
import krasa.release.utls.SvnBranchesCheckouter;

import org.apache.commons.io.Charsets;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.FileFilterUtils;
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

public class TokenizationJobCommand {

	protected static final Logger log = LoggerFactory.getLogger(TokenizationJobCommand.class);
	protected final File tempDir;
	private final FileUtils fileUtils = new FileUtils();
	private final TokenizationJobParameters tokenizationJobParameters;
	protected String branchNamePattern;
	private String svnRepoUrl;

	public TokenizationJobCommand(TokenizationJobParameters tokenizationJobParameters, String svnRepoUrl, File tempDir,
			final String branchNamePattern) {
		this.tokenizationJobParameters = tokenizationJobParameters;
		this.svnRepoUrl = svnRepoUrl;
		this.branchNamePattern = branchNamePattern;
		this.tempDir = tempDir;
	}

	public void run() {
		MdcUtils.putLogName(getLogName());
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

	protected void diff() throws SVNException {
		diff(getSvnClientManager(), tempDir);
	}

	public String getLogName() {
		return "branchTokenizer_" + tempDir.getName();
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
		log.info("Commiting");
		commit(getSvnClientManager(), tempDir);
	}

	protected void replace() throws MojoExecutionException {
		for (ReplacementDefinition replacementDefinition : tokenizationJobParameters.getReplacementDefinitions()) {
			StringBuilder sb = new StringBuilder();
			sb.append("Includes:[\n");
			for (int i = 0; i < replacementDefinition.getIncludes().size(); i++) {
				String include = replacementDefinition.getIncludes().get(i);
				sb.append("\t").append(include);
				if (i != replacementDefinition.getIncludes().size() - 1) {
					sb.append(", \n");
				}
			}
			sb.append("\n]");
			log.info(sb.toString());

			ReplacerMojo replacerMojo = new ReplacerMojo();
			replacerMojo.setLog(new ReplacerLog());
			replacerMojo.setBasedir(tempDir.getAbsolutePath());
			replacerMojo.getIncludes().addAll(replacementDefinition.getIncludes());
			replacerMojo.setReplacements(getReplacements(replacementDefinition));
			log.info("Executing replace");
			replacerMojo.execute();
		}
	}

	private ArrayList<Replacement> getReplacements(ReplacementDefinition replacementDefinition) {
		ArrayList<Replacement> replacements = new ArrayList<>();
		for (krasa.release.tokenization.Replacement replacement : replacementDefinition.getReplacements()) {
			replacements.add(new Replacement(fileUtils, replacePlaceholders(replacement.getToken()),
					replacePlaceholders(replacement.getValue()), false, null, Charsets.UTF_8.name()));
		}
		StringBuilder sb = new StringBuilder();
		sb.append("Replacements:[\n");
		for (int i = 0; i < replacements.size(); i++) {
			Replacement replacement = replacements.get(i);
			sb.append("\t").append(replacement.getToken()).append(" -> ").append(replacement.getValue());
			if (i != replacements.size() - 1) {
				sb.append(", \n ");
			}
		}
		sb.append("\n]");
		log.info(sb.toString());

		return replacements;
	}

	private String replacePlaceholders(String value) {
		return StrSubstitutor.replace(value, tokenizationJobParameters.getPlaceholderReplace());
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

	private void diff(SVNClientManager svnClientManager, File baseDir) throws SVNException {
		File[] list = FileFilterUtils.filter(DirectoryFileFilter.DIRECTORY, baseDir.listFiles());
		for (File s : list) {
			new DiffCommand().diff(svnClientManager, s);
		}
	}

	private void commit(SVNClientManager svnClientManager, File baseDir) throws SVNException {
		String commitMessage = "##config version";
		File[] list = FileFilterUtils.filter(DirectoryFileFilter.DIRECTORY, baseDir.listFiles());
		for (File s : list) {
			new CommitCommand().commit(svnClientManager, s, commitMessage);
		}
	}

}
