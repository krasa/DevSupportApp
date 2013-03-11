package krasa.merge.backend.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import krasa.core.backend.service.GlobalSettingsProvider;
import krasa.merge.backend.dao.SvnFolderDAO;
import krasa.merge.backend.domain.SvnFolder;
import krasa.merge.backend.dto.MergeInfoResult;
import krasa.merge.backend.dto.MergeInfoResultItem;
import krasa.merge.backend.service.conventions.ConventionsStrategyHolder;
import krasa.merge.backend.svn.SvnMergeInfoProvider;
import krasa.merge.backend.svn.connection.SVNConnector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;

/**
 * @author Vojtech Krasa
 */
@Service
public class MergeInfoServiceImpl implements MergeInfoService {
	protected final Logger log = LoggerFactory.getLogger(getClass());
	@Autowired
	private SvnFolderDAO svnFolderDAO;

	@Autowired
	private SVNConnector svnConnection;

	@Autowired
	private GlobalSettingsProvider globalSettingsProvider;

	public MergeInfoResult findMerges(List<SvnFolder> branches) {

		MergeInfoResult mergeInfoResult = new MergeInfoResult();
		MultiValueMap<SvnFolder, SvnFolder> branchesByProjectMap = convert(branches);
		for (SvnFolder project : branchesByProjectMap.keySet()) {
			findMergesForProject(mergeInfoResult, branchesByProjectMap, project);
		}

		return mergeInfoResult;
	}

	private void findMergesForProject(MergeInfoResult mergeInfoResult,
			MultiValueMap<SvnFolder, SvnFolder> branchesByProjectMap, SvnFolder project) {
		log.debug("findMerges for project " + project.getName());
		List<SvnFolder> branchesByProject = branchesByProjectMap.get(project);
		sortByName(branchesByProject);
		for (SvnFolder to : branchesByProject) {
			List<SvnFolder> fromBranches = ConventionsStrategyHolder.getStrategy().resolveFromBranches(to);
			Boolean mergeOnSubFoldersForProject = globalSettingsProvider.getGlobalSettings().isMergeOnSubFoldersForProject(
					project.getName());
			List<MergeInfoResultItem> merges = findMerges(to, fromBranches, mergeOnSubFoldersForProject);
			mergeInfoResult.addAll(merges);
		}
	}

	private List<MergeInfoResultItem> findMerges(SvnFolder to, List<SvnFolder> fromBranches,
			Boolean mergeOnSubFoldersForProject) {
		SvnMergeInfoProvider svnMergeInfoProvider = new SvnMergeInfoProvider(
				svnConnection.getBaseRepositoryConnection());
		List<MergeInfoResultItem> mergeInfoResultItems = new ArrayList<MergeInfoResultItem>();
		try {
			for (SvnFolder from : fromBranches) {
				if (mergeOnSubFoldersForProject) {
					for (String commonFolder : to.getCommonSubFolders(from)) {
						List<SVNLogEntry> merges = svnMergeInfoProvider.getMerges(from, to, commonFolder);
						mergeInfoResultItems.add(new MergeInfoResultItem(to, from, commonFolder, merges));
					}
				} else {
					List<SVNLogEntry> merges = svnMergeInfoProvider.getMerges(from, to);
					mergeInfoResultItems.add(new MergeInfoResultItem(to, from, merges));
				}
			}
		} catch (SVNException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		return mergeInfoResultItems;
	}

	private void sortByName(List<SvnFolder> branchesByProject) {
		Collections.sort(branchesByProject, new Comparator<SvnFolder>() {
			public int compare(SvnFolder o1, SvnFolder o2) {
				String name = o2.getName();
				String name1 = o1.getName();
				return name.compareTo(name1);
			}
		});
	}

	private MultiValueMap<SvnFolder, SvnFolder> convert(List<SvnFolder> branches) {
		MultiValueMap<SvnFolder, SvnFolder> branchesByProjectMap = new LinkedMultiValueMap<SvnFolder, SvnFolder>();
		for (SvnFolder folder : branches) {
			branchesByProjectMap.add(folder.getParent(), folder);
		}
		return branchesByProjectMap;
	}
}
