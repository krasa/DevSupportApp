package krasa.core.backend.dao;

import krasa.core.backend.domain.AbstractEntity;

public interface GenericDaoBuilder {
	<R extends AbstractEntity> GenericDAO<R> build(Class<R> domainClass);
}
