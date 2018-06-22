package sk.hudak.prco.dao.db.impl;

import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.stereotype.Component;
import sk.hudak.prco.dao.db.NewProductEntityDbDao;
import sk.hudak.prco.dto.newproduct.NewProductFilterUIDto;
import sk.hudak.prco.model.NewProductEntity;
import sk.hudak.prco.model.QNewProductEntity;

import java.util.List;
import java.util.Optional;

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
        JPAQuery<NewProductEntity> query = from(QNewProductEntity.newProductEntity);
        query.where(QNewProductEntity.newProductEntity.valid.eq(Boolean.FALSE));
        query.limit(1);
        return Optional.ofNullable(query.fetchFirst());
    }

    @Override
    public List<NewProductEntity> findByFilter(NewProductFilterUIDto filter) {
        JPAQuery<NewProductEntity> query = from(QNewProductEntity.newProductEntity);
        //TODO filter applied
        return query.fetch();
    }

    @Override
    public boolean existWithUrl(String url) {
        JPAQuery<NewProductEntity> query = from(QNewProductEntity.newProductEntity);
        query.where(QNewProductEntity.newProductEntity.url.equalsIgnoreCase(url));
        return query.fetchCount() > 0;
    }

    @Override
    public List<NewProductEntity> findInvalid(int maxCountOfInvalid) {
        JPAQuery<NewProductEntity> query = from(QNewProductEntity.newProductEntity);
        query.where(QNewProductEntity.newProductEntity.valid.eq(Boolean.FALSE));
        query.limit(maxCountOfInvalid);
        return query.fetch();
    }

    @Override
    public long countOfAllInvalidNewProduct() {
        JPAQuery<NewProductEntity> query = from(QNewProductEntity.newProductEntity);
        query.where(QNewProductEntity.newProductEntity.valid.eq(Boolean.FALSE));
        return query.fetchCount();
    }

    @Override
    public List<NewProductEntity> findAll() {
        return from(QNewProductEntity.newProductEntity).fetch();
    }
}
