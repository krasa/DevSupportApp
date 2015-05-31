package krasa.merge.backend.service.conventions;

import java.util.*;

import krasa.merge.backend.domain.SvnFolder;

import org.slf4j.*;
import org.springframework.stereotype.Service;

@Service(TMSvnConventionsStrategy.TMSVN_CONVENTIONS_STRATEGY)
public class TMSvnConventionsStrategy extends SvnConventionsStrategy {

	protected static final Logger log = LoggerFactory.getLogger(TMSvnConventionsStrategy.class);

	public static final Comparator<String> TM_NAME_COMPARATOR_BY_VERSION_STRING = new Comparator<String>() {

		@Override
		public int compare(String name, String name1) {
			return getVersion(name).compareTo(getVersion(name1));
		}
	};

	public static final Comparator<SvnFolder> TM_NAME_COMPARATOR_BY_VERSION = new Comparator<SvnFolder>() {

		@Override
		public int compare(SvnFolder o1, SvnFolder o2) {
			return getVersion(o1).compareTo(getVersion(o2.getName()));
		}
	};

	private static Integer getVersion(SvnFolder a) {
		String name = a.getName();
		return getVersion(name);
	}

	private static String getNameWithoutVersion(SvnFolder a) {
		String name = a.getName();
		return name.substring(0, name.lastIndexOf("_") + 1);
	}

	private static Integer getVersion(String name) {
		try {
			return Integer.valueOf(name.substring(name.lastIndexOf("_") + 1));
		} catch (NumberFormatException e) {
			log.debug(name + " " + e.getMessage());
			return -1;
		}
	}

	public static final String TMSVN_CONVENTIONS_STRATEGY = "TMSvnConventionsStrategy";

	@Override
	public List<SvnFolder> resolveFromBranches(SvnFolder toFolder) {
		List<SvnFolder> result = new ArrayList<>();
		String searchFrom = toFolder.getSearchFrom();
		String branchName = toFolder.getName();
		List<SvnFolder> childs = toFolder.getParent().getChilds();

		if (searchFrom == null) {
			Collections.sort(childs, TM_NAME_COMPARATOR_BY_VERSION);
			Collections.reverse(childs);
			searchFrom = getSearchFrom(toFolder, childs);
		}
		if (searchFrom == null) {
			return Collections.emptyList();
		}
		if (isTrunk(searchFrom)) {
			for (SvnFolder child : childs) {
				if (searchFrom.equals(child.getName())) {
					result.add(child);
				}
			}
			// when searchFrom < branchName or branchName isTrunk
		} else if (TM_NAME_COMPARATOR_BY_VERSION_STRING.compare(searchFrom, branchName) < 0 || isTrunk(branchName)) {
			for (SvnFolder child : childs) {
				String childName = child.getName();
				if (branchVersionIsHigherOrEquals(searchFrom, childName)
						&& currentBranchIsTrunkOrHigher(branchName, childName)) {
					result.add(child);
				}
			}
		} else {// when searchFrom> branchName
			for (SvnFolder child : childs) {
				String name = child.getName();
				// 7000 - xxx - 8000 - xxx will be added
				if (compare(searchFrom, name) <= 0 && compare(branchName, name) > 0) {
					result.add(child);
				}
			}
		}
		Collections.sort(result, SvnFolder.NAME_COMPARATOR);
		return result;
	}

	private boolean branchVersionIsHigherOrEquals(String searchFrom, String childName) {
		return compare(searchFrom, childName) >= 0;
	}

	private boolean currentBranchIsTrunkOrHigher(String branchName, String childName) {
		return (compare(branchName, childName) < 0 || isTrunk(branchName));
	}

	private String getSearchFrom(SvnFolder svnFolder, List<SvnFolder> childs) {
		String searchFrom = null;
		for (SvnFolder child : childs) {
			if (getVersion(child) < getVersion(svnFolder) && getVersion(child) % 100 == 0) {
				searchFrom = child.getName();
				break;
			}
		}
		return searchFrom;
	}

	private int compare(String searchFrom, String childName) {
		return TM_NAME_COMPARATOR_BY_VERSION_STRING.compare(childName, searchFrom);
	}

	@Override
	public void postProcessAllBranches(Map<String, SvnFolder> childs) {
		SearchFromDTO searchFromDTO = new SearchFromDTO(childs).invoke();
		Set<Integer> versionsSet = searchFromDTO.getVersionsSet();
		List<Integer> versions = searchFromDTO.getVersions();

		for (SvnFolder child : childs.values()) {
			if (child.getSearchFrom() == null) {
				setSearchFrom(versionsSet, versions, child);
			}
		}
	}

	@Override
	public void replaceSearchFrom(SvnFolder selectedBranch) {
		SvnFolder parent = selectedBranch.getParent();
		Map<String, SvnFolder> childs = parent.getChildsAsMapByName();
		SearchFromDTO searchFromDTO = new SearchFromDTO(childs).invoke();
		Set<Integer> versionsSet = searchFromDTO.getVersionsSet();
		List<Integer> versions = searchFromDTO.getVersions();
		setSearchFrom(versionsSet, versions, selectedBranch);
	}

	@Override
	public void replaceSearchFromToTrunk(SvnFolder selectedBranch) {
		selectedBranch.setSearchFrom(getNameWithoutVersion(selectedBranch) + "9999");
	}

	protected void setSearchFrom(Set<Integer> versionsSet, List<Integer> versions, SvnFolder child) {
		int searchFromVersion = getSearchFromVersion(child);

		if (isTrunk(child) && versions.size() > 0) {
			Integer highestVersion = versions.get(versions.size() - 1);
			child.setSearchFrom(getNameWithoutVersion(child) + highestVersion);
		} else if (versionsSet.contains(searchFromVersion)) {
			child.setSearchFrom(getNameWithoutVersion(child) + searchFromVersion);
		}
	}

	private int getSearchFromVersion(SvnFolder child) {
		Integer version = getVersion(child);
		if (version % 100 == 0) {
			return version - 100;
		} else {
			int i = version / 100;
			return i * 100 - 100;
		}
	}

	@Override
	protected boolean isTrunk(SvnFolder toFolder) {
		return super.isTrunk(toFolder) || isTrunk(toFolder.getName());
	}

	private boolean isTrunk(String name) {
		return getVersion(name) == 9999;
	}

	private class SearchFromDTO {

		private Map<String, SvnFolder> childs;
		private Set<Integer> versionsSet;
		private List<Integer> versions;

		public SearchFromDTO(Map<String, SvnFolder> childs) {
			this.childs = childs;
		}

		public Set<Integer> getVersionsSet() {
			return versionsSet;
		}

		public List<Integer> getVersions() {
			return versions;
		}

		public SearchFromDTO invoke() {
			versionsSet = new HashSet<>();
			Set<String> strings = childs.keySet();
			for (String string : strings) {
				Integer version = getVersion(string);
				if (version < 90000 && version % 100 == 0) {
					versionsSet.add(version);
				}
			}
			versions = new ArrayList<>(versionsSet);
			Collections.sort(versions);
			return this;
		}
	}
}
