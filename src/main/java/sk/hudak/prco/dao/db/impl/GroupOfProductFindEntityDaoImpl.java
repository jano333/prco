package sk.hudak.prco.dao.db.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sk.hudak.prco.dao.db.GroupOfProductFindEntityDao;
import sk.hudak.prco.model.ProductEntity;
import sk.hudak.prco.model.QGroupEntity;
import sk.hudak.prco.model.QGroupOfProductFindEntity;
import sk.hudak.prco.model.QProductEntity;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

@Component
public class GroupOfProductFindEntityDaoImpl implements GroupOfProductFindEntityDao {

    @Autowired
    private EntityManager em;

    @Override
    public List<ProductEntity> findProductsWitchAreNotInAnyGroup() {
        JPAQueryFactory queryFactory = getQueryFactory();
        return queryFactory
                .select(QProductEntity.productEntity)
                .from(QProductEntity.productEntity)
                .where(QProductEntity.productEntity.id.notIn(getCountOfProductWhichAreInAtLeastOneGroup(queryFactory)))
                .fetch();
    }

    @Override
    public long countOfProductsWitchAreNotInAnyGroup() {
        JPAQueryFactory queryFactory = getQueryFactory();
        return queryFactory
                .select(QProductEntity.productEntity)
                .from(QProductEntity.productEntity)
                .where(QProductEntity.productEntity.id.notIn(getCountOfProductWhichAreInAtLeastOneGroup(queryFactory)))
                .fetchCount();
    }

    private List<Long> getCountOfProductWhichAreInAtLeastOneGroup(JPAQueryFactory queryFactory) {
        return queryFactory
                .select(QGroupOfProductFindEntity.groupOfProductFindEntity.productId)
                .from(QGroupOfProductFindEntity.groupOfProductFindEntity)
                .distinct()
                .fetch();
    }

    @Override
    public long countOfProductInGroup(String groupName) {
        return getQueryFactory()
                .select(QGroupEntity.groupEntity)
                .from(QGroupEntity.groupEntity)
                .where(QGroupEntity.groupEntity.name.eq(groupName))
                .fetchFirst().getProducts().size();
    }

    @Override
    public Optional<Long> findFirstProductGroupId(Long productId) {
        return Optional.ofNullable(getQueryFactory()
                .select(QGroupOfProductFindEntity.groupOfProductFindEntity.groupId)
                .from(QGroupOfProductFindEntity.groupOfProductFindEntity)
                .where(QGroupOfProductFindEntity.groupOfProductFindEntity.productId.eq(productId))
                .fetchFirst());
    }

    @Override
    public List<Long> findGroupIdsWithProductId(Long productId) {
        return getQueryFactory()
                .select(QGroupOfProductFindEntity.groupOfProductFindEntity.groupId)
                .from(QGroupOfProductFindEntity.groupOfProductFindEntity)
                .where(QGroupOfProductFindEntity.groupOfProductFindEntity.productId.eq(productId))
                .fetch();
    }

    private JPAQueryFactory getQueryFactory() {
        return new JPAQueryFactory(em);
    }

}
