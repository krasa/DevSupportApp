package krasa.core.backend.dao;

import java.util.List;

import krasa.core.backend.domain.AbstractEntity;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class UniversalDao {
	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	protected SessionFactory sf;

	public Session getSession() {
		return sf.getCurrentSession();
	}

	public <T extends AbstractEntity> List<T> findBy(Class clazz, Object... propertyAndValue) {
		final Session session = getSession();
		final Criteria crit = session.createCriteria(clazz.getSimpleName());
		for (int i = 0; i < propertyAndValue.length - 1; i = i + 2) {
			crit.add(Restrictions.eq((String) propertyAndValue[i], propertyAndValue[i + 1]));
		}
		return crit.list();
	}

	public void deleteAll(Class clazz) {
		Query query = getSession().createQuery("delete from " + clazz.getSimpleName());
		query.executeUpdate();
	}

	public <T extends AbstractEntity> T findById(Class<T> clazz, Integer id) {
		return (T) getSession().get(clazz.getSimpleName(), id);
	}

	public <T extends AbstractEntity> List<T> findAll(Class clazz) {
		return getSession().createQuery("from " + clazz.getSimpleName()).list();
	}

	public <T extends AbstractEntity> T findFirst(Class<T> clazz) {
		Object o = getSession().createQuery("SELECT min(id) from " + clazz.getSimpleName()).uniqueResult();
		if (o == null) {
			return null;
		}
		Integer id = Integer.valueOf(o.toString());
		return findById(clazz, id);
	}

	public <T extends AbstractEntity> T findLast(Class<T> clazz) {
		Object o = getSession().createQuery("SELECT max(id) from " + clazz.getSimpleName()).uniqueResult();
		if (o == null) {
			return null;
		}
		Integer id = Integer.valueOf(o.toString());
		return findById(clazz, id);
	}

	public <T extends AbstractEntity> T save(T object) {
		if (object.getId() == null) {
			getSession().persist(object);

		} else {
			getSession().update(object);
		}
		return (T) object;
	}

	public int count(Class clazz) {
		return Integer.valueOf(getSession().createQuery("SELECT COUNT(*) FROM " + clazz.getSimpleName()).uniqueResult().toString());

	}

	public <T extends AbstractEntity> void delete(T object) {
		getSession().delete(object);
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

	public void setSf(SessionFactory sf) {
		this.sf = sf;
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

	public <T extends AbstractEntity> T refresh(T object) {
		return findById((Class<T>) object.getClass(), object.getId());
	}

}
