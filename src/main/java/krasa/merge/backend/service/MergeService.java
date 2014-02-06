package krasa.merge.backend.service;

import java.util.List;

import krasa.merge.backend.dto.MergeInfoResultItem;
import krasa.merge.backend.dto.MergeJobDto;
import krasa.merge.backend.service.automerge.AutoMergeExecutor;
import krasa.merge.backend.service.automerge.AutoMergeJob;
import krasa.merge.backend.service.automerge.AutoMergeJobMode;
import krasa.merge.backend.service.automerge.AutoMergeProcess;
import krasa.merge.backend.service.automerge.MergeJobsHolder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;

@Service
public class MergeService {

	@Autowired
	MergeJobsHolder runningTasks;
	@Autowired
	AutoMergeExecutor autoMergeExecutor;

	public void merge(MergeInfoResultItem mergeInfoResultItem, SVNLogEntry svnLogEntry) {
		AutoMergeJob autoMergeJob = AutoMergeJob.create(mergeInfoResultItem, svnLogEntry, AutoMergeJobMode.ALL);
		autoMergeExecutor.schedule(autoMergeJob);
	}

	public void mergeSvnMergeInfoOnly(MergeInfoResultItem mergeInfoResultItem, SVNLogEntry svnLogEntry) {
		AutoMergeJob autoMergeJob = AutoMergeJob.create(mergeInfoResultItem, svnLogEntry,
				AutoMergeJobMode.ONLY_MERGE_INFO);
		autoMergeExecutor.schedule(autoMergeJob);
	}

	public List<MergeJobDto> getRunningMergeJobs() {
		return autoMergeExecutor.getRunningMergeJobs();
	}

	public MergeJobDto getMergeJobById(Integer buildJobId) {
		return null;
	}

	public List<MergeJobDto> getLastFinishedJobs() {
		final List<AutoMergeProcess> lastFinished = runningTasks.getLastFinished();
		return MergeJobDto.translate(lastFinished);

	}

	public String getDiff(MergeInfoResultItem mergeInfoResultItem, SVNLogEntry svnLogEntry) {
		AutoMergeJob autoMergeJob = AutoMergeJob.create(mergeInfoResultItem, svnLogEntry, AutoMergeJobMode.DIFF);
		try {
			return autoMergeJob.getRevisionDiff();
		} catch (SVNException e) {
			throw new RuntimeException(e);
		}
	}
}
