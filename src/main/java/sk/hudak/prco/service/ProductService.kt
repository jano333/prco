package sk.hudak.prco.service

import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dto.ProductUpdateDataDto
import sk.hudak.prco.dto.StatisticForUpdateForEshopDto
import sk.hudak.prco.dto.product.*
import java.math.BigDecimal

interface ProductService {

    /**
     * @param productURL URL of product
     * @return true, if product exist (product table only)
     */
    fun existProductWithUrl(productURL: String): Boolean

    fun getProductById(productId: Long): ProductFullDto

    /**
     * Product data for product with given id.
     *
     * @param productId product id
     * @return product data
     */
    fun getProductForAddingToGroup(productId: Long): ProductAddingToGroupDto

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
    //TODO overit ci sa pouziva
    fun getStatisticForUpdateForEshop(eshopUuid: EshopUuid, olderThanInHours: Int): StatisticForUpdateForEshopDto

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

    // ----------- FIND -----------

    /**
     * @param productId product id
     * @return
     */
    fun findProductById(productId: Long): ProductDetailInfo


    /**
     * @param eshopUuid        eshop unique identifier
     * @param olderThanInHours
     * @return
     */
    fun findProductInEshopForUpdate(eshopUuid: EshopUuid, olderThanInHours: Int): ProductDetailInfo?


    /**
     * vyhlada product na zaklade URL, pozor nemusi existovat...
     */
    fun findProductForUpdateByUrl(productUrl: String): ProductDetailInfo?

    /**
     * @param filter
     * @return
     */
    fun findProductsByFilter(filter: ProductFilterUIDto): List<ProductFullDto>

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
    //TODO rename na nieco zmysluplne...
    fun findProductsBestPriceInGroupDto(eshopUuid: EshopUuid): List<ProductBestPriceInGroupDto>

    /**
     * @param eshopUuid eshop unique identifier
     * @return
     */
    fun findProductsInEshopWithDuplicityByNameAndPrice(eshopUuid: EshopUuid): List<ProductFullDto>

    /**
     * @param productUrl        URL of product
     * @param productIdToIgnore product id which will be ignored during finding
     * @return product id if found, empty if not
     */
    fun findProductIdWithUrl(productUrl: String, productIdToIgnore: Long?): Long?

    fun findProductForUpdateInGroup(groupId: Long): Map<EshopUuid, List<Long>>

    fun findProductsForUpdateWhichAreNotInAnyGroup(): Map<EshopUuid, List<Long>>

    fun findProductsForUpdate(eshopUuid: EshopUuid, olderThanInHours: Int): List<ProductDetailInfo>

    fun findProductsForExport(): List<ProductFullDto>

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
    fun removeProductsInEshopByCount(eshopUuid: EshopUuid, maxCountToBeDelete: Long): Int


}
