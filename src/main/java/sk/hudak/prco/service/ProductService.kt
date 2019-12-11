package sk.hudak.prco.service

import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dto.ProductUpdateDataDto
import sk.hudak.prco.dto.StatisticForUpdateForEshopDto
import sk.hudak.prco.dto.product.*
import java.math.BigDecimal

interface ProductService {

    //TODO niekde je delete a niekde remove na product ujednotit to ...

    /**
     * @param productURL URL of product
     * @return true, if product exist (product table only)
     */
    fun existProductWithUrl(productURL: String): Boolean

    // ----------- GET -----------

    fun getProductById(productId: Long): ProductFullDto

    /**
     * Product data for product with given id.
     *
     * @param productId product id
     * @return product data
     */
    //TODO zrusit ? v  Long?
    //TODO rename methody
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
    fun getStatisticForUpdateForEshop(eshopUuid: EshopUuid, olderThanInHours: Int): StatisticForUpdateForEshopDto

    // ----------- FIND -----------

    /**
     * @param productId product id
     * @return
     */
    fun findProductForUpdate(productId: Long): ProductDetailInfo


    /**
     * @param eshopUuid        eshop unique identifier
     * @param olderThanInHours
     * @return
     */
    fun findProductForUpdate(eshopUuid: EshopUuid, olderThanInHours: Int): ProductDetailInfo?


    // ----------- UPDATE -----------

    /**
     * @param productUpdateDataDto
     */
    fun updateProduct(productUpdateDataDto: ProductUpdateDataDto)

    fun updateProductUrl(productId: Long, newProductUrl: String)

    fun updateProductUnitPackageCount(productId: Long, unitPackageCount: Int)

    /**
     * Update of 'common' price of product.
     *
     * @param productId      product id
     * @param newCommonPrice common(bezna cena) productu
     */
    fun updateProductCommonPrice(productId: Long, newCommonPrice: BigDecimal)


    // -------- OTHERS --------

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
    fun resetUpdateDateProduct(productId: Long)

    /**
     * Reset of all prices to null, same for action and lastTimeDataUpdated is set tu current date
     *
     * @param productId product id which will be mark as unavailable
     */
    fun markProductAsUnavailable(productId: Long)

    /**
     * Move product to NotInteredted product. Before that it removes it from all group and after moving it delete it.
     *
     * @param productId product id
     */
    fun markProductAsNotInterested(productId: Long)


    /**
     * Remove product from all groups where it is and finally remove product itself.
     *
     * @param productId product id
     */
    fun removeProduct(productId: Long)

    //TODO dat navratovu hodnotu boolen ci sa taky vobec nasiel
    fun removeProductByUrl(productUrl: String)

    /**
     * Remove/delete randomly count of product. Return count of really deleted products.
     */
    fun removeProductsByCount(eshopUuid: EshopUuid, maxCountToDelete: Long): Int


    /**
     * vyhlada product na zaklade URL, pozor nemusi existovat...
     */
    //TODO rename to findProductForUpdateByUrl
    fun getProductForUpdateByUrl(productUrl: String): ProductDetailInfo?



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
    fun findProductIdWithUrl(productUrl: String, productIdToIgnore: Long?): Long?

    fun findProductForUpdateInGroup(groupId: Long): Map<EshopUuid, List<Long>>
    fun findProductsForUpdateWhichAreNotInAnyGroup(): Map<EshopUuid, List<Long>>


}
