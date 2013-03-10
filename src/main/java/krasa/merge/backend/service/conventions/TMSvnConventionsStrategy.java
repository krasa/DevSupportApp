package krasa.merge.backend.service.conventions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import krasa.merge.backend.domain.SvnFolder;

import org.springframework.stereotype.Service;

@Service(TMSvnConventionsStrategy.TMSVN_CONVENTIONS_STRATEGY)
public class TMSvnConventionsStrategy extends SvnConventionsStrategy {

	public static final String TMSVN_CONVENTIONS_STRATEGY = "TMSvnConventionsStrategy";

	public List<SvnFolder> resolveFromBranches(SvnFolder svnFolder) {
		List<SvnFolder> result = new ArrayList<SvnFolder>();
		String searchFrom = svnFolder.getSearchFrom();
		if (searchFrom != null) {
			List<SvnFolder> childs = svnFolder.getParent().getChilds();
			if (searchFrom.compareTo(svnFolder.getName()) < 0) {
				for (SvnFolder child : childs) {
					String name = child.getName();
					if (name.compareTo(searchFrom) >= 0 && name.compareTo(svnFolder.getName()) < 0) {
						result.add(child);
					}
				}
			} else {
				for (SvnFolder child : childs) {
					String name = child.getName();
					if (name.compareTo(searchFrom) <= 0 && name.compareTo(svnFolder.getName()) > 0) {
						result.add(child);
					}
				}
			}
		} else {
			List<String> alphabeticallyLowerBranchNameForMatchAll = getAlphabeticallyLowerBranchNameForMatchAll(svnFolder);
			for (String s : alphabeticallyLowerBranchNameForMatchAll) {
				result.addAll(svnFolderDAO.findBranchesByNamePrefix(s));
			}
		}
		Collections.sort(result, SvnFolder.NAME_COMPARATOR);
		return result;
	}

	public List<String> getAlphabeticallyLowerBranchNameForMatchAll(SvnFolder svnFolder) {
		String name = svnFolder.getName();
		Integer version = Integer.valueOf(getVersion(svnFolder));
		ArrayList<String> strings = new ArrayList<String>();
		int i1;
		if (version % 100 == 0) {
			int i = version - 100;
			i1 = i / 100;
			strings.add(name.substring(0, name.lastIndexOf("_") + 1) + i1);
		}
		if (version % 100 != 0) {
			i1 = version / 100;
			strings.add(name.substring(0, name.lastIndexOf("_") + 1) + i1);
		}

		return strings;
	}

	public String getVersion(SvnFolder svnFolder) {
		String name = svnFolder.getName();
		return name.substring(name.lastIndexOf("_") + 1);
	}
}
