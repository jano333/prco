package sk.hudak.prco.service

import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dto.ProductUpdateDataDto
import sk.hudak.prco.dto.StatisticForUpdateForEshopDto
import sk.hudak.prco.dto.product.*
import java.math.BigDecimal
import java.util.*

/**
 *
 */
interface ProductService {

    //TODO niekde je delete a niekde remove na product ujednotit to ...

    /**
     * @param productURL URL of product
     * @return true, if product exist (product table only)
     */
    fun existProductWithUrl(productURL: String): Boolean

    /**
     * @param productUpdateDataDto
     */
    fun updateProduct(productUpdateDataDto: ProductUpdateDataDto)

    /**
     * Update of 'common' price of product.
     *
     * @param productId      product id
     * @param newCommonPrice common(bezna cena) productu
     */
    //TODO zrusit ? v  Long?
    fun updateProductCommonPrice(productId: Long?, newCommonPrice: BigDecimal)


    fun updateProductUrl(productId: Long, newProductUrl: String)

    /**
     * set field 'LastTimeDataUpdated' to null for product in given eshop
     *
     * @param eshopUuid eshop unique identification
     */
    fun resetUpdateDateForAllProductsInEshop(eshopUuid: EshopUuid)

    /**
     * set field 'lastTimeDataUpdated' to null for product with given id
     *
     * @param productId product id
     */
    //TODO zrusit ? v  Long?
    fun resetUpdateDateProduct(productId: Long?)

    /**
     * Reset of all prices to null, same for action and 'lastTimeDataUpdated'.
     *
     * @param productId product id
     */
    //TODO zrusit ? v  Long?
    fun markProductAsUnavailable(productId: Long?)

    /**
     * Move product to NotInteredted product. Before that it removes it from all group and after moving it delete it.
     *
     * @param productId product id
     */
    fun markProductAsNotInterested(productId: Long?)


    /**
     * Remove product from all groups where it is and finally remove product itself.
     *
     * @param productId product id
     */
    //TODO zrusit ? v  Long?
    fun removeProduct(productId: Long?)


    fun removeProductByUrl(productUrl: String)

    /**
     * Remove/delete randomly count of product. Return count of really deleted products.
     */
    fun removeProductsByCount(eshopUuid: EshopUuid, maxCountToDelete: Long): Int

    // ----------- GET -----------

    /**
     * Product data for product with given id.
     *
     * @param productId product id
     * @return product data
     */
    //TODO zrusit ? v  Long?
    fun getProduct(productId: Long?): ProductAddingToGroupDto

    /**
     * Eshop to which the product with given id belongs to.
     *
     * @param productId product id
     * @return eshop unique identifier
     */
    fun getEshopForProductId(productId: Long): EshopUuid

    /**
     * @param eshopUuid        eshop unique identifier
     * @param olderThanInHours
     * @return
     */
    fun findProductForUpdate(eshopUuid: EshopUuid, olderThanInHours: Int): ProductDetailInfo?

    /**
     * @param productId product id
     * @return
     */
    fun findProductForUpdate(productId: Long): ProductDetailInfo


    /**
     * vyhlada product na zaklade URL, pozor nemusi existovat...
     */
    fun getProductForUpdateByUrl(productUrl: String): ProductDetailInfo?


    /**
     * @param eshopUuid        eshop unique identifier
     * @param olderThanInHours
     * @return
     */
    fun getStatisticForUpdateForEshop(eshopUuid: EshopUuid, olderThanInHours: Int): StatisticForUpdateForEshopDto

    // ----------- FIND -----------

    /**
     * @param filter
     * @return
     */
    fun findProducts(filter: ProductFilterUIDto): List<ProductFullDto>

    /**
     * @param groupId       id of group
     * @param withPriceOnly
     * @param eshopsToSkip
     * @return
     */
    fun findProductsInGroup(groupId: Long, withPriceOnly: Boolean, vararg eshopsToSkip: EshopUuid): List<ProductFullDto>

    /**
     * @return list of products which are not in any group
     */
    fun findProductsNotInAnyGroup(): List<ProductFullDto>

    /**
     * @param eshopUuid eshop unique identifier
     * @return list of product in action for given eshop
     */
    fun findProductsInAction(eshopUuid: EshopUuid): List<ProductInActionDto>

    /**
     * @param eshopUuid eshop unique identifier
     * @return
     */
    fun findProductsBestPriceInGroupDto(eshopUuid: EshopUuid): List<ProductBestPriceInGroupDto>

    /**
     * @return
     */
    fun findProductsForExport(): List<ProductFullDto>

    /**
     * @param eshopUuid eshop unique identifier
     * @return
     */
    fun findDuplicityProductsByNameAndPriceInEshop(eshopUuid: EshopUuid): List<ProductFullDto>

    /**
     * @param productUrl        URL of product
     * @param productIdToIgnore product id which will be ignored during finding
     * @return product id if found, empty if not
     */
    fun getProductWithUrl(productUrl: String, productIdToIgnore: Long?): Optional<Long>

    fun findProductForUpdateInGroup(groupId: Long): Map<EshopUuid, List<Long>>

    fun findProductsForUpdateWhichAreNotInAnyGroup(): Map<EshopUuid, List<Long>>
}
