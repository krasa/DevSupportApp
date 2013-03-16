package krasa.core.backend.dao;

import krasa.core.backend.domain.AbstractEntity;

import org.springframework.stereotype.Repository;

/**
 * @author Vojtech Krasa
 */
@Repository
public class GenericDAO<T extends AbstractEntity> extends AbstractDAO<T> implements GenericDaoBuilder {

	protected Class<T> entityClass;

	public GenericDAO() {
	}

	@Override
	public <R extends AbstractEntity> GenericDAO<R> build(Class<R> domainClass) {
		GenericDAO<R> genericDAO = new GenericDAO<R>();
		genericDAO.setSf(sf);
		genericDAO.setEntityClass(domainClass);
		return genericDAO;
	}

	@Override
	protected Class<T> getEntityClass() {
		return entityClass;
	}

	public void setEntityClass(Class<T> entityClass) {
		this.entityClass = entityClass;
	}
}
