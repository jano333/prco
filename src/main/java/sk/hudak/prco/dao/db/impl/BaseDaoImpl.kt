package sk.hudak.prco.dao.db.impl;

import com.querydsl.core.types.EntityPath;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import sk.hudak.prco.dao.BaseDao;
import sk.hudak.prco.exception.PrcoRuntimeException;
import sk.hudak.prco.model.core.DbEntity;

import javax.persistence.EntityManager;
import java.util.Date;

public abstract class BaseDaoImpl<T extends DbEntity> implements BaseDao<T> {

    @Autowired
    protected EntityManager em;

    @Override
    public Long save(T entity) {
        entity.setCreated(new Date());
        entity.setUpdated(entity.getCreated());
        em.persist(entity);
        return entity.getId();
    }

    @Override
    public void update(T entity) {
        entity.setUpdated(new Date());
        em.merge(entity);
    }

    protected T findById(Class<T> clazz, Long id) {
        T t = em.find(clazz, id);
        if (t == null) {
            throw new PrcoRuntimeException("Entity " + clazz.getSimpleName() + " not found by id " + id);
        }
        return t;
    }

    @Override
    public void delete(T entity) {
        em.remove(entity);
    }

    protected JPAQuery<T> from(EntityPath<T> from) {
        //FIXME skusit s jednou instanciou factory...
        return new JPAQueryFactory(em).selectFrom(from);
    }

    protected JPAQueryFactory getQueryFactory() {
        return new JPAQueryFactory(em);
    }
}
