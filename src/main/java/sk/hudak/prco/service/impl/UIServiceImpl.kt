package sk.hudak.prco.service.impl

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dto.*
import sk.hudak.prco.dto.product.*
import sk.hudak.prco.service.InternalTxService
import sk.hudak.prco.service.UIService
import java.math.BigDecimal

/**
 * Delegator pattern.
 */
@Component
class UIServiceImpl(
        @Autowired private val internalTxService: InternalTxService
) : UIService {

    override val statisticsOfProducts: ProductStatisticInfoDto
        get() = internalTxService.statisticsOfProducts

    override val countOfAllNewProducts: Long
        get() = internalTxService.countOfAllNewProducts

    override fun findNewProducts(filter: NewProductFilterUIDto): List<NewProductFullDto> {
        return internalTxService.findNewProducts(filter)
    }

    override fun getNewProduct(newProductId: Long?): NewProductFullDto {
        return internalTxService.getNewProduct(newProductId!!)
    }

    override fun confirmUnitDataForNewProduct(newProductId: Long?) {
        val newProductIds = longArrayOf(newProductId!!)

        internalTxService.confirmUnitDataForNewProducts(newProductIds)
    }

    override fun markNewProductAsInterested(newProductId: Long?) {
        internalTxService.markNewProductAsInterested(newProductId!!)
    }

    override fun markNewProductAsNotInterested(newProductId: Long?) {
        internalTxService.markNewProductAsNotInterested(newProductId!!)
    }

    override fun getProduct(productId: Long?): ProductAddingToGroupDto {
        return internalTxService.getProduct(productId)
    }

    override fun updateProductUnitData(productUnitDataDto: ProductUnitDataDto) {
        internalTxService.updateProductUnitData(productUnitDataDto)
    }

    override fun updateCommonPrice(productId: Long?, newCommonPrice: BigDecimal) {
        internalTxService.updateProductCommonPrice(productId!!, newCommonPrice)
    }

    override fun resetUpdateDateForAllProductsInEshop(eshopUuid: EshopUuid) {
        internalTxService.resetUpdateDateForAllProductsInEshop(eshopUuid)
    }

    override fun findProducts(filter: ProductFilterUIDto): List<ProductFullDto> {
        return internalTxService.findProducts(filter)
    }

    override fun removeProduct(productId: Long?) {
        internalTxService.removeProduct(productId!!)
    }

    override fun findProductsInGroup(groupId: Long, withPriceOnly: Boolean, vararg eshopsToSkip: EshopUuid): List<ProductFullDto> {
        return internalTxService.findProductsInGroup(groupId, withPriceOnly, *eshopsToSkip)
    }

    override fun findProductsWitchAreNotInAnyGroup(): List<ProductFullDto> {
        return internalTxService.findProductsNotInAnyGroup()
    }

    override fun tryToRepairInvalidUnitForNewProductByReprocessing(newProductId: Long?) {
        internalTxService.reprocessProductData(newProductId)
    }

    override fun createGroup(groupCreateDto: GroupCreateDto): Long? {
        return internalTxService.createGroup(groupCreateDto)
    }

    override fun updateGroup(groupUpdateDto: GroupUpdateDto) {
        internalTxService.updateGroup(groupUpdateDto)
    }

    override fun getGroupById(groupId: Long?): GroupIdNameDto {
        return internalTxService.getGroupById(groupId)
    }

    override fun findGroups(groupFilterDto: GroupFilterDto): List<GroupListDto> {
        return internalTxService.findGroups(groupFilterDto)
    }

    override fun addProductsToGroup(groupId: Long?, vararg productIds: Long) {
        internalTxService.addProductsToGroup(groupId!!, *productIds)
    }

    override fun removeProductsFromGroup(groupId: Long?, vararg productIds: Long) {
        internalTxService.removeProductsFromGroup(groupId, *productIds)
    }

    override fun getGroupsWithoutProduct(productId: Long?): List<GroupListDto> {
        return internalTxService.getGroupsWithoutProduct(productId)
    }

    override fun findAllGroupExtended(): List<GroupListExtendedDto> {
        return internalTxService.findAllGroupExtended()
    }

    override fun existProductWithUrl(productURL: String): Boolean {
        return internalTxService.existProductWithUrl(productURL)
    }

    override fun deleteProducts(vararg productIds: Long) {
        if (productIds == null) {
            return
        }
        for (productId in productIds) {
            internalTxService.removeProduct(productId)
        }
    }

    override fun findProductsInAction(eshopUuid: EshopUuid): List<ProductInActionDto> {
        return internalTxService.findProductsInAction(eshopUuid)
    }

    override fun findProductsBestPriceInGroupDto(eshopUuid: EshopUuid): List<ProductBestPriceInGroupDto> {
        return internalTxService.findProductsBestPriceInGroupDto(eshopUuid)
    }

    override fun deleteNewProducts(vararg newProductIds: Long) {
        //TODO
        //        internalTxService.deleteNewProducts(newProductIds);
    }

    override fun markProductAsNotInterested(productId: Long?) {
        internalTxService.markProductAsNotInterested(productId!!)
    }
}
