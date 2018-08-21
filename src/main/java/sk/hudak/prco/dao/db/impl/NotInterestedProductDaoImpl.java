package sk.hudak.prco.dao.db.impl;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import org.springframework.stereotype.Repository;
import sk.hudak.prco.dao.db.NotInterestedProductDbDao;
import sk.hudak.prco.model.NotInterestedProductEntity;
import sk.hudak.prco.model.QNotInterestedProductEntity;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class NotInterestedProductDaoImpl extends BaseDaoImpl<NotInterestedProductEntity> implements NotInterestedProductDbDao {

    @Override
    public NotInterestedProductEntity findById(Long id) {
        return findById(NotInterestedProductEntity.class, id);
    }

    @Override
    public boolean existWithUrl(String url) {
        return from(QNotInterestedProductEntity.notInterestedProductEntity)
                .where(QNotInterestedProductEntity.notInterestedProductEntity.url.equalsIgnoreCase(url))
                .fetchCount() > 0;
    }

    @Override
    public List<NotInterestedProductEntity> findAll() {
        return from(QNotInterestedProductEntity.notInterestedProductEntity).fetch();
    }

    @Override
    public List<String> findFistTenURL() {
        return from(QNotInterestedProductEntity.notInterestedProductEntity)
                .limit(10)
                .orderBy(new OrderSpecifier<>(Order.DESC, QNotInterestedProductEntity.notInterestedProductEntity.created))
                .fetch()
                .stream().map(entity -> entity.getUrl()).collect(Collectors.toList());

    }
}
