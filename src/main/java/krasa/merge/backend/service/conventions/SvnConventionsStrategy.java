package krasa.merge.backend.service.conventions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import krasa.core.backend.service.GlobalSettingsProvider;
import krasa.merge.backend.dao.SvnFolderDAO;
import krasa.merge.backend.domain.SvnFolder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service(SvnConventionsStrategy.SVN_CONVENTIONS_STRATEGY)
public class SvnConventionsStrategy {

	public static final String SVN_CONVENTIONS_STRATEGY = "SvnConventionsStrategy";

	@Autowired
	protected SvnFolderDAO svnFolderDAO;
	@Autowired
	protected GlobalSettingsProvider globalSettingsProvider;

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
			List<SvnFolder> childs = svnFolder.getParent().getChilds();
			Collections.sort(childs, SvnFolder.NAME_COMPARATOR);
			Collections.reverse(childs);
			for (int i = 0; i < childs.size(); i++) {
				SvnFolder folder = childs.get(i);
				if (folder == svnFolder) {
					if (i > 0) {
						result.add(childs.get(i - 1));
					}
				}
			}
		}
		Collections.sort(result, SvnFolder.NAME_COMPARATOR);
		return result;
	}

}
