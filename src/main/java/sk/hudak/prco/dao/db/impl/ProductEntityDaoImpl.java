package sk.hudak.prco.dao.db.impl;

import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQuery;
import org.springframework.stereotype.Component;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.api.ProductAction;
import sk.hudak.prco.dao.db.ProductEntityDao;
import sk.hudak.prco.dto.product.ProductFilterUIDto;
import sk.hudak.prco.model.ProductEntity;
import sk.hudak.prco.model.QProductEntity;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Component
public class ProductEntityDaoImpl extends BaseDaoImpl<ProductEntity> implements ProductEntityDao {

    public static final int OLDER_THAN_IN_HOURS = 24;

    @Override
    public ProductEntity findById(Long id) {
        return findById(ProductEntity.class, id);
    }

    @Override
    public boolean existWithUrl(String url) {
        JPAQuery<ProductEntity> query = from(QProductEntity.productEntity);
        query.where(QProductEntity.productEntity.url.equalsIgnoreCase(url));
        return query.fetchCount() > 0;
    }

    /**
     * Najde take, ktore este neboli nikde updatovane plus take ktore su starsie ako <code>olderThanInHours</code>
     *
     * @param eshopUuid
     * @param olderThanInHours pocet v hodinach, kolko minimalne sa neupdatoval dany record
     * @return
     */
    @Override
    public Optional<ProductEntity> findProductForUpdate(EshopUuid eshopUuid, int olderThanInHours) {
        JPAQuery<ProductEntity> query = from(QProductEntity.productEntity);
        query.where(QProductEntity.productEntity.eshopUuid.eq(eshopUuid));
        query.where(QProductEntity.productEntity.lastTimeDataUpdated.isNull()
                .or(QProductEntity.productEntity.lastTimeDataUpdated.lt(calculateDate(olderThanInHours))));
        query.limit(1);
        return Optional.ofNullable(query.fetchFirst());
    }

    @Override
    public List<ProductEntity> findAll() {
        //FIXME optimalizovat cez paging !!! max 500 naraz !!!
        return from(QProductEntity.productEntity).fetch();
    }

    @Override
    public List<ProductEntity> findByFilter(ProductFilterUIDto filter) {
        JPAQuery<ProductEntity> query = from(QProductEntity.productEntity);

        // EshopUuid
        if (filter.getEshopUuid() != null) {
            query.where(QProductEntity.productEntity.eshopUuid.eq(filter.getEshopUuid()));
        }
        // OnlyInAction
        if (Boolean.TRUE.equals(filter.getOnlyInAction())) {
            query.where(QProductEntity.productEntity.productAction.eq(ProductAction.IN_ACTION));
        }

        query.orderBy(new OrderSpecifier<>(Order.ASC, QProductEntity.productEntity.priceForUnit));
        return query.fetch();
    }

    @Override
    public long count() {
        return getQueryFactory()
                .select(QProductEntity.productEntity.id)
                .from(QProductEntity.productEntity)
                .fetchCount();
    }

    @Override
    public long countOfAllProductInEshop(EshopUuid eshopUuid) {
        return getQueryFactory()
                .select(QProductEntity.productEntity.id)
                .from(QProductEntity.productEntity)
                .where(QProductEntity.productEntity.eshopUuid.eq(eshopUuid))
                .fetchCount();
    }

    @Override
    public long countOfProductsAlreadyUpdated(EshopUuid eshopUuid, int olderThanInHours) {
        return getQueryFactory()
                .select(QProductEntity.productEntity.id)
                .from(QProductEntity.productEntity)
                .where(QProductEntity.productEntity.eshopUuid.eq(eshopUuid)
                        .and(QProductEntity.productEntity.lastTimeDataUpdated.isNotNull())
                        .and(QProductEntity.productEntity.lastTimeDataUpdated.gt(calculateDate(olderThanInHours)))

                )
                .fetchCount();
    }

    @Override
    public long countOfProductsWaitingToBeUpdated(EshopUuid eshopUuid, int olderThanInHours) {
        return getQueryFactory()
                .select(QProductEntity.productEntity.id)
                .from(QProductEntity.productEntity)
                .where(QProductEntity.productEntity.eshopUuid.eq(eshopUuid)
                        .and(QProductEntity.productEntity.lastTimeDataUpdated.isNull()
                                .or(QProductEntity.productEntity.lastTimeDataUpdated.gt(calculateDate(olderThanInHours)).not()))
                )
                .fetchCount();
    }

    @Override
    public long countOfAllProductInEshopUpdatedMax24Hours(EshopUuid eshopUuid) {
        return countOfProductsAlreadyUpdated(eshopUuid, OLDER_THAN_IN_HOURS);
    }

    //FIXME move to DateUtils
    private Date calculateDate(int olderThanInHours) {
        Date currentDate = new Date();
        LocalDateTime localDateTime = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime newDateTime = localDateTime.minusHours(olderThanInHours);
        return Date.from(newDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

}
