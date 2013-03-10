package krasa.core.backend.dao;

import java.util.List;

import krasa.core.backend.domain.AbstractEntity;

public interface DAO<T extends AbstractEntity> {

	void delete(T object);

	List<T> findAll();

	T findById(Integer id);

	List<T> findBy(Object... propertyAndValue);

	T findLast();

	T save(T object);

	void deleteAll();

	int count();

	T findFirst();
}
