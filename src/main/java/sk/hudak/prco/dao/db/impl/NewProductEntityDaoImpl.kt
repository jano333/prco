package sk.hudak.prco.dao.db.impl

import com.querydsl.core.types.Order
import com.querydsl.core.types.OrderSpecifier
import lombok.NonNull
import org.springframework.stereotype.Component
import sk.hudak.prco.dao.db.NewProductEntityDbDao
import sk.hudak.prco.dto.product.NewProductFilterUIDto
import sk.hudak.prco.model.NewProductEntity
import sk.hudak.prco.model.QNewProductEntity
import java.util.*
import java.util.Optional.ofNullable

@Component
open class NewProductEntityDaoImpl : BaseDaoImpl<NewProductEntity>(), NewProductEntityDbDao {

    override val countOfAllNewProducts: Long
        get() = from(QNewProductEntity.newProductEntity).fetchCount()

    override fun save(entity: NewProductEntity): Long {
        entity.confirmValidity = false
        return super.save(entity)
    }

    override fun findById(id: Long): NewProductEntity {
        return findById(NewProductEntity::class.java, id)
    }

    override fun findFirstInvalid(): Optional<NewProductEntity> {
        return ofNullable(from(QNewProductEntity.newProductEntity)
                .where(QNewProductEntity.newProductEntity.valid.eq(java.lang.Boolean.FALSE))
                .limit(1)
                .fetchFirst())
    }

    override fun findByFilter(@NonNull filter: NewProductFilterUIDto): List<NewProductEntity> {
        val query = from(QNewProductEntity.newProductEntity)
        if (filter.eshopUuid != null) {
            query.where(QNewProductEntity.newProductEntity.eshopUuid.eq(filter.eshopUuid!!))
        }
        query.limit(filter.maxCount!!)
        // najnovsie najskor
        query.orderBy(OrderSpecifier(Order.DESC, QNewProductEntity.newProductEntity.created))
        return query.fetch()
    }

    override fun existWithUrl(url: String): Boolean {
        return from(QNewProductEntity.newProductEntity)
                .where(QNewProductEntity.newProductEntity.url.equalsIgnoreCase(url))
                .fetchCount() > 0
    }

    override fun findInvalid(maxCountOfInvalid: Int): List<NewProductEntity> {
        return from(QNewProductEntity.newProductEntity)
                .where(QNewProductEntity.newProductEntity.valid.eq(java.lang.Boolean.FALSE))
                .limit(maxCountOfInvalid.toLong())
                .fetch()
    }

    override fun countOfAllInvalidNewProduct(): Long {
        return from(QNewProductEntity.newProductEntity)
                .where(QNewProductEntity.newProductEntity.valid.eq(java.lang.Boolean.FALSE))
                .fetchCount()
    }

    override fun findAll(): List<NewProductEntity> {
        return from(QNewProductEntity.newProductEntity)
                .fetch()
    }
}
