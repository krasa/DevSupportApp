package krasa.merge.backend.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import krasa.core.backend.service.GlobalSettingsProvider;
import krasa.merge.backend.dao.SvnFolderDAO;
import krasa.merge.backend.domain.Repository;
import krasa.merge.backend.domain.SvnFolder;
import krasa.merge.backend.dto.MergeInfoResult;
import krasa.merge.backend.dto.MergeInfoResultItem;
import krasa.merge.backend.service.conventions.ConventionsStrategyHolder;
import krasa.merge.backend.svn.SvnMergeInfoProvider;

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
public class MergeInfoService {
	protected final Logger log = LoggerFactory.getLogger(getClass());
	@Autowired
	private SvnFolderDAO svnFolderDAO;

	@Autowired
	private GlobalSettingsProvider globalSettingsProvider;

	public MergeInfoResult findMerges(List<SvnFolder> folders) {
		MergeInfoResult mergeInfoResult = new MergeInfoResult();
		MultiValueMap<SvnFolder, SvnFolder> foldersByProjectMap = groupByProject(folders);
		for (SvnFolder project : foldersByProjectMap.keySet()) {
			mergeInfoResult.addAll(findMergesForProject(project, foldersByProjectMap));
		}

		return mergeInfoResult;
	}

	private List<MergeInfoResultItem> findMergesForProject(SvnFolder project,
			MultiValueMap<SvnFolder, SvnFolder> foldersByProjectMap) {
		Boolean mergeOnSubFolders = globalSettingsProvider.getGlobalSettings().isMergeOnSubFoldersForProject(
				project.getName());
		List<MergeInfoResultItem> merges = new ArrayList<MergeInfoResultItem>();
		log.debug("findMergesForProject for project " + project.getName());
		List<SvnFolder> branchesByProject = foldersByProjectMap.get(project);
		sortByName(branchesByProject);
		for (SvnFolder to : branchesByProject) {
			List<SvnFolder> fromBranches = ConventionsStrategyHolder.getStrategy().resolveFromBranches(to);
			merges.addAll(findMerges(to, fromBranches, mergeOnSubFolders));
		}
		return merges;
	}

	private List<MergeInfoResultItem> findMerges(SvnFolder to, List<SvnFolder> fromBranches, Boolean mergeOnSubfolders) {
		Repository repository = to.getRepository();
		if (repository == null) {
			repository = globalSettingsProvider.getGlobalSettings().getDefaultRepository();
		}
		SvnMergeInfoProvider svnMergeInfoProvider = SvnMergeInfoProvider.create(repository);
		List<MergeInfoResultItem> mergeInfoResultItems = new ArrayList<MergeInfoResultItem>();
		try {
			for (SvnFolder from : fromBranches) {
				if (mergeOnSubfolders) {
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
		Collections.sort(branchesByProject, SvnFolder.NAME_COMPARATOR);
	}

	private MultiValueMap<SvnFolder, SvnFolder> groupByProject(List<SvnFolder> branches) {
		MultiValueMap<SvnFolder, SvnFolder> foldersByProjectMap = new LinkedMultiValueMap<SvnFolder, SvnFolder>();
		for (SvnFolder folder : branches) {
			foldersByProjectMap.add(folder.getParent(), folder);
		}
		return foldersByProjectMap;
	}
}
