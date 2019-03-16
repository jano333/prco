package sk.hudak.prco.dao.db.impl;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.dao.db.GroupEntityDao;
import sk.hudak.prco.dao.db.GroupOfProductFindEntityDao;
import sk.hudak.prco.dao.db.ProductEntityDao;
import sk.hudak.prco.dto.group.GroupFilterDto;
import sk.hudak.prco.exception.PrcoRuntimeException;
import sk.hudak.prco.model.GroupEntity;
import sk.hudak.prco.model.ProductEntity;
import sk.hudak.prco.model.QGroupEntity;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
public class GroupEntityDaoImpl extends BaseDaoImpl<GroupEntity> implements GroupEntityDao {

    @Autowired
    private ProductEntityDao productEntityDao;

    @Autowired
    private GroupOfProductFindEntityDao groupOfProductFindEntityDao;

    @Override
    public GroupEntity findById(Long id) {
        return findById(GroupEntity.class, id);
    }

    @Override
    public List<GroupEntity> findGroups(GroupFilterDto groupFilterDto) {
        JPAQuery<GroupEntity> query = from(QGroupEntity.groupEntity);
        // ids
        Long[] ids = groupFilterDto.getIds();
        if (ids != null) {
            query.where(QGroupEntity.groupEntity.id.in(ids));
        }
        // name
        String name = groupFilterDto.getName();
        if (StringUtils.isNotBlank(name)) {
            query.where(QGroupEntity.groupEntity.name.isNotNull());
            query.where(QGroupEntity.groupEntity.name.eq(name));
        }
        // eshop only
        EshopUuid eshopOnly = groupFilterDto.getEshopOnly();
        if (eshopOnly != null) {
            //TODO impl
        }
        query.orderBy(new OrderSpecifier<>(Order.ASC, QGroupEntity.groupEntity.name));
        return query.fetch();
    }

    @Override
    public List<GroupEntity> findGroupsWithoutProduct(Long productId) {
        //FIXME optimalizovat
        ProductEntity byId = productEntityDao.findById(productId);
        JPAQuery<GroupEntity> query = from(QGroupEntity.groupEntity);
        query.where(QGroupEntity.groupEntity.products.contains(byId).not());
        query.orderBy(new OrderSpecifier<>(Order.ASC, QGroupEntity.groupEntity.name));
        return query.distinct().fetch();
    }

    @Override
    public Optional<GroupEntity> findGroupByName(String groupName) {
        JPAQuery<GroupEntity> query = from(QGroupEntity.groupEntity);
        query.where(QGroupEntity.groupEntity.name.eq(groupName));
        return Optional.ofNullable(query.fetchFirst());
    }

    @Override
    public boolean existGroupByName(String groupName, Long groupIdToSkip) {
        getQueryFactory()
                .select(QGroupEntity.groupEntity.id)
                .from(QGroupEntity.groupEntity)
                .where(QGroupEntity.groupEntity.id.ne(groupIdToSkip))
                .where(QGroupEntity.groupEntity.name.eq(groupName))
                .exists();


        JPAQuery<GroupEntity> query = from(QGroupEntity.groupEntity);
        if (groupIdToSkip != null) {
            query.where(QGroupEntity.groupEntity.id.ne(groupIdToSkip));
        }
        query.where(QGroupEntity.groupEntity.name.eq(groupName));
        //FIXME optimalizovat na exist...
        return query.fetchFirst() != null;
    }

    @Override
    public List<ProductEntity> findProductsInGroup(Long groupId, boolean withPriceOnly, EshopUuid... eshopsToSkip) {
        JPAQuery<GroupEntity> query = from(QGroupEntity.groupEntity);
        query.where(QGroupEntity.groupEntity.id.eq(groupId));
        GroupEntity groupEntity = query.fetchFirst();
        if (groupEntity == null) {
            throw new PrcoRuntimeException(GroupEntity.class.getSimpleName() + " not found by id " + groupId);
        }
        //FIXME cez DB urobit neaky komplikovany select..
        List<ProductEntity> products = groupEntity.getProducts();
        //filter na tie, ktore maju cenu
        Stream<ProductEntity> productEntityStream = products.stream();
        if (withPriceOnly) {
            productEntityStream = productEntityStream.filter(p -> p.getPriceForUnit() != null);
        }
        if (eshopsToSkip != null) {
            productEntityStream = productEntityStream.filter(p -> !Arrays.asList(eshopsToSkip).contains(p.getEshopUuid()));
        }

        List<ProductEntity> withValidPriceForPackage = productEntityStream.collect(Collectors.toList());

        // FIXME cez db query
//        Collections.sort(withValidPriceForPackage, Comparator.comparing(ProductEntity::getPriceForUnit));
        Collections.sort(withValidPriceForPackage, Comparator.comparing(
                productEntity -> productEntity.getPriceForUnit() != null ? productEntity.getPriceForUnit() : BigDecimal.ZERO)
        );

        return withValidPriceForPackage;
    }

    @Override
    public List<String> findAllGroupNames() {
        return getQueryFactory()
                .select(QGroupEntity.groupEntity.name)
                .from(QGroupEntity.groupEntity)
                .fetch();
    }

    @Override
    public List<GroupEntity> findGroupsForProduct(Long productId) {
        return getQueryFactory()
                .select(QGroupEntity.groupEntity)
                .from(QGroupEntity.groupEntity)
                .where(QGroupEntity.groupEntity.id.in(groupOfProductFindEntityDao.findGroupIdsWithProductId(productId)))
                .fetch();
    }


}
