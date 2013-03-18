package krasa.merge.backend.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import krasa.core.backend.dao.AbstractDAO;
import krasa.merge.backend.domain.Branch;
import krasa.merge.backend.domain.SvnFolder;
import krasa.merge.backend.domain.Type;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.tmatesoft.svn.core.SVNDirEntry;

/**
 * @author Vojtech Krasa
 */
@Repository
public class SvnFolderDAO extends AbstractDAO<SvnFolder> {
	public SvnFolderDAO() {
		super(SvnFolder.class);
	}

	@Override
	protected Class<SvnFolder> getEntityClass() {
		return SvnFolder.class;
	}

	public List<SvnFolder> getSubDirsByParentPath(String name) {
		Query query = getSession().createQuery(
				"from " + getEntityName() + " s where s.parent.name = :name order by s.name");
		query.setString("name", name);
		return query.list();
	}

	public SvnFolder findByPath(String path) {
		Query query = getSession().createQuery("from " + getEntityName() + " s where s.path = :path");
		query.setString("path", path);
		return (SvnFolder) query.uniqueResult();
	}

	public List<SvnFolder> findAllProjects() {
		Query query = getSession().createQuery("from " + getEntityName() + " s where s.type = :type order by s.name");
		query.setParameter("type", Type.PROJECT);
		return query.list();

	}

	public List<SvnFolder> findBranchesByNames(List<Branch> selectedBranches) {
		if (selectedBranches.isEmpty()) {
			return Collections.emptyList();
		}
		Query query = getSession().createQuery(
				"from " + getEntityName() + " s where s.name in :names  and s.type = :type  order by s.name");
		query.setParameter("type", Type.BRANCH);
		query.setParameterList("names", toNames(selectedBranches));
		return query.list();
	}

	private Object[] toNames(List<Branch> selectedBranches) {
		List<String> names = new ArrayList<String>();
		for (Branch selectedBranch : selectedBranches) {
			names.add(selectedBranch.getName());
		}
		return names.toArray();
	}

	public List<SvnFolder> findBranchesByNameLike(String name) {
		Query query = getSession().createQuery(
				"from " + getEntityName()
						+ " s where lower(s.name) like lower(:name) and s.type = :type  order by s.name  ");
		query.setParameter("type", Type.BRANCH);
		query.setString("name", "%" + name + "%");
		return query.list();
	}

	public List<SvnFolder> findBranchesByNamePrefix(String name) {
		Query query = getSession().createQuery(
				"from " + getEntityName()
						+ " s where lower(s.name) like lower(:name) and s.type = :type  order by s.name  ");
		query.setParameter("type", Type.BRANCH);
		query.setString("name", name + "%");
		return query.list();
	}

	public SvnFolder findBranchByInCaseSensitiveName(String name) {
		Query query = getSession().createQuery(
				"from " + getEntityName() + " s where lower(s.name) = lower(:name)  and s.type = :type ");
		query.setParameter("type", Type.BRANCH);
		query.setString("name", name);
		return (SvnFolder) query.uniqueResult();
	}

	public List<SvnFolder> findFoldersByNames(String parent, List<Branch> list) {
		if (list.isEmpty()) {
			return Collections.emptyList();
		}
		Query query = getSession().createQuery(
				"from " + getEntityName() + " s where s.name in :names  and s.parent.name = :parent order by s.name");
		query.setString("parent", parent);
		query.setParameterList("names", toNames(list));
		return query.list();
	}

	public SvnFolder findProjectByName(String name) {
		Query query = getSession().createQuery("from " + getEntityName() + " s where s.name = :name and s.type = :type");
		query.setString("name", name);
		query.setParameter("type", Type.PROJECT);
		List list = query.list();
		if (list.isEmpty()) {
			return null;
		} else if (list.size() > 1) {
			throw new IllegalStateException(Arrays.toString(list.toArray()));
		}
		return (SvnFolder) list.get(0);
	}

	public List<String> findBranchesByNameLike(String parentName, String input) {
		Query query = getSession().createQuery(
				"select s.name from "
						+ getEntityName()
						+ " s where lower(s.name) like lower(:name)  and s.type = :type and s.parent.name = :parentName  order by s.name ");
		query.setParameter("type", Type.BRANCH);
		query.setString("name", "%" + input + "%");
		query.setString("parentName", parentName);
		return query.list();

	}

	public SvnFolder findBranchByName(String name2) {
		Query query = getSession().createQuery(
				"from " + getEntityName() + " s where s.name = :name  and s.type = :type ");
		query.setParameter("type", Type.BRANCH);
		query.setString("name", name2);
		return (SvnFolder) query.uniqueResult();
	}

	public void saveProjects(List<SVNDirEntry> projects) {
		for (SVNDirEntry project : projects) {
			saveProject(project);
		}
	}

	private void saveProject(SVNDirEntry entry) {
		// path=name
		SvnFolder project = new SvnFolder(entry, entry.getName(), Type.PROJECT);
		project.setType(Type.PROJECT);
		save(project);
	}

}
