package sk.hudak.prco.dao.db.impl

import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import sk.hudak.prco.dao.db.GroupOfProductFindEntityDao
import sk.hudak.prco.model.ProductEntity
import sk.hudak.prco.model.QGroupEntity
import sk.hudak.prco.model.QGroupOfProductFindEntity
import sk.hudak.prco.model.QProductEntity
import sk.hudak.prco.utils.DateUtils
import java.util.*
import javax.persistence.EntityManager

@Repository
open class GroupOfProductFindEntityDaoImpl(private val em: EntityManager) : GroupOfProductFindEntityDao {

    private fun getQueryFactory() = JPAQueryFactory(em)

    override fun findProductsWitchAreNotInAnyGroup(applyExpiration: Boolean): List<ProductEntity> {
        val queryFactory = getQueryFactory()
        return queryFactory
                .select(QProductEntity.productEntity)
                .from(QProductEntity.productEntity)
                .where(QProductEntity.productEntity.id.notIn(
                        queryFactory
                                .select(QGroupOfProductFindEntity.groupOfProductFindEntity.productId)
                                .from(QGroupOfProductFindEntity.groupOfProductFindEntity)
                                .distinct()
                ).and(QProductEntity.productEntity.lastTimeDataUpdated.isNull
                        .or(QProductEntity.productEntity.lastTimeDataUpdated.lt(DateUtils.calculateDate(12))))
                )
                .orderBy(OrderSpecifier(Order.DESC, QProductEntity.productEntity.created))
                .fetch()
    }

    override fun findProductsWitchAreNotInAnyGroup(): List<ProductEntity> {
        val queryFactory = getQueryFactory()
        return queryFactory
                .select(QProductEntity.productEntity)
                .from(QProductEntity.productEntity)
                .where(QProductEntity.productEntity.id.notIn(findIdsOfProductWhichAreInAtLeastOneGroup(queryFactory)))
                .orderBy(OrderSpecifier(Order.DESC, QProductEntity.productEntity.created))
                .fetch()
    }

    override fun countOfProductsWitchAreNotInAnyGroup(): Long {
        val queryFactory = getQueryFactory()
        return queryFactory
                .select(QProductEntity.productEntity)
                .from(QProductEntity.productEntity)
                .where(QProductEntity.productEntity.id.notIn(findIdsOfProductWhichAreInAtLeastOneGroup(queryFactory)))
                .fetchCount()
    }

    private fun findIdsOfProductWhichAreInAtLeastOneGroup(queryFactory: JPAQueryFactory): List<Long> {
        return queryFactory
                .select(QGroupOfProductFindEntity.groupOfProductFindEntity.productId)
                .from(QGroupOfProductFindEntity.groupOfProductFindEntity)
                .distinct()
                .fetch()
    }

    override fun countOfProductInGroup(groupName: String): Long {
        return getQueryFactory()
                .select(QGroupEntity.groupEntity)
                .from(QGroupEntity.groupEntity)
                .where(QGroupEntity.groupEntity.name.eq(groupName))
                .fetchFirst().products.size.toLong()
    }

    override fun findFirstProductGroupId(productId: Long?): Optional<Long> {
        return Optional.ofNullable(getQueryFactory()
                .select(QGroupOfProductFindEntity.groupOfProductFindEntity.groupId)
                .from(QGroupOfProductFindEntity.groupOfProductFindEntity)
                .where(QGroupOfProductFindEntity.groupOfProductFindEntity.productId.eq(productId!!))
                .fetchFirst())
    }

    override fun findGroupIdsWithProductId(productId: Long): List<Long> {
        return getQueryFactory()
                .select(QGroupOfProductFindEntity.groupOfProductFindEntity.groupId)
                .from(QGroupOfProductFindEntity.groupOfProductFindEntity)
                .where(QGroupOfProductFindEntity.groupOfProductFindEntity.productId.eq(productId))
                .fetch()
    }

}
