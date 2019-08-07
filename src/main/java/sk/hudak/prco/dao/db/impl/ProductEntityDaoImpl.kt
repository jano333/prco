package sk.hudak.prco.dao.db.impl

import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import org.springframework.stereotype.Repository
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.ProductAction
import sk.hudak.prco.dao.db.ProductEntityDao
import sk.hudak.prco.dto.product.ProductFilterUIDto
import sk.hudak.prco.model.ProductEntity
import sk.hudak.prco.model.QProductEntity
import java.time.ZoneId
import java.util.*
import java.util.Optional.ofNullable
import javax.persistence.EntityManager

@Repository
open class ProductEntityDaoImpl(em: EntityManager) : BaseDaoImpl<ProductEntity>(em), ProductEntityDao {


    companion object {
        val OLDER_THAN_IN_HOURS = 24
    }

    override fun findById(id: Long): ProductEntity =
            findById(ProductEntity::class.java, id)


    override fun existWithUrl(url: String): Boolean =
            from(QProductEntity.productEntity)
                    .where(QProductEntity.productEntity.url.equalsIgnoreCase(url))
                    .fetchCount() > 0

    /**
     * Najde take, ktore este neboli nikde updatovane plus take ktore su starsie ako `olderThanInHours`
     *
     * @param eshopUuid
     * @param olderThanInHours pocet v hodinach, kolko minimalne sa neupdatoval dany record
     * @return
     */
    override fun findProductForUpdate(eshopUuid: EshopUuid, olderThanInHours: Int): Optional<ProductEntity> {
        return ofNullable(from(QProductEntity.productEntity)
                .where(QProductEntity.productEntity.eshopUuid.eq(eshopUuid))
                .where(QProductEntity.productEntity.lastTimeDataUpdated.isNull
                        .or(QProductEntity.productEntity.lastTimeDataUpdated.lt(calculateDate(olderThanInHours))))
                .limit(1)
                .fetchFirst())
    }

    override fun findAll(): List<ProductEntity> {
        //FIXME optimalizovat cez paging !!! max 500 naraz !!!
        return from(QProductEntity.productEntity).fetch()
    }

    override fun findByFilter(filter: ProductFilterUIDto): List<ProductEntity> {
        val query = from(QProductEntity.productEntity)

        // EshopUuid
        if (filter.eshopUuid != null) {
            query.where(QProductEntity.productEntity.eshopUuid.eq(filter.eshopUuid!!))
        }
        // OnlyInAction
        if (java.lang.Boolean.TRUE == filter.onlyInAction) {
            query.where(QProductEntity.productEntity.productAction.eq(ProductAction.IN_ACTION))
        }

        query.orderBy(OrderSpecifier(Order.ASC, QProductEntity.productEntity.priceForUnit))
        return query.fetch()
    }

    override fun findByUrl(productUrl: String): ProductEntity? {
        return from(QProductEntity.productEntity)
                .where(QProductEntity.productEntity.url.eq(productUrl))
                .fetchFirst()
    }

    override fun findByCount(eshopUuid: EshopUuid, maxCountToDelete: Long): List<ProductEntity> {
        return from(QProductEntity.productEntity)
                .where(QProductEntity.productEntity.eshopUuid.eq(eshopUuid))
                .limit(maxCountToDelete)
                .fetch()
    }


    override fun count(): Long {
        return queryFactory
                .select(QProductEntity.productEntity.id)
                .from(QProductEntity.productEntity)
                .fetchCount()
    }

    override fun countOfAllProductInEshop(eshopUuid: EshopUuid): Long {
        return queryFactory
                .select(QProductEntity.productEntity.id)
                .from(QProductEntity.productEntity)
                .where(QProductEntity.productEntity.eshopUuid.eq(eshopUuid))
                .fetchCount()
    }

    override fun countOfProductsAlreadyUpdated(eshopUuid: EshopUuid, olderThanInHours: Int): Long {
        return queryFactory
                .select(QProductEntity.productEntity.id)
                .from(QProductEntity.productEntity)
                .where(QProductEntity.productEntity.eshopUuid.eq(eshopUuid)
                        .and(QProductEntity.productEntity.lastTimeDataUpdated.isNotNull)
                        .and(QProductEntity.productEntity.lastTimeDataUpdated.gt(calculateDate(olderThanInHours)))

                )
                .fetchCount()
    }

    override fun countOfProductsWaitingToBeUpdated(eshopUuid: EshopUuid, olderThanInHours: Int): Long {
        return queryFactory
                .select(QProductEntity.productEntity.id)
                .from(QProductEntity.productEntity)
                .where(QProductEntity.productEntity.eshopUuid.eq(eshopUuid)
                        .and(QProductEntity.productEntity.lastTimeDataUpdated.isNull
                                .or(QProductEntity.productEntity.lastTimeDataUpdated.gt(calculateDate(olderThanInHours)).not()))
                )
                .fetchCount()
    }

    override fun countOfAllProductInEshopUpdatedMax24Hours(eshopUuid: EshopUuid): Long {
        return countOfProductsAlreadyUpdated(eshopUuid, OLDER_THAN_IN_HOURS)
    }

    override fun getProductWithUrl(productUrl: String, productIdToIgnore: Long?): Optional<Long> {
        return ofNullable(queryFactory
                .select(QProductEntity.productEntity.id)
                .from(QProductEntity.productEntity)
                .where(QProductEntity.productEntity.url.eq(productUrl)
                        .and(QProductEntity.productEntity.id.ne(productIdToIgnore!!)))
                .fetchFirst())
    }


    //FIXME move to DateUtils
    private fun calculateDate(olderThanInHours: Int): Date {
        val currentDate = Date()
        val localDateTime = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
        val newDateTime = localDateTime.minusHours(olderThanInHours.toLong())
        return Date.from(newDateTime.atZone(ZoneId.systemDefault()).toInstant())
    }



}
