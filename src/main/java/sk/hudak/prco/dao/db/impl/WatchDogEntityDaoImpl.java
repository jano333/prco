package sk.hudak.prco.dao.db.impl;

import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.stereotype.Component;
import sk.hudak.prco.dao.db.WatchDogEntityDao;
import sk.hudak.prco.model.QWatchDogEntity;
import sk.hudak.prco.model.WatchDogEntity;

import java.util.List;

@Component
public class WatchDogEntityDaoImpl extends BaseDaoImpl<WatchDogEntity> implements WatchDogEntityDao {

    @Override
    public WatchDogEntity findById(Long id) {
        return findById(WatchDogEntity.class, id);
    }

    @Override
    public List<WatchDogEntity> findAll() {
        return from(QWatchDogEntity.watchDogEntity).fetch();
    }

    @Override
    public boolean existWithUrl(String productUrl) {
        JPAQuery<WatchDogEntity> query = from(QWatchDogEntity.watchDogEntity);
        query.where(QWatchDogEntity.watchDogEntity.productUrl.eq(productUrl));
        return query.fetchCount() == 1;
    }
}
