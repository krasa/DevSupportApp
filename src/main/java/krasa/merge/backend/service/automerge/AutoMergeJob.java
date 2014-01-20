package krasa.merge.backend.service.automerge;

import java.io.File;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import krasa.build.backend.domain.Status;
import krasa.build.backend.execution.ProcessStatus;
import krasa.build.backend.execution.process.ProcessStatusListener;
import krasa.core.backend.domain.AbstractEntity;
import krasa.merge.backend.dto.MergeInfoResultItem;

import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNRevisionRange;

public class AutoMergeJob extends AbstractEntity implements ProcessStatusListener {

	private String from;
	private String to;
	private String fromPath;
	private String toPath;
	private String repository;
	private long revision;
	private AutoMergeJobMode jobMode = AutoMergeJobMode.ALL;
	@Column
	@Enumerated(EnumType.STRING)
	public volatile Status status = Status.PENDING;

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

	public void setFrom(String from) {
		this.from = from;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public void setFromPath(String fromPath) {
		this.fromPath = fromPath;
	}

	public void setToPath(String toPath) {
		this.toPath = toPath;
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}

	public void setRevision(long revision) {
		this.revision = revision;
	}

	public AutoMergeJobMode getJobMode() {
		return jobMode;
	}

	public void setJobMode(AutoMergeJobMode jobMode) {
		this.jobMode = jobMode;
	}

	public String getFrom() {
		return from;
	}

	public String getTo() {
		return to;
	}

	public String getFromPath() {
		return fromPath;
	}

	public String getToPath() {
		return toPath;
	}

	public String getRepository() {
		return repository;
	}

	public long getRevision() {
		return revision;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public void onStatusChanged(ProcessStatus processStatus) {

	}

	public SVNURL getFromUrl() throws SVNException {
		SVNURL reposURL = SVNURL.parseURIEncoded(getRepository());
		return reposURL.appendPath(getFromPath(), true);
	}

	public SVNURL getToUrl() throws SVNException {
		SVNURL reposURL = SVNURL.parseURIEncoded(getRepository());
		return reposURL.appendPath(getToPath(), true);
	}

	public SVNRevisionRange getSvnRevisionRange() {
		long revision = getRevision();
		return new SVNRevisionRange(SVNRevision.create(revision - 1), SVNRevision.create(revision));
	}

	public File getWorkingCopy() {
		return new File("target/temp/" + getTo());
	}
}
