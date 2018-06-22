package sk.hudak.prco.dao.db.impl;

import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.stereotype.Repository;
import sk.hudak.prco.dao.db.NotInterestedProductDbDao;
import sk.hudak.prco.model.NotInterestedProductEntity;
import sk.hudak.prco.model.QNotInterestedProductEntity;

import java.util.List;

@Repository
public class NotInterestedProductDaoImpl extends BaseDaoImpl<NotInterestedProductEntity> implements NotInterestedProductDbDao {

    @Override
    public NotInterestedProductEntity findById(Long id) {
        return findById(NotInterestedProductEntity.class, id);
    }

    @Override
    public boolean existWithUrl(String url) {
        JPAQuery<NotInterestedProductEntity> query = from(QNotInterestedProductEntity.notInterestedProductEntity);
        query.where(QNotInterestedProductEntity.notInterestedProductEntity.url.equalsIgnoreCase(url));
        return query.fetchCount() > 0;
    }

    @Override
    public List<NotInterestedProductEntity> findAll() {
        return from(QNotInterestedProductEntity.notInterestedProductEntity).fetch();
    }
}
