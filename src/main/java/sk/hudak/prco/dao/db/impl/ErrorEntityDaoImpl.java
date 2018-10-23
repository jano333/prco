package sk.hudak.prco.dao.db.impl;

import org.springframework.stereotype.Component;
import sk.hudak.prco.api.ErrorType;
import sk.hudak.prco.dao.db.ErrorEntityDao;
import sk.hudak.prco.exception.PrcoRuntimeException;
import sk.hudak.prco.model.ErrorEntity;
import sk.hudak.prco.model.QErrorEntity;

import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

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

    @Override
    public List<ErrorEntity> findOlderThan(int unitCount, TimeUnit timeUnit) {
        return getQueryFactory()
                .select(QErrorEntity.errorEntity)
                .from(QErrorEntity.errorEntity)
                .where(QErrorEntity.errorEntity.updated.lt(calculateDate(unitCount, timeUnit)))
                .fetch();
    }

    private Date calculateDate(int unitCount, TimeUnit timeUnit) {
        TemporalUnit unit = null;
        switch (timeUnit) {
            case DAYS:
                unit = ChronoUnit.DAYS;
                break;
            //TODO ostatne
            default:
                throw new PrcoRuntimeException("Not yet implemented");
        }
        return Date.from(new Date().toInstant().minus(unitCount, unit));
    }

    @Override
    public List<ErrorEntity> findByUrls(List<String> urls) {
        return getQueryFactory()
                .select(QErrorEntity.errorEntity)
                .from(QErrorEntity.errorEntity)
                .where(QErrorEntity.errorEntity.url.in(urls.toArray(new String[urls.size()])))
                .fetch();
    }
}
