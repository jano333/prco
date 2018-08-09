package sk.hudak.prco.dao.db.impl;

import org.springframework.stereotype.Component;
import sk.hudak.prco.api.ErrorType;
import sk.hudak.prco.dao.db.ErrorEntityDao;
import sk.hudak.prco.model.ErrorEntity;
import sk.hudak.prco.model.QErrorEntity;

import java.util.List;

@Component
public class ErrorEntityDaoImpl extends BaseDaoImpl<ErrorEntity> implements ErrorEntityDao {

    @Override
    public ErrorEntity findById(Long id) {
        return findById(ErrorEntity.class, id);
    }

    @Override
    public ErrorEntity findByUrl(String url) {
        return getQueryFactory()
                .select(QErrorEntity.errorEntity)
                .from(QErrorEntity.errorEntity)
                .where(QErrorEntity.errorEntity.url.eq(url))
                .fetchFirst();
    }

    @Override
    public List<ErrorEntity> findAll() {
        return getQueryFactory()
                .select(QErrorEntity.errorEntity)
                .from(QErrorEntity.errorEntity)
                .fetch();
    }

    @Override
    public List<ErrorEntity> findByTypes(ErrorType... errorTypes) {
        return getQueryFactory()
                .select(QErrorEntity.errorEntity)
                .from(QErrorEntity.errorEntity)
                .where(QErrorEntity.errorEntity.errorType.in(errorTypes))
                .fetch();
    }

    @Override
    public Long getCountOfType(ErrorType type) {
        return getQueryFactory()
                .select(QErrorEntity.errorEntity)
                .from(QErrorEntity.errorEntity)
                .where(QErrorEntity.errorEntity.errorType.eq(type))
                .fetchCount();
    }
}
