package sk.hudak.prco.dao.db.impl

import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import org.springframework.stereotype.Repository
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dao.db.NotInterestedProductDbDao
import sk.hudak.prco.dto.product.NotInterestedProductFindDto
import sk.hudak.prco.model.NotInterestedProductEntity
import sk.hudak.prco.model.QNotInterestedProductEntity
import javax.persistence.EntityManager

@Repository
open class NotInterestedProductDaoImpl(em: EntityManager)
    : BaseDaoImpl<NotInterestedProductEntity>(em), NotInterestedProductDbDao {

    override fun countOfAll(): Long =
            from(QNotInterestedProductEntity.notInterestedProductEntity).fetchCount()


    override fun findById(id: Long): NotInterestedProductEntity =
            findById(NotInterestedProductEntity::class.java, id)

    override fun existWithUrl(url: String): Boolean {
        return from(QNotInterestedProductEntity.notInterestedProductEntity)
                .where(QNotInterestedProductEntity.notInterestedProductEntity.url.equalsIgnoreCase(url))
                .fetchCount() > 0
    }

    override fun findAll(findDto: NotInterestedProductFindDto): List<NotInterestedProductEntity> {
        val from = from(QNotInterestedProductEntity.notInterestedProductEntity)
        if (findDto.eshopUuid != null) {
            from.where(QNotInterestedProductEntity.notInterestedProductEntity.eshopUuid.eq(findDto.eshopUuid))
        }
        return from.fetch()
    }

    override fun findFistTenURL(): List<String> {
        return queryFactory
                .select(QNotInterestedProductEntity.notInterestedProductEntity.url)
                .from(QNotInterestedProductEntity.notInterestedProductEntity)
                .limit(10)
                .orderBy(OrderSpecifier(Order.DESC, QNotInterestedProductEntity.notInterestedProductEntity.created))
                .fetch()
    }

    override fun findByCount(eshopUuid: EshopUuid, maxCountToDelete: Long): List<NotInterestedProductEntity> {
        return from(QNotInterestedProductEntity.notInterestedProductEntity)
                .where(QNotInterestedProductEntity.notInterestedProductEntity.eshopUuid.eq(eshopUuid))
                .limit(maxCountToDelete)
                .fetch()
    }

    override fun countOfAllProductInEshop(eshopUuid: EshopUuid): Long {
        return from(QNotInterestedProductEntity.notInterestedProductEntity)
                .where(QNotInterestedProductEntity.notInterestedProductEntity.eshopUuid.eq(eshopUuid))
                .fetchCount()
    }
}
