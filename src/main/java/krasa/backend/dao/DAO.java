package krasa.backend.dao;

import krasa.backend.domain.AbstractEntity;

import java.util.List;

public interface DAO<T extends AbstractEntity> {

    void delete(T object);

    List<T> findAll();

    T findById(Integer id);

    T findLast();

    T save(T object);

    void deleteAll();

    int count();

    T findFirst();
}
