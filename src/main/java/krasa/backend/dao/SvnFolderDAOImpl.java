package krasa.backend.dao;

import krasa.backend.domain.SvnFolder;
import krasa.backend.domain.Type;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.tmatesoft.svn.core.SVNDirEntry;

import java.util.Collections;
import java.util.List;
import java.util.Set;

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

    @Transactional
    public void replaceBranches(List<SVNDirEntry> svnDirEntries) {
        deleteAll();
        for (SVNDirEntry svnDirEntry : svnDirEntries) {
            save(new SvnFolder(svnDirEntry, Type.BRANCH));
        }
    }

    public List<SvnFolder> getSubDirsByParentPath(String name) {
        Query query = getSession().createQuery("from " + getEntityName() + " s where s.parent.name = :name order by s.name");
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

    public List<SvnFolder> findBranchesByNames(Set<String> selectedBranches) {
        if (selectedBranches.isEmpty()) {
            return Collections.emptyList();
        }
        Query query = getSession().createQuery("from " + getEntityName() + " s where s.name in :names  and s.type = :type  order by s.name");
        query.setParameter("type", Type.BRANCH);
        Object[] vals = selectedBranches.toArray();
        query.setParameterList("names", vals);
        return query.list();
    }

    public List<SvnFolder> findBranchesByNameLike(String name) {
        Query query = getSession().createQuery("from " + getEntityName() + " s where lower(s.name) like lower(:name) and s.type = :type  order by s.name  ");
        query.setParameter("type", Type.BRANCH);
        query.setString("name", "%" + name + "%");
        return query.list();
    }

    public SvnFolder findBranchByInCaseSensitiveName(String name) {
        Query query = getSession().createQuery("from " + getEntityName() + " s where lower(s.name) = lower(:name)  and s.type = :type ");
        query.setParameter("type", Type.BRANCH);
        query.setString("name", name);
        return (SvnFolder) query.uniqueResult();
    }

    public List<SvnFolder> findBranchesByNames(String parent, Set<String> selectedBranches) {
        if (selectedBranches.isEmpty()) {
            return Collections.emptyList();
        }
        Query query = getSession().createQuery("from " + getEntityName() + " s where s.name in :names  and s.type = :type and s.parent.name = :parent order by s.name");
        query.setString("parent", parent);
        query.setParameter("type", Type.BRANCH);
        query.setParameterList("names", selectedBranches.toArray());
        return query.list();
    }

    public SvnFolder findProjectByName(String name) {
        Query query = getSession().createQuery("from " + getEntityName() + " s where s.name = :name and s.type = :type");
        query.setString("name", name);
        query.setParameter("type", Type.PROJECT);
        return (SvnFolder) query.uniqueResult();
    }

    public List<SvnFolder> findBranchesByNameLike(String input, String parentName) {
        Query query = getSession().createQuery("from " + getEntityName() + " s where lower(s.name) = lower(:name)  and s.type = :type and s.parent.name = :parentName  order by s.name ");
        query.setParameter("type", Type.BRANCH);
        query.setString("name", input);
        query.setString("parentName", parentName);
        return query.list();


    }

    public void replaceProjects(List<SVNDirEntry> svnDirEntries) {
        log.debug("replaceProjects, size: {}", svnDirEntries.size());
        for (SVNDirEntry svnDirEntry : svnDirEntries) {
            save(new SvnFolder(svnDirEntry, Type.PROJECT));
        }
        log.debug("replaceProjects ok");
    }

}
