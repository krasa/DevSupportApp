package krasa.core.backend.dao;

import krasa.core.backend.domain.AbstractEntity;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.springframework.stereotype.Service;

@Service
public abstract class CommonDAO extends AbstractDAO {

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

}
