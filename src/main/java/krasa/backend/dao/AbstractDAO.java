package krasa.backend.dao;

import krasa.backend.domain.AbstractEntity;
import org.hibernate.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public abstract class AbstractDAO<T extends AbstractEntity> implements DAO<T> {
    protected final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    protected SessionFactory sf;

    protected Session getSession() {
        return sf.getCurrentSession();
    }

    protected AbstractDAO() {
    }

    public AbstractDAO(Class<T> domainClass) {
    }

    public void deleteAll() {
        Query query = getSession().createQuery("delete from " + getEntityName());
        query.executeUpdate();
    }

    public T findById(Integer id) {
        return (T) getSession().get(getEntityClass(), id);
    }

    public List<T> findAll() {
        return getSession().createQuery("from " + getEntityName()).list();
    }

    protected String getEntityName() {
        return getEntityClass().getCanonicalName();
    }

    public T findFirst() {
        Object o = getSession().createQuery("SELECT min(id) from " + getEntityName()).uniqueResult();
        if (o == null) {
            return null;
        }
        Integer id = Integer.valueOf(o.toString());
        return findById(id);
    }

    public T findLast() {
        Object o = getSession().createQuery("SELECT max(id) from " + getEntityName()).uniqueResult();
        if (o == null) {
            return null;
        }
        Integer id = Integer.valueOf(o.toString());
        return findById(id);
    }

    public T save(T object) {
        if (object.getId() == null) {
            getSession().persist(object);

        } else {
            getSession().merge(object);
        }
        return (T) object;
    }

    public int count() {
        return Integer.valueOf(getSession().createQuery("SELECT COUNT(*) FROM " + getEntityName()).uniqueResult().toString());

    }


    public void delete(T object) {
        getSession().delete(object);
    }

    protected abstract Class<T> getEntityClass();

    protected Query query(String query) {
        try {
            Query q = getSession().createQuery(query);
            return q;
        } catch (Exception e) {
            throw new HibernateException("Error while creating query: " + query, e);
        }
    }

    /**
     * returns list of records for given query, ensures that result is not null (empty list returned when no record
     * found)
     *
     * @param query
     * @return
     */
    @SuppressWarnings("unchecked")
    protected List list(Query query) {
        log.debug("Listing records for query: [{}]", query);
        try {
            List result = query.list();
            log.debug("Created list of records: [{}]", result);
            return result;
        } catch (Exception e) {
            throw new HibernateException("Error while listing query: " + query, e);
        }
    }

    protected Object uniqueResult(Query query) {
        log.debug("Getting unique result for query: [{}]", query);
        try {
            Object result = query.uniqueResult();
            log.debug("Obtained unique result: [{}]", result);
            return result;
        } catch (Exception e) {
            throw new HibernateException("Error while getting unique result: " + query, e);
        }
    }

    protected Object uniqueResult(Criteria criteria) {
        log.debug("Getting unique result for criteria: [{}]", criteria);
        try {
            Object result = criteria.uniqueResult();
            log.debug("Obtained unique result: [{}]", result);
            return result;
        } catch (Exception e) {
            throw new HibernateException("Error while getting unique result: " + criteria, e);
        }
    }


}
