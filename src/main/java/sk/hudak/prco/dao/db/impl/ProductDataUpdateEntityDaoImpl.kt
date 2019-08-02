package sk.hudak.prco.dao.db.impl

import org.springframework.stereotype.Component
import sk.hudak.prco.dao.db.ProductDataUpdateEntityDao
import sk.hudak.prco.model.ProductDataUpdateEntity
import javax.persistence.EntityManager

@Component
open class ProductDataUpdateEntityDaoImpl(em: EntityManager)
    : BaseDaoImpl<ProductDataUpdateEntity>(em), ProductDataUpdateEntityDao {

    override fun findById(id: Long): ProductDataUpdateEntity {
        return findById(ProductDataUpdateEntity::class.java, id)
    }
}
