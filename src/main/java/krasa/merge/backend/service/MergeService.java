package krasa.merge.backend.service;

import java.util.*;

import krasa.core.backend.config.MainConfig;
import krasa.core.backend.dao.UniversalDao;
import krasa.merge.backend.dto.*;
import krasa.merge.backend.service.automerge.*;
import krasa.merge.backend.service.automerge.domain.MergeJob;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tmatesoft.svn.core.*;

@Service
public class MergeService {

	@Autowired
	MergeJobsHolder runningTasks;
	@Autowired
	AutoMergeService autoMergeService;
	@Autowired
	@Qualifier("universalDao")
	UniversalDao universalDao;

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER)
	public void merge(MergeInfoResultItem mergeInfoResultItem, SVNLogEntry svnLogEntry) {
		MergeJob mergeJob = MergeJob.create(mergeInfoResultItem, svnLogEntry, AutoMergeJobMode.ALL);
		universalDao.save(mergeJob);
		autoMergeService.schedule(mergeJob);
	}

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER)
	public void mergeSvnMergeInfoOnly(MergeInfoResultItem mergeInfoResultItem, SVNLogEntry svnLogEntry) {
		MergeJob mergeJob = MergeJob.create(mergeInfoResultItem, svnLogEntry, AutoMergeJobMode.ONLY_MERGE_INFO);
		universalDao.save(mergeJob);
		autoMergeService.schedule(mergeJob);
	}

	public List<MergeJobDto> getRunningMergeJobs() {
		return autoMergeService.getRunningMergeJobs();
	}

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER, readOnly = true)
	public MergeJobDto getMergeJobById(Integer buildJobId) {
		MergeJob byId = universalDao.findById(MergeJob.class, buildJobId);
		return MergeJob.getMergeJobDto(byId);
	}

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER, readOnly = true)
	public List<MergeJobDto> getLastMergeJobs() {
		Set<MergeJob> mergeJobs = new HashSet<>();
		mergeJobs.addAll(universalDao.findLast(10, MergeJob.class));
		return MergeJobDto.translate(mergeJobs);
	}

	public String getDiff(MergeInfoResultItem mergeInfoResultItem, SVNLogEntry svnLogEntry) {
		MergeJob mergeJob = MergeJob.create(mergeInfoResultItem, svnLogEntry, AutoMergeJobMode.DIFF);
		try {
			return mergeJob.getRevisionDiff();
		} catch (SVNException e) {
			throw new RuntimeException(e);
		}
	}

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER)
	public void update(MergeJob mergeJob) {
		universalDao.save(mergeJob);
	}
}
