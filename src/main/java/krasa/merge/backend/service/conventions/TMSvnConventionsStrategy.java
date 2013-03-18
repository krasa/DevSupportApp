package krasa.merge.backend.service.conventions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import krasa.merge.backend.domain.SvnFolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static Integer getVersion(String name) {
		try {
			return Integer.valueOf(name.substring(name.lastIndexOf("_") + 1));
		} catch (NumberFormatException e) {
			log.warn(name + " " + e.getMessage());
			return -1;
		}
	}

	public static final String TMSVN_CONVENTIONS_STRATEGY = "TMSvnConventionsStrategy";

	@Override
	public List<SvnFolder> resolveFromBranches(SvnFolder toFolder) {
		List<SvnFolder> result = new ArrayList<SvnFolder>();
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
		if (TM_NAME_COMPARATOR_BY_VERSION_STRING.compare(searchFrom, branchName) < 0) {
			for (SvnFolder child : childs) {
				String childName = child.getName();
				if (compare(searchFrom, childName) >= 0 && compare(branchName, childName) < 0) {
					result.add(child);
				}
			}
		} else {
			for (SvnFolder child : childs) {
				String name = child.getName();
				if (compare(searchFrom, name) <= 0 && compare(branchName, name) > 0) {
					result.add(child);
				}
			}
		}
		Collections.sort(result, SvnFolder.NAME_COMPARATOR);
		return result;
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

}
