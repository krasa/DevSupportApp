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
import org.springframework.stereotype.Service;

@Service
public abstract class AbstractDAO<T extends AbstractEntity> implements DAO<T> {
	protected final Logger log = LoggerFactory.getLogger(getClass());

	@Autowired
	protected SessionFactory sf;

	protected AbstractDAO() {
	}

	public AbstractDAO(Class<T> domainClass) {
	}

	public void flush() {
		sf.getCurrentSession().flush();
		sf.getCurrentSession().clear();
	}

	public Session getSession() {
		return sf.getCurrentSession();
	}

	@Override
	public List<T> findBy(Object... propertyAndValue) {
		final Session session = getSession();
		final Criteria crit = session.createCriteria(getEntityClass());
		for (int i = 0; i < propertyAndValue.length - 1; i = i + 2) {
			crit.add(Restrictions.eq((String) propertyAndValue[i], propertyAndValue[i + 1]));
		}
		return crit.list();
	}

	@Override
	public T findOneBy(Object... propertyAndValue) {
		final Session session = getSession();
		final Criteria crit = session.createCriteria(getEntityClass());
		for (int i = 0; i < propertyAndValue.length - 1; i = i + 2) {
			crit.add(Restrictions.eq((String) propertyAndValue[i], propertyAndValue[i + 1]));
		}
		return (T) crit.uniqueResult();
	}

	@Override
	public void deleteAll() {
		Query query = getSession().createQuery("delete from " + getEntityName());
		query.executeUpdate();
	}

	@Override
	public T findById(Integer id) {
		return (T) getSession().get(getEntityClass(), id);
	}

	@Override
	public List<T> findAll() {
		return getSession().createQuery("from " + getEntityName()).list();
	}

	protected String getEntityName() {
		return getEntityClass().getCanonicalName();
	}

	@Override
	public T findFirst() {
		Object o = getSession().createQuery("SELECT min(id) from " + getEntityName()).uniqueResult();
		if (o == null) {
			return null;
		}
		Integer id = Integer.valueOf(o.toString());
		return findById(id);
	}

	@Override
	public T findLast() {
		Object o = getSession().createQuery("SELECT max(id) from " + getEntityName()).uniqueResult();
		if (o == null) {
			return null;
		}
		Integer id = Integer.valueOf(o.toString());
		return findById(id);
	}

	@Override
	public List<T> findLast(int count) {
		final Query query = getSession().createQuery(" from " + getEntityName() + " order by id desc");
		query.setMaxResults(count);
		return query.list();
	}

	@Override
	public T save(T object) {
		if (object.getId() == null) {
			getSession().persist(object);

		} else {
			getSession().update(object);
		}
		return (T) object;
	}

	public T merge(T buildJob) {
		return (T) getSession().merge(buildJob);
	}

	@Override
	public int count() {
		return Integer.valueOf(getSession().createQuery("SELECT COUNT(*) FROM " + getEntityName()).uniqueResult().toString());

	}

	@Override
	public void delete(T object) {
		getSession().delete(object);
	}

	protected abstract Class<T> getEntityClass();

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

	public T refresh(T object) {
		return (T) getSession().get(object.getClass(), object.getId());
	}

}
