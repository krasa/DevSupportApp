package krasa.merge.backend.service.automerge;

import com.google.gag.annotation.remark.Win;
import com.google.gag.enumeration.Outcome;
import krasa.build.backend.domain.Status;
import krasa.build.backend.execution.ProcessStatus;
import krasa.build.backend.execution.process.ProcessStatusListener;
import krasa.core.backend.domain.AbstractEntity;
import krasa.merge.backend.dto.MergeInfoResultItem;
import krasa.merge.backend.dto.MergeJobDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNCancelException;
import org.tmatesoft.svn.core.SVNDepth;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.ISVNEventHandler;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNCommitClient;
import org.tmatesoft.svn.core.wc.SVNDiffClient;
import org.tmatesoft.svn.core.wc.SVNEvent;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNRevisionRange;
import org.tmatesoft.svn.core.wc.SVNStatusType;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

@Entity
@Access(AccessType.FIELD)
public class AutoMergeJob extends AbstractEntity implements ProcessStatusListener {
	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Column
	@Enumerated(EnumType.STRING)
	public volatile Status status = Status.PENDING;
	protected StringBuilder processLog = new StringBuilder();
	private String from;
	private String to;
	private String fromPath;
	private String toPath;
	private String repository;
	private long revision;
	private AutoMergeJobMode jobMode = AutoMergeJobMode.ALL;

	public AutoMergeJob(String from, String to, String fromPath, String toPath, String repository, long revision,
						final AutoMergeJobMode all) {
		this.from = from;
		this.to = to;
		this.fromPath = fromPath;
		this.toPath = toPath;
		this.repository = repository;
		this.revision = revision;
		this.jobMode = all;
	}

	public static AutoMergeJob create(MergeInfoResultItem mergeInfoResultItem, SVNLogEntry svnLogEntry,
									  final AutoMergeJobMode mergeJobMode) {
		String from = mergeInfoResultItem.getFrom();
		String to = mergeInfoResultItem.getTo();
		String fromPath = mergeInfoResultItem.getFromPath();
		String toPath = mergeInfoResultItem.getToPath();
		String repository = mergeInfoResultItem.getRepository();
		long revision = svnLogEntry.getRevision();
		return new AutoMergeJob(from, to, fromPath, toPath, repository, revision, mergeJobMode);
	}

	protected static void sort(List<AutoMergeJob> autoMergeJobs) {
		Collections.sort(autoMergeJobs, new Comparator<AutoMergeJob>() {

			@Override
			public int compare(AutoMergeJob o1, AutoMergeJob o2) {
				Long revision = o1.revision;
				return revision.compareTo(o2.revision);
			}
		});
	}

	public static MergeJobDto getMergeJobDto(AutoMergeProcess autoMergeProcess) {
		final AutoMergeJob mergeJob = autoMergeProcess.getAutoMergeJob();
		MergeJobDto e = new MergeJobDto();
		e.setFrom(mergeJob.from);
		e.setTo(mergeJob.to);
		e.setStatus(mergeJob.status.name());
		e.setRevision(mergeJob.revision);
		return e;
	}

	@Win(Outcome.EPIC)
	public void merge() {
		SVNClientManager clientManager = getSvnClientManager();
		File workingCopy = getWorkingCopy();
		try {
			SVNURL from = getFromUrl();
			SVNURL to = getToUrl();

			SVNRevisionRange rangeToMerge = getSvnRevisionRange();

			cleanup(clientManager, workingCopy);

			checkoutOrUpdate(workingCopy, to, clientManager.getUpdateClient());
			SVNDiffClient diffClient = getDiffClient(clientManager);

			merge(from, workingCopy, rangeToMerge, diffClient);

			diffClient.setShowCopiesAsAdds(true);
			diffClient.doDiff(workingCopy, SVNRevision.UNDEFINED, SVNRevision.WORKING, SVNRevision.COMMITTED,
					SVNDepth.INFINITY, true, System.out, null);

			SVNCommitClient commitClient = clientManager.getCommitClient();

			commit(clientManager, workingCopy, from, rangeToMerge, commitClient);
		} catch (SVNException e) {
			throw new RuntimeException(e);
		}
	}

	@Win
	public String getRevisionDiff() throws SVNException {
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		SVNClientManager clientManager = getSvnClientManager();
		final SVNDiffClient diffClient = getDiffClient(clientManager);
		diffClient.doDiff(getFromUrl(), getSvnRevisionRange().getStartRevision(), getFromUrl(), getSvnRevisionRange().getEndRevision(),
				SVNDepth.INFINITY, true, out);
		return out.toString();
	}

	@Override
	public void onStatusChanged(ProcessStatus processStatus) {

	}

	public SVNURL getFromUrl() throws SVNException {
		SVNURL reposURL = SVNURL.parseURIEncoded(repository);
		return reposURL.appendPath(fromPath, true);
	}

	public SVNURL getToUrl() throws SVNException {
		SVNURL reposURL = SVNURL.parseURIEncoded(repository);
		return reposURL.appendPath(getToPath(), true);
	}

	public SVNRevisionRange getSvnRevisionRange() {
		long revision = this.revision;
		return new SVNRevisionRange(SVNRevision.create(revision - 1), SVNRevision.create(revision));
	}

