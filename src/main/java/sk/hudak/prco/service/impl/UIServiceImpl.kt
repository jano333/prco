package sk.hudak.prco.service.impl

import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dto.*
import sk.hudak.prco.dto.product.*
import sk.hudak.prco.events.executors.EshopExecutors
import sk.hudak.prco.events.executors.EshopScheduledExecutor
import sk.hudak.prco.manager.update.UpdateProductManager
import sk.hudak.prco.service.InternalTxService
import sk.hudak.prco.service.UIService
import java.math.BigDecimal
import java.util.concurrent.ScheduledExecutorService

/**
 * Delegator pattern.
 */
@Component
class UIServiceImpl(private val internalTxService: InternalTxService,
                    private val eshopDocumentExecutor: EshopExecutors,
                    private val updateProductManager: UpdateProductManager)
    : UIService {

    override fun getEshopsAdminData(): List<EshopAdminDto> {
        val info = ArrayList<EshopAdminDto>()
        EshopUuid.values().forEach {
            info.add(EshopAdminDto(it))
        }
        return info
    }

    override fun getExecutorStatistic(): List<EshopsExecutorInfoDto> {
        val info = ArrayList<EshopsExecutorInfoDto>()

        EshopUuid.values().forEach { eshopUuid ->

            val eshopExecutor: ScheduledExecutorService = eshopDocumentExecutor.getEshopExecutor(eshopUuid)
            eshopExecutor as EshopScheduledExecutor

            info.add(EshopsExecutorInfoDto(
                    eshopUuid = eshopUuid,
                    activeCount = eshopExecutor.activeCount,
                    completedTaskCount = eshopExecutor.completedTaskCount,
                    taskCount = eshopExecutor.taskCount
            ))
        }
        //sort by state Running -> Waiting -> Completed
        info.sortWith(Comparator { e1, e2 ->
            e1.getState().id.compareTo(e2.getState().id)
        })

        return info
    }

    override fun findErrorsByFilter(findDto: ErrorFindFilterDto): List<ErrorListDto> =
            internalTxService.findErrorsByFilter(findDto)

    override fun statisticsOfProducts(): ProductStatisticInfoDto =
            internalTxService.statisticsOfProducts()

    override fun countOfAllNewProducts(): Long =
            internalTxService.countOfAllNewProducts()

    override fun findNewProducts(filter: NewProductFilterUIDto): List<NewProductFullDto> =
            internalTxService.findNewProducts(filter)


    override fun getNewProduct(newProductId: Long?): NewProductFullDto {
        return internalTxService.getNewProduct(newProductId!!)
    }

    override fun confirmUnitDataForNewProduct(newProductId: Long?) {
        val newProductIds = longArrayOf(newProductId!!)

        internalTxService.confirmUnitDataForNewProducts(newProductIds)
    }

    override fun markNewProductAsInterested(newProductId: Long) {
        val productId = internalTxService.markNewProductAsInterested(newProductId)
        if (productId != null) {
            // spustim update process pre dany product
            updateProductManager.updateProductDataForProductWithId(productId)
        } else {
//            product z danou URL uz existuje ...
            //TODO asi urobit log s errorom...
        }

    }

    override fun markNewProductAsNotInterested(newProductId: Long?) {
        internalTxService.markNewProductAsNotInterested(newProductId!!)
    }

    override fun getProduct(productId: Long): ProductAddingToGroupDto {
        return internalTxService.getProductForAddingToGroup(productId)
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
        return internalTxService.findProductsByFilter(filter)
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
