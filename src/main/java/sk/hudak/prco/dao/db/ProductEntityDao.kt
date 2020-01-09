package sk.hudak.prco.dao.db

import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dao.BaseDao
import sk.hudak.prco.dto.product.ProductFilterUIDto
import sk.hudak.prco.model.ProductEntity

interface ProductEntityDao : BaseDao<ProductEntity> {

    fun existProductWithUrl(url: String): Boolean

    fun findProducts(): List<ProductEntity>

    fun findProductsByFilter(filter: ProductFilterUIDto): List<ProductEntity>

    fun findProductByUrl(productUrl: String): ProductEntity?

    fun findProductsInEshopForUpdate(eshopUuid: EshopUuid, olderThanInHours: Int): List<ProductEntity>

    /**
     * @param eshopUuid
     * @param olderThanInHours pocet v hodinach, kolko minimalne sa neupdatoval dany record
     * @return
     */
    fun findProductInEshopForUpdate(eshopUuid: EshopUuid, olderThanInHours: Int): ProductEntity?

    fun findProductsInEshop(eshopUuid: EshopUuid, maxCount: Long): List<ProductEntity>

    fun findProductIdWithUrl(productUrl: String, productIdToIgnore: Long?): Long?

    fun countOfProducts(): Long

    fun countOfProductsInEshop(eshopUuid: EshopUuid): Long

    fun countOfProductsInEshopUpdatedMax24Hours(eshopUuid: EshopUuid): Long

    fun countOfProductsWaitingToBeUpdated(eshopUuid: EshopUuid, olderThanInHours: Int): Long

    fun countOfProductsAlreadyUpdated(eshopUuid: EshopUuid, olderThanInHours: Int): Long

    fun countOfProductsMarkedAsUnavailable(eshopUuid: EshopUuid): Long
}
