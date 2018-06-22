package sk.hudak.prco.dao;

import sk.hudak.prco.model.core.DbEntity;

public interface BaseDao<T extends DbEntity> {

    Long save(T entity);

    void update(T entity);

    T findById(Long id);

    void delete(T entity);
}
