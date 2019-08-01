package sk.hudak.prco.dao.db

import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dao.BaseDao
import sk.hudak.prco.dto.product.ProductFilterUIDto
import sk.hudak.prco.model.ProductEntity
import java.util.*

interface ProductEntityDao : BaseDao<ProductEntity> {

    fun existWithUrl(url: String): Boolean

    /**
     * @param eshopUuid
     * @param olderThanInHours pocet v hodinach, kolko minimalne sa neupdatoval dany record
     * @return
     */
    fun findProductForUpdate(eshopUuid: EshopUuid, olderThanInHours: Int): Optional<ProductEntity>

    fun findAll(): List<ProductEntity>

    fun findByFilter(filter: ProductFilterUIDto): List<ProductEntity>

    fun findByUrl(productUrl: String): Optional<ProductEntity>

    fun count(): Long

    fun countOfAllProductInEshop(eshopUuid: EshopUuid): Long

    fun countOfAllProductInEshopUpdatedMax24Hours(eshopUuid: EshopUuid): Long

    fun countOfProductsWaitingToBeUpdated(eshopUuid: EshopUuid, olderThanInHours: Int): Long

    fun countOfProductsAlreadyUpdated(eshopUuid: EshopUuid, olderThanInHours: Int): Long

    fun getProductWithUrl(productUrl: String, productIdToIgnore: Long?): Optional<Long>
}
