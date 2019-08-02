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
import java.util.*
import javax.persistence.EntityManager

@Repository
open class GroupOfProductFindEntityDaoImpl(em: EntityManager) : GroupOfProductFindEntityDao {

    private val queryFactory: JPAQueryFactory = JPAQueryFactory(em)

    override fun findProductsWitchAreNotInAnyGroup(): List<ProductEntity> {
        val queryFactory = queryFactory
        return queryFactory
                .select(QProductEntity.productEntity)
                .from(QProductEntity.productEntity)
                .where(QProductEntity.productEntity.id.notIn(getCountOfProductWhichAreInAtLeastOneGroup(queryFactory)))
                .orderBy(OrderSpecifier(Order.DESC, QProductEntity.productEntity.created))
                .fetch()
    }

    override fun countOfProductsWitchAreNotInAnyGroup(): Long {
        val queryFactory = queryFactory
        return queryFactory
                .select(QProductEntity.productEntity)
                .from(QProductEntity.productEntity)
                .where(QProductEntity.productEntity.id.notIn(getCountOfProductWhichAreInAtLeastOneGroup(queryFactory)))
                .fetchCount()
    }

    private fun getCountOfProductWhichAreInAtLeastOneGroup(queryFactory: JPAQueryFactory): List<Long> {
        return queryFactory
                .select(QGroupOfProductFindEntity.groupOfProductFindEntity.productId)
                .from(QGroupOfProductFindEntity.groupOfProductFindEntity)
                .distinct()
                .fetch()
    }

    override fun countOfProductInGroup(groupName: String): Long {
        return queryFactory
                .select(QGroupEntity.groupEntity)
                .from(QGroupEntity.groupEntity)
                .where(QGroupEntity.groupEntity.name.eq(groupName))
                .fetchFirst().products.size.toLong()
    }

    override fun findFirstProductGroupId(productId: Long?): Optional<Long> {
        return Optional.ofNullable(queryFactory
                .select(QGroupOfProductFindEntity.groupOfProductFindEntity.groupId)
                .from(QGroupOfProductFindEntity.groupOfProductFindEntity)
                .where(QGroupOfProductFindEntity.groupOfProductFindEntity.productId.eq(productId!!))
                .fetchFirst())
    }

    override fun findGroupIdsWithProductId(productId: Long?): List<Long> {
        return queryFactory
                .select(QGroupOfProductFindEntity.groupOfProductFindEntity.groupId)
                .from(QGroupOfProductFindEntity.groupOfProductFindEntity)
                .where(QGroupOfProductFindEntity.groupOfProductFindEntity.productId.eq(productId!!))
                .fetch()
    }

}
