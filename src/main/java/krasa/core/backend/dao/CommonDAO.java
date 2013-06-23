package krasa.core.backend.dao;

import java.util.List;

import krasa.core.backend.domain.AbstractEntity;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public abstract class CommonDAO {
	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	protected SessionFactory sf;

	public void flush() {
		sf.getCurrentSession().flush();
		sf.getCurrentSession().clear();
	}

	public Session getSession() {
		return sf.getCurrentSession();
	}

	@SuppressWarnings("unchecked")
	protected <D extends AbstractEntity> D uniqueResult(Query query, Class<D> dClass) {
		log.debug("Getting unique result for query: [{}]", query);
		try {
			Object result = query.uniqueResult();
			log.debug("Obtained unique result: [{}]", result);
			return (D) result;
		} catch (Exception e) {
			throw new HibernateException("Error while getting unique result: " + query, e);
		}
	}

	public <T extends AbstractEntity> void delete(T object) {
		getSession().delete(object);
	}

	public <D extends AbstractEntity> List<D> findAll(D t) {
		return getSession().createQuery("from " + t.getClass().getName()).list();
	}

	public <T extends AbstractEntity> T refresh(T object) {
		return (T) getSession().get(object.getClass(), object.getId());
	}

	public <T extends AbstractEntity> T merge(T buildJob) {
		return (T) getSession().merge(buildJob);
	}

	public <T extends AbstractEntity> T save(T object) {
		if (object.getId() == null) {
			getSession().persist(object);

		} else {
			getSession().update(object);
		}
		return (T) object;
	}

	protected Query query(String query) {
		try {
			return getSession().createQuery(query);
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

}
