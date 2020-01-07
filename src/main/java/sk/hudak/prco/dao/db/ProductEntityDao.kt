package sk.hudak.prco.dao.db

import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dao.BaseDao
import sk.hudak.prco.dto.product.ProductFilterUIDto
import sk.hudak.prco.model.ProductEntity

interface ProductEntityDao : BaseDao<ProductEntity> {

    fun existWithUrl(url: String): Boolean

    fun findAll(): List<ProductEntity>

    /**
     * @param eshopUuid
     * @param olderThanInHours pocet v hodinach, kolko minimalne sa neupdatoval dany record
     * @return
     */
    fun findProductForUpdate(eshopUuid: EshopUuid, olderThanInHours: Int): ProductEntity?

    fun findProductsForUpdate(eshopUuid: EshopUuid, olderThanInHours: Int): List<ProductEntity>

    fun findByFilter(filter: ProductFilterUIDto): List<ProductEntity>

    fun findByUrl(productUrl: String): ProductEntity?

    fun findProductIdWithUrl(productUrl: String, productIdToIgnore: Long?): Long?

    fun findByCount(eshopUuid: EshopUuid, maxCountToDelete: Long): List<ProductEntity>

    fun countOfAll(): Long

    fun countOfAllProductInEshop(eshopUuid: EshopUuid): Long

    fun countOfAllProductInEshopUpdatedMax24Hours(eshopUuid: EshopUuid): Long

    fun countOfProductsWaitingToBeUpdated(eshopUuid: EshopUuid, olderThanInHours: Int): Long

    fun countOfProductsAlreadyUpdated(eshopUuid: EshopUuid, olderThanInHours: Int): Long
    fun countOfProductMarkedAsUnavailable(eshopUuid: EshopUuid): Long
}
