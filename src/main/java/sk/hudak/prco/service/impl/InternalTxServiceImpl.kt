package sk.hudak.prco.service.impl

import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sk.hudak.prco.api.ErrorType
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dto.*
import sk.hudak.prco.dto.product.*
import sk.hudak.prco.service.*
import java.math.BigDecimal
import java.util.*
import java.util.concurrent.Future

/**
 * Transaction wrapper for internal services.
 */
@Service
open class InternalTxServiceImpl(@param:Qualifier("newProductService") private val newProductService: NewProductService,
                                 @param:Qualifier("productCommonService") private val productCommonService: ProductCommonService,
                                 @param:Qualifier("productService") private val productService: ProductService,
                                 @param:Qualifier("notInterestedProductService") private val notInterestedProductService: NotInterestedProductService,
                                 @param:Qualifier("groupService") private val groupService: GroupService,
                                 @param:Qualifier("watchDogService") private val watchDogService: WatchDogService,
                                 @param:Qualifier("errorService") private val errorService: ErrorService,
                                 @param:Qualifier("groupProductKeywordsService") private val groupProductKeywordsService: GroupProductKeywordsService,
                                 @param:Qualifier("searchKeywordService") private val searchKeywordService: SearchKeywordService)
    : InternalTxService {

    @Transactional
    override fun createSearchKeyword(createDto: SearchKeywordCreateDto): Long =
            searchKeywordService.createSearchKeyword(createDto)

    @Transactional
    override fun updateSearchKeyword(updateDto: SearchKeywordUdateDto) {
        searchKeywordService.updateSearchKeyword(updateDto)
    }

    @Transactional(readOnly = true)
    override fun findAllSearchKeyword(): List<SearchKeywordListDto> =
        searchKeywordService.findAllSearchKeyword()

    @Transactional
    override fun updateProductUrl(productId: Long, newProductUrl: String) {
        productService.updateProductUrl(productId, newProductUrl)
    }

    @Transactional(readOnly = true)
    override fun getProductForUpdateByUrl(productUrl: String): ProductDetailInfo? =
            productService.getProductForUpdateByUrl(productUrl)


    @Transactional(readOnly = true)
    override fun findProductForUpdateInGroup(groupId: Long): Map<EshopUuid, List<Long>> =
            productService.findProductForUpdateInGroup(groupId)

    @Transactional(readOnly = true)
    override fun findProductsForUpdateWhichAreNotInAnyGroup(): Map<EshopUuid, List<Long>> =
            productService.findProductsForUpdateWhichAreNotInAnyGroup()

    @Transactional
    override fun removeWatchDog(eshopUuid: EshopUuid, maxCountToDelete: Long): Int =
            watchDogService.removeWatchDog(eshopUuid, maxCountToDelete)

    @Transactional
    override fun removeErrorsByCount(eshopUuid: EshopUuid, maxCountToDelete: Long): Int =
            errorService.removeErrorsByCount(eshopUuid, maxCountToDelete)


    @Transactional
    override fun removeProductsByCount(eshopUuid: EshopUuid, maxCountToDelete: Long): Int =
            productService.removeProductsByCount(eshopUuid, maxCountToDelete)


    @Transactional
    override fun removeNewProductsByCount(eshopUuid: EshopUuid, maxCountToDelete: Long): Int =
            newProductService.removeNewProductsByCount(eshopUuid, maxCountToDelete)


    @Transactional
    override fun removeNotInterestedProductsByCount(eshopUuid: EshopUuid, maxCountToDelete: Long): Int {
        return notInterestedProductService.removeNotInterestedProductsByCount(eshopUuid, maxCountToDelete)
    }

    override val countOfInvalidNewProduct: Long
        @Transactional(readOnly = true)
        get() = newProductService.countOfInvalidNewProduct

    override val countOfAllNewProducts: Long
        @Transactional(readOnly = true)
        get() = newProductService.countOfAllNewProducts

    override val statisticsOfProducts: ProductStatisticInfoDto
        @Transactional(readOnly = true)
        get() = productCommonService.statisticsOfProducts

    override val statisticForErrors: Map<ErrorType, Long>
        @Transactional(readOnly = true)
        get() = errorService.statisticForErrors

    @Transactional(readOnly = true)
    override fun existProductWithURL(productURL: String): Boolean {
        return productCommonService.existProductWithURL(productURL)
    }

    @Transactional
    override fun createNewProduct(newProductCreateDto: NewProductCreateDto): Long {
        return newProductService.createNewProduct(newProductCreateDto)
    }

    @Transactional(readOnly = true)
    override fun getNewProduct(newProductId: Long): NewProductFullDto {
        return newProductService.getNewProduct(newProductId)
    }

    @Transactional(readOnly = true)
    override fun findFirstInvalidNewProduct(): NewProductInfoDetail? {
        return newProductService.findFirstInvalidNewProduct()
    }

    @Transactional
    override fun repairInvalidUnitForNewProduct(newProductId: Long?,
                                                correctUnitData: UnitData) {
        newProductService.repairInvalidUnitForNewProduct(newProductId, correctUnitData)
    }

    @Transactional
    override fun reprocessProductData(newProductId: Long?) {
        newProductService.reprocessProductData(newProductId)
    }

    @Transactional
    override fun confirmUnitDataForNewProducts(newProductIds: LongArray) {
        newProductService.confirmUnitDataForNewProducts(newProductIds)
    }

    @Transactional
    override fun fixAutomaticallyProductUnitData(maxCountOfInvalid: Int): Long {
        return newProductService.fixAutomaticallyProductUnitData(maxCountOfInvalid)
    }

    @Transactional(readOnly = true)
    override fun findNewProducts(filter: NewProductFilterUIDto): List<NewProductFullDto> {
        return newProductService.findNewProducts(filter)
    }

    @Transactional
    override fun markNewProductAsInterested(vararg newProductIds: Long) {
        productCommonService.markNewProductAsInterested(*newProductIds)
    }

    @Transactional
    override fun markNewProductAsNotInterested(vararg newProductIds: Long) {
        productCommonService.markNewProductAsNotInterested(*newProductIds)
    }

    @Transactional(readOnly = true)
    override fun findProductForUpdate(eshopUuid: EshopUuid, olderThanInHours: Int): ProductDetailInfo? {
        return productService.findProductForUpdate(eshopUuid, olderThanInHours)
    }

    @Transactional
    override fun updateProduct(productUpdateDataDto: ProductUpdateDataDto) {
        productService.updateProduct(productUpdateDataDto)
    }

    @Transactional
    override fun updateProductUnitData(productUnitDataDto: ProductUnitDataDto) {
        newProductService.updateProductUnitData(productUnitDataDto)
    }

    @Transactional
    override fun deleteNewProducts(newProductIds: Array<Long>) {
        newProductService.deleteNewProducts(newProductIds)
    }

    @Transactional
    override fun deleteNotInterestedProducts(eshopUuid: EshopUuid) {
        notInterestedProductService.deleteNotInterestedProducts(eshopUuid)
    }

    @Transactional
    override fun markProductAsUnavailable(productId: Long) {
        productService.markProductAsUnavailable(productId)
    }

    @Transactional
    override fun resetUpdateDateProduct(productId: Long) {
        productService.resetUpdateDateProduct(productId)
    }

    @Transactional(readOnly = true)
    override fun findProductsBestPriceInGroupDto(eshopUuid: EshopUuid): List<ProductBestPriceInGroupDto> {
        return productService.findProductsBestPriceInGroupDto(eshopUuid)
    }

    @Transactional(readOnly = true)
    override fun getStatisticForUpdateForEshop(eshopUuid: EshopUuid, olderThanInHours: Int): StatisticForUpdateForEshopDto {
        return productService.getStatisticForUpdateForEshop(eshopUuid, olderThanInHours)
    }

    @Transactional
    override fun markProductAsNotInterested(productId: Long) {
        productService.markProductAsNotInterested(productId)
    }

    @Transactional(readOnly = true)
    override fun findNewProductsForExport(): List<NewProductFullDto> {
        return newProductService.findNewProductsForExport()
    }

    @Transactional(readOnly = true)
    override fun findProductsForExport(): List<ProductFullDto> {
        return productService.findProductsForExport()
    }

    @Transactional(readOnly = true)
    override fun findProducts(filter: ProductFilterUIDto): List<ProductFullDto> {
        return productService.findProducts(filter)
    }

    @Transactional(readOnly = true)
    override fun findProductsInAction(eshopUuid: EshopUuid): List<ProductInActionDto> {
        return productService.findProductsInAction(eshopUuid)
    }

    @Transactional
    override fun removeProduct(productId: Long) {
        productService.removeProduct(productId)
    }

    @Transactional
    override fun removeProductByUrl(productUrl: String) {
        productService.removeProductByUrl(productUrl)
    }

    @Transactional(readOnly = true)
    override fun findProductsInGroup(groupId: Long, withPriceOnly: Boolean, vararg eshopsToSkip: EshopUuid): List<ProductFullDto> {
        return productService.findProductsInGroup(groupId, withPriceOnly, *eshopsToSkip)
    }

    @Transactional(readOnly = true)
    override fun findProductsNotInAnyGroup(): List<ProductFullDto> {
        return productService.findProductsNotInAnyGroup()
    }

    @Transactional(readOnly = true)
    override fun getProduct(productId: Long?): ProductAddingToGroupDto {
        return productService.getProduct(productId)
    }

    @Transactional(readOnly = true)
    override fun existProductWithUrl(productURL: String): Boolean {
        return productService.existProductWithUrl(productURL)
    }

    @Transactional
    override fun resetUpdateDateForAllProductsInEshop(eshopUuid: EshopUuid) {
        productService.resetUpdateDateForAllProductsInEshop(eshopUuid)
    }

    @Transactional
    override fun updateProductCommonPrice(productId: Long, newCommonPrice: BigDecimal) {
        productService.updateProductCommonPrice(productId, newCommonPrice)
    }

    @Transactional(readOnly = true)
    override fun getEshopForProductId(productId: Long): EshopUuid {
        return productService.getEshopForProductId(productId)
    }

    @Transactional(readOnly = true)
    override fun findProductForUpdate(productId: Long): ProductDetailInfo {
        return productService.findProductForUpdate(productId)
    }

    @Transactional(readOnly = true)
    override fun findNotInterestedProducts(findDto: NotInterestedProductFindDto): List<NotInterestedProductFullDto> {
        return notInterestedProductService.findNotInterestedProducts(findDto)
    }

    @Transactional
    override fun importNewProducts(newProductList: List<NewProductFullDto>): Long {
        return productCommonService.importNewProducts(newProductList)
    }

    @Transactional
    override fun importProducts(productList: List<ProductFullDto>): Long {
        return productCommonService.importProducts(productList)
    }

    @Transactional
    override fun importNotInterestedProducts(notInterestedProductList: List<NotInterestedProductFullDto>): Long {
        return productCommonService.importNotInterestedProducts(notInterestedProductList)
    }

    // group

    @Transactional
    override fun createGroup(createDto: GroupCreateDto): Long {
        return groupService.createGroup(createDto)
    }

    @Transactional
    override fun updateGroup(updateDto: GroupUpdateDto) {
        groupService.updateGroup(updateDto)
    }

    @Transactional
    override fun addProductsToGroup(groupId: Long, vararg productId: Long) {
        groupService.addProductsToGroup(groupId, *productId)
    }

    @Transactional
    override fun removeProductsFromGroup(groupId: Long?, vararg productIds: Long) {
        groupService.removeProductsFromGroup(groupId, *productIds)
    }

    @Transactional(readOnly = true)
    override fun getGroupsWithoutProduct(productId: Long?): List<GroupListDto> {
        return groupService.getGroupsWithoutProduct(productId)
    }

    @Transactional(readOnly = true)
    override fun findGroups(groupFilterDto: GroupFilterDto): List<GroupListDto> {
        return groupService.findGroups(groupFilterDto)
    }

    @Transactional(readOnly = true)
    override fun findAllGroupExtended(): List<GroupListExtendedDto> {
        return groupService.findAllGroupExtended()
    }

    @Transactional(readOnly = true)
    override fun getGroupById(groupId: Long?): GroupIdNameDto {
        return groupService.getGroupById(groupId)
    }

    @Transactional
    override fun addNewProductToWatch(addDto: WatchDogAddCustomDto): Long? {
        return watchDogService.addNewProductToWatch(addDto)
    }

    @Transactional(readOnly = true)
    override fun findProductsForWatchDog(): Map<EshopUuid, List<WatchDogDto>> {
        return watchDogService.findProductsForWatchDog()
    }

    override fun notifyByEmail(toBeNotified: List<WatchDogNotifyUpdateDto>) {
        // nemusi bezat v tranzakcii
        watchDogService.notifyByEmail(toBeNotified)
    }


    @Transactional
    override fun createError(createDto: ErrorCreateDto): Long? {
        return errorService.createError(createDto)
    }

    @Transactional(readOnly = true)
    override fun findAll(): List<ErrorListDto> {
        return errorService.findAll()
    }

    @Transactional(readOnly = true)
    override fun findErrorByMaxCount(limit: Int, errorType: ErrorType): List<ErrorListDto> {
        return errorService.findErrorByMaxCount(limit, errorType)
    }

    @Transactional(readOnly = true)
    override fun findErrorsByFilter(findDto: ErrorFindFilterDto): List<ErrorListDto> {
        return errorService.findErrorsByFilter(findDto)
    }

    @Transactional(readOnly = true)
    override fun findErrorsByTypes(vararg errorTypes: ErrorType): List<ErrorListDto> {
        return errorService.findErrorsByTypes(*errorTypes)
    }

    @Transactional
    override fun startErrorCleanUp(): Future<Void>? {
        return errorService.startErrorCleanUp()
    }

    @Transactional
    override fun deleteNotInterestedProducts(vararg notInterestedProductIds: Long) {
        notInterestedProductService.deleteNotInterestedProducts(*notInterestedProductIds)
    }

    @Transactional(readOnly = true)
    override fun findDuplicityProductsByNameAndPriceInEshop(eshopUuid: EshopUuid): List<ProductFullDto> {
        return productService.findDuplicityProductsByNameAndPriceInEshop(eshopUuid)
    }

    @Transactional(readOnly = true)
    override fun getProductWithUrl(productUrl: String, productIdToIgnore: Long?): Optional<Long> {
        return productService.getProductWithUrl(productUrl, productIdToIgnore)
    }

    @Transactional
    override fun createGroupProductKeywords(groupProductKeywordsCreateDto: GroupProductKeywordsCreateDto): Long? {
        return groupProductKeywordsService.createGroupProductKeywords(groupProductKeywordsCreateDto)
    }

    @Transactional(readOnly = true)
    override fun getGroupProductKeywordsByGroupId(groupId: Long?): Optional<GroupProductKeywordsFullDto> {
        return groupProductKeywordsService.getGroupProductKeywordsByGroupId(groupId)
    }

    @Transactional
    override fun removeAllKeywordForGroupId(groupId: Long?) {
        groupProductKeywordsService.removeAllKeywordForGroupId(groupId)
    }

    // tests

    @Transactional
    override fun test() {
        throw UnsupportedOperationException()
    }
}