	public File getWorkingCopy() {
		return new File("target/temp/" + to);
	}

	protected void append(String str) {
		log.debug(str);
		appendNewLine();
		processLog.append(str);
	}


	protected void appendNewLine() {
		processLog.append("\n");
	}

	protected void commit(SVNClientManager clientManager, File workingCopy, SVNURL from, SVNRevisionRange rangeToMerge, SVNCommitClient commitClient) throws SVNException {
		String commitMessage = getCommitMessage(clientManager, from, rangeToMerge);
		commitClient.doCommit(new File[]{workingCopy}, false, commitMessage, null, null, false, false,
				SVNDepth.INFINITY);
	}

	private String getCommitMessage(SVNClientManager clientManager, SVNURL from, SVNRevisionRange rangeToMerge) throws SVNException {
		final MyISVNLogEntryHandler handler = new MyISVNLogEntryHandler();
		clientManager.getLogClient().doLog(from, new String[]{}, getSvnRevisionRange().getEndRevision(), rangeToMerge.getEndRevision(), rangeToMerge.getEndRevision(), false, false, 1, handler);
		String message = handler.message;
		String commitMessage;
		if (jobMode == AutoMergeJobMode.ONLY_MERGE_INFO) {
			commitMessage = "##merge mergeinfo";
		} else {
			final int beginIndex = message.lastIndexOf("\n");
			if (beginIndex > 0) {
				message = message.substring(beginIndex);
			}
			commitMessage = "##merge " + message;
		}
		return commitMessage;
	}

	private void cleanup(SVNClientManager clientManager, File workingCopy) throws SVNException {
		if (workingCopy.exists()) {
			clientManager.getWCClient().doCleanup(workingCopy);
			clientManager.getWCClient().doRevert(new File[]{workingCopy}, SVNDepth.INFINITY, null);
		}
	}

	private SVNDiffClient getDiffClient(SVNClientManager clientManager) {
		final SVNDiffClient diffClient = clientManager.getDiffClient();
		// if (getJobMode() == AutoMergeJobMode.ONLY_SVN_MERGE_INFO) {
		// DefaultSVNOptions options = (DefaultSVNOptions) diffClient.getOptions();
		// //This way we set a conflict handler which will automatically resolve conflicts for those
		// //cases that we would like
		// options.setConflictHandler(new ConflictResolverHandler());
		// }
		return diffClient;
	}

	protected void merge(SVNURL from, File workingCopy, SVNRevisionRange rangeToMerge, SVNDiffClient diffClient)
			throws SVNException {
		final boolean recordOnly = jobMode == AutoMergeJobMode.ONLY_MERGE_INFO;
		append("merging from " + from.getPath() + " into " + workingCopy.getAbsolutePath() + " revision "
				+ rangeToMerge.getStartRevision() + "-" + rangeToMerge.getEndRevision() + " recordOnly=" + recordOnly);

		diffClient.doMerge(from, SVNRevision.HEAD, Collections.singleton(rangeToMerge), workingCopy, SVNDepth.INFINITY,
				true, false, true, recordOnly);
		diffClient.doMerge(from, SVNRevision.HEAD, Collections.singleton(rangeToMerge), workingCopy, SVNDepth.INFINITY,
				true, false, false, recordOnly);
	}

	public SVNClientManager getSvnClientManager() {
		SVNClientManager svnClientManager = SVNClientManager.newInstance();
		svnClientManager.setEventHandler(getHandler());
		return svnClientManager;
	}

	protected ISVNEventHandler getHandler() {
		return new ISVNEventHandler() {

			@Override
			public void handleEvent(SVNEvent event, double progress) throws SVNException {
				SVNStatusType contentsStatus = event.getContentsStatus();
				event.getPropertiesStatus();
				contentsStatus.getID();
				if (SVNStatusType.CONFLICTED.getID() == contentsStatus.getID()) {
					throw new RuntimeException("CONFLICT");
				}
				append(event.toString());
			}

			@Override
			public void checkCancelled() throws SVNCancelException {

			}
		};
	}

	protected void checkoutOrUpdate(File wcRoot, SVNURL svnurl, SVNUpdateClient updateClient) throws SVNException {
		updateClient.setIgnoreExternals(false);
		if (wcRoot.exists()) {
			appendNewLine();
			append("updating " + wcRoot.getAbsolutePath());
			updateClient.doUpdate(wcRoot, SVNRevision.HEAD, SVNDepth.INFINITY, false, false);
		} else {
			appendNewLine();
			append("checkouting to " + wcRoot.getAbsolutePath());
			updateClient.doCheckout(svnurl, wcRoot, SVNRevision.HEAD, SVNRevision.HEAD, SVNDepth.INFINITY, false);
		}
	}

	protected boolean isSameDestination(AutoMergeJob autoMergeJob) {
		return autoMergeJob.getToPath().equals(getToPath());
	}

	public String getToPath() {
		return toPath;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	private static class MyISVNLogEntryHandler implements ISVNLogEntryHandler {

		protected String message;

		@Override
		public void handleLogEntry(SVNLogEntry logEntry) throws SVNException {
			message = logEntry.getMessage();
		}
	}

}
