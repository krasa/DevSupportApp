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
import org.springframework.transaction.annotation.Transactional;
import org.tmatesoft.svn.core.SVNDirEntry;

/**
 * @author Vojtech Krasa
 */
@Repository
public class SvnFolderDAOImpl extends AbstractDAO<SvnFolder> implements SvnFolderDAO {
	public SvnFolderDAOImpl() {
		super(SvnFolder.class);
	}

	@Override
	protected Class<SvnFolder> getEntityClass() {
		return SvnFolder.class;
	}

	@Override
	@Transactional
	public void replaceBranches(List<SVNDirEntry> svnDirEntries) {
		deleteAll();
		for (SVNDirEntry svnDirEntry : svnDirEntries) {
			save(new SvnFolder(svnDirEntry, Type.BRANCH));
		}
	}

	@Override
	public List<SvnFolder> getSubDirsByParentPath(String name) {
		Query query = getSession().createQuery(
				"from " + getEntityName() + " s where s.parent.name = :name order by s.name");
		query.setString("name", name);
		return query.list();
	}

	@Override
	public SvnFolder findByPath(String path) {
		Query query = getSession().createQuery("from " + getEntityName() + " s where s.path = :path");
		query.setString("path", path);
		return (SvnFolder) query.uniqueResult();
	}

	@Override
	public List<SvnFolder> findAllProjects() {
		Query query = getSession().createQuery("from " + getEntityName() + " s where s.type = :type order by s.name");
		query.setParameter("type", Type.PROJECT);
		return query.list();

	}

	@Override
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

	@Override
	public List<SvnFolder> findBranchesByNameLike(String name) {
		Query query = getSession().createQuery(
				"from " + getEntityName()
						+ " s where lower(s.name) like lower(:name) and s.type = :type  order by s.name  ");
		query.setParameter("type", Type.BRANCH);
		query.setString("name", "%" + name + "%");
		return query.list();
	}

	@Override
	public List<SvnFolder> findBranchesByNamePrefix(String name) {
		Query query = getSession().createQuery(
				"from " + getEntityName()
						+ " s where lower(s.name) like lower(:name) and s.type = :type  order by s.name  ");
		query.setParameter("type", Type.BRANCH);
		query.setString("name", name + "%");
		return query.list();
	}

	@Override
	public SvnFolder findBranchByInCaseSensitiveName(String name) {
		Query query = getSession().createQuery(
				"from " + getEntityName() + " s where lower(s.name) = lower(:name)  and s.type = :type ");
		query.setParameter("type", Type.BRANCH);
		query.setString("name", name);
		return (SvnFolder) query.uniqueResult();
	}

	@Override
	public List<SvnFolder> findBranchesByNames(String parent, List<Branch> selectedBranches) {
		if (selectedBranches.isEmpty()) {
			return Collections.emptyList();
		}
		Query query = getSession().createQuery(
				"from " + getEntityName()
						+ " s where s.name in :names  and s.type = :type and s.parent.name = :parent order by s.name");
		query.setString("parent", parent);
		query.setParameter("type", Type.BRANCH);
		query.setParameterList("names", toNames(selectedBranches));
		return query.list();
	}

	@Override
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

	@Override
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

	@Override
	public SvnFolder findBranchByName(String name2) {
		Query query = getSession().createQuery(
				"from " + getEntityName() + " s where s.name = :name  and s.type = :type ");
		query.setParameter("type", Type.BRANCH);
		query.setString("name", name2);
		return (SvnFolder) query.uniqueResult();
	}

	@Override
	public void replaceProjects(List<SVNDirEntry> svnDirEntries) {
		log.debug("replaceProjects, size: {}", svnDirEntries.size());
		for (SVNDirEntry svnDirEntry : svnDirEntries) {
			save(new SvnFolder(svnDirEntry, Type.PROJECT));
		}
		log.debug("replaceProjects ok");
	}

}
