package krasa.merge.backend.service.conventions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import krasa.core.backend.service.GlobalSettingsProvider;
import krasa.merge.backend.dao.SvnFolderDAO;
import krasa.merge.backend.domain.SvnFolder;
import krasa.merge.backend.domain.Type;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(SvnConventionsStrategy.SVN_CONVENTIONS_STRATEGY)
public class SvnConventionsStrategy {

	public static final String SVN_CONVENTIONS_STRATEGY = "SvnConventionsStrategy";

	@Autowired
	protected SvnFolderDAO svnFolderDAO;
	@Autowired
	protected GlobalSettingsProvider globalSettingsProvider;

	public List<SvnFolder> resolveFromBranches(SvnFolder toFolder) {
		List<SvnFolder> result = new ArrayList<SvnFolder>();
		String searchFrom = toFolder.getSearchFrom();
		if (searchFrom != null) {
			List<SvnFolder> childs = toFolder.getParent().getChilds();
			if (toFolder.getType() == Type.TRUNK || searchFrom.compareTo(toFolder.getName()) < 0) {
				for (SvnFolder child : childs) {
					String name = child.getName();
					if (name.compareTo(searchFrom) >= 0
							&& (toFolder.getType() == Type.TRUNK || name.compareTo(toFolder.getName()) < 0)) {
						result.add(child);
					}
				}
			} else {
				for (SvnFolder child : childs) {
					String name = child.getName();
					if (name.compareTo(searchFrom) <= 0 && name.compareTo(toFolder.getName()) > 0) {
						result.add(child);
					}
				}
			}
		} else {
			if (toFolder.getType() == Type.TRUNK) {
				result.add(getNewestBranch(toFolder));
			} else {
				SvnFolder e = findFirstLowerBranch(toFolder);
				if (e != null) {
					result.add(e);
				}
			}

		}
		Collections.sort(result, SvnFolder.NAME_COMPARATOR);
		return result;
	}

	private SvnFolder findFirstLowerBranch(SvnFolder toFolder) {
		SvnFolder e = null;
		List<SvnFolder> siblings = toFolder.getParent().getChilds();
		Collections.sort(siblings, SvnFolder.NAME_COMPARATOR);
		Collections.reverse(siblings);
		for (int i = 0; i < siblings.size(); i++) {
			SvnFolder folder = siblings.get(i);
			if (folder == toFolder) {
				if (i > 0) {
					e = siblings.get(i - 1);
				}
			}
		}
		return e;
	}

	private SvnFolder getNewestBranch(SvnFolder toFolder) {
		List<SvnFolder> siblings = toFolder.getParent().getChilds();
		Collections.sort(siblings, SvnFolder.NAME_COMPARATOR);
		return siblings.get(0);
	}

}