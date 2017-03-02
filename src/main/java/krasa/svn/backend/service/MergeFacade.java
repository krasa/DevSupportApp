package krasa.svn.backend.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;

import krasa.automerge.AutoMergeJobMode;
import krasa.automerge.AutoMergeService;
import krasa.automerge.MergeJobsHolder;
import krasa.automerge.domain.MergeJob;
import krasa.core.backend.config.MainConfig;
import krasa.core.backend.dao.UniversalDao;
import krasa.core.frontend.web.CookieUtils;
import krasa.svn.backend.dto.MergeInfoResultItem;
import krasa.svn.backend.dto.MergeJobDto;

@Service
public class MergeFacade {

	@Autowired
	MergeJobsHolder runningTasks;
	@Autowired
	AutoMergeService autoMergeService;
	@Autowired
	@Qualifier("universalDao")
	UniversalDao universalDao;

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER)
	public void merge(MergeInfoResultItem mergeInfoResultItem, SVNLogEntry svnLogEntry) {
		String userName = CookieUtils.getCookie_userName();
		MergeJob mergeJob = MergeJob.create(mergeInfoResultItem, svnLogEntry, AutoMergeJobMode.ALL, userName);
		universalDao.save(mergeJob);
		autoMergeService.schedule(mergeJob);
	}

	@Transactional(value = MainConfig.HSQLDB_TX_MANAGER)
	public void mergeSvnMergeInfoOnly(MergeInfoResultItem mergeInfoResultItem, SVNLogEntry svnLogEntry) {
		String userName = CookieUtils.getCookie_userName();
		MergeJob mergeJob = MergeJob.create(mergeInfoResultItem, svnLogEntry, AutoMergeJobMode.ONLY_MERGE_INFO, userName);
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
		mergeJobs.addAll(universalDao.findLast(30, MergeJob.class));
		return MergeJobDto.translate(mergeJobs);
	}

	public String getDiff(MergeInfoResultItem mergeInfoResultItem, SVNLogEntry svnLogEntry) {
		MergeJob mergeJob = MergeJob.create(mergeInfoResultItem, svnLogEntry, AutoMergeJobMode.DIFF, "vojtitko");
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
