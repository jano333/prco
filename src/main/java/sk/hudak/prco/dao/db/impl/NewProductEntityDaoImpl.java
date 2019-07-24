package sk.hudak.prco.dao.db.impl;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import lombok.NonNull;
import org.springframework.stereotype.Component;
import sk.hudak.prco.dao.db.NewProductEntityDbDao;
import sk.hudak.prco.dto.newproduct.NewProductFilterUIDto;
import sk.hudak.prco.model.NewProductEntity;
import sk.hudak.prco.model.QNewProductEntity;

import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

@Component
public class NewProductEntityDaoImpl extends BaseDaoImpl<NewProductEntity> implements NewProductEntityDbDao {

    @Override
    public Long save(NewProductEntity entity) {
        entity.setConfirmValidity(false);
        return super.save(entity);
    }

    @Override
    public NewProductEntity findById(Long id) {
        return findById(NewProductEntity.class, id);
    }

    @Override
    public Optional<NewProductEntity> findFirstInvalid() {
        return ofNullable(from(QNewProductEntity.newProductEntity)
                .where(QNewProductEntity.newProductEntity.valid.eq(Boolean.FALSE))
                .limit(1)
                .fetchFirst());
    }

    @Override
    public List<NewProductEntity> findByFilter(@NonNull NewProductFilterUIDto filter) {
        JPAQuery<NewProductEntity> query = from(QNewProductEntity.newProductEntity);
        if (filter.getEshopUuid() != null) {
            query.where(QNewProductEntity.newProductEntity.eshopUuid.eq(filter.getEshopUuid()));
        }
        query.limit(filter.getMaxCount());
        // najnovsie najskor
        query.orderBy(new OrderSpecifier<>(Order.DESC, QNewProductEntity.newProductEntity.created));
        return query.fetch();
    }

    @Override
    public long getCountOfAllNewProducts() {
        return from(QNewProductEntity.newProductEntity)
                .fetchCount();
    }

    @Override
    public boolean existWithUrl(String url) {
        return from(QNewProductEntity.newProductEntity)
                .where(QNewProductEntity.newProductEntity.url.equalsIgnoreCase(url))
                .fetchCount() > 0;
    }

    @Override
    public List<NewProductEntity> findInvalid(int maxCountOfInvalid) {
        return from(QNewProductEntity.newProductEntity)
                .where(QNewProductEntity.newProductEntity.valid.eq(Boolean.FALSE))
                .limit(maxCountOfInvalid)
                .fetch();
    }

    @Override
    public long countOfAllInvalidNewProduct() {
        return from(QNewProductEntity.newProductEntity)
                .where(QNewProductEntity.newProductEntity.valid.eq(Boolean.FALSE))
                .fetchCount();
    }

    @Override
    public List<NewProductEntity> findAll() {
        return from(QNewProductEntity.newProductEntity)
                .fetch();
    }
}
