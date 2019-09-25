package sk.hudak.prco.service.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import sk.hudak.prco.api.BestPriceInGroup
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dao.db.*
import sk.hudak.prco.dto.GroupIdNameDto
import sk.hudak.prco.dto.ProductUpdateDataDto
import sk.hudak.prco.dto.StatisticForUpdateForEshopDto
import sk.hudak.prco.dto.product.*
import sk.hudak.prco.mapper.PrcoOrikaMapper
import sk.hudak.prco.model.NotInterestedProductEntity
import sk.hudak.prco.model.ProductEntity
import sk.hudak.prco.service.ProductService
import sk.hudak.prco.utils.CalculationUtils
import sk.hudak.prco.utils.PriceCalculator
import sk.hudak.prco.utils.Validate.notEmpty
import sk.hudak.prco.utils.Validate.notNegativeAndNotZeroValue
import sk.hudak.prco.utils.Validate.notNull
import sk.hudak.prco.utils.Validate.notNullNotEmpty
import java.math.BigDecimal
import java.util.*
import java.util.function.Predicate
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.streams.toList

@Service("productService")
class ProductServiceImpl(private val productEntityDao: ProductEntityDao,
                         private val groupEntityDao: GroupEntityDao,
                         private val groupOfProductFindEntityDao: GroupOfProductFindEntityDao,
                         private val productDataUpdateEntityDao: ProductDataUpdateEntityDao,
                         private val notInterestedProductDbDao: NotInterestedProductDbDao,
                         private val mapper: PrcoOrikaMapper,
                         private val priceCalculator: PriceCalculator)
    : ProductService {

    companion object {
        val log = LoggerFactory.getLogger(ProductServiceImpl::class.java)!!

        private const val PRODUCT_URL = "productURL"
    }


    override fun findProductForUpdateInGroup(groupId: Long): Map<EshopUuid, List<Long>> {
        val productEntityList = groupEntityDao.findProductForUpdateInGroup(groupId)

        val result = HashMap<EshopUuid, MutableList<Long>>()
        EshopUuid.values().forEach {
            result[it] = ArrayList()
        }
        productEntityList.forEach {
            result[it.eshopUuid]?.add(it.id)
        }
        return result
    }

    override fun findProductsForUpdateWhichAreNotInAnyGroup(): Map<EshopUuid, List<Long>> {
        val productEntityList = groupOfProductFindEntityDao.findProductsWitchAreNotInAnyGroup(true)
        //FIXME tato preklapacka je aj hore
        val result = HashMap<EshopUuid, MutableList<Long>>()
        EshopUuid.values().forEach {
            result[it] = ArrayList()
        }
        productEntityList.forEach {
            result[it.eshopUuid]?.add(it.id)
        }
        return result

    }


    override fun findProductsForExport(): List<ProductFullDto> {
        return mapper.mapAsList<Any, ProductFullDto>(productEntityDao.findAll().toTypedArray(),
                ProductFullDto::class.java)
    }

    override fun findProductForUpdate(eshopUuid: EshopUuid, olderThanInHours: Int): ProductDetailInfo? {
        notNegativeAndNotZeroValue(olderThanInHours, "olderThanInHours")

        val productEntity = productEntityDao.findProductForUpdate(eshopUuid, olderThanInHours)

        if (productEntity != null) {
            return mapper.map(productEntity, ProductDetailInfo::class.java)
        }
        return null
    }

    override fun findProducts(filter: ProductFilterUIDto): List<ProductFullDto> {
        //FIXME skusit optimalizovat databazovo

        val productFullDtos = mapper.mapAsList<Any, ProductFullDto>(
                productEntityDao.findByFilter(filter).toTypedArray(),
                ProductFullDto::class.java)

        productFullDtos.forEach {
            it.groupList = mapper.mapAsList(groupEntityDao.findGroupsForProduct(it.id!!), GroupIdNameDto::class.java)
        }
        return productFullDtos
    }

    override fun findProductsInAction(eshopUuid: EshopUuid): List<ProductInActionDto> {
        val productsInAction = productEntityDao.findByFilter(ProductFilterUIDto(eshopUuid, java.lang.Boolean.TRUE))

        val result = ArrayList<ProductInActionDto>(productsInAction.size)
        for (entity in productsInAction) {
            // TODO urobit cast cez orika mapping a tu len doplnenie dopocitavaneho atributu
            val dto = ProductInActionDto()
            dto.id = entity.id
            dto.url = entity.url
            dto.name = entity.name
            dto.eshopUuid = entity.eshopUuid
            dto.priceForPackage = entity.priceForPackage
            dto.priceForOneItemInPackage = entity.priceForOneItemInPackage
            dto.commonPrice = entity.commonPrice
            dto.productAction = entity.productAction
            dto.actionValidTo = entity.actionValidTo
            // vypocitam percenta
            // FIXME presunut nech sa to perzistuje aby som vedel vyhladavat podla najvecsej zlavy...
            if (entity.priceForPackage != null && entity.commonPrice != null) {
                val actionInPercentage = CalculationUtils.calculatePercetage(entity.priceForPackage, entity.commonPrice)
                dto.actionInPercentage = actionInPercentage
            } else {
                dto.actionInPercentage = -1
            }

            calculateBestPriceInGroup(dto)

            result.add(dto)
        }
        return result
    }

    private fun calculateBestPriceInGroup(dto: ProductInActionDto) {
        val groupIdOptional = groupOfProductFindEntityDao.findFirstProductGroupId(dto.id)
        if (!groupIdOptional.isPresent) {
            dto.bestPriceInGroup = BestPriceInGroup.NO_GROUP
            return
        }

        //TODO ak je vo viacerych grupach
        val products = groupEntityDao.findById(groupIdOptional.get()).products

        val withValidPriceForPackage = products.filter {
            it.priceForUnit != null
        }.toList()

        Collections.sort(withValidPriceForPackage, Comparator.comparing { productEntity: ProductEntity ->
            productEntity.priceForUnit
        })

        if (withValidPriceForPackage[0].id == dto.id) {
            dto.bestPriceInGroup = BestPriceInGroup.YES
        } else {
            dto.bestPriceInGroup = BestPriceInGroup.NO
        }
    }

    override fun findProductsBestPriceInGroupDto(eshopUuid: EshopUuid): List<ProductBestPriceInGroupDto> {
        val productsInAction = productEntityDao.findByFilter(ProductFilterUIDto(eshopUuid, java.lang.Boolean.TRUE))

        return productsInAction.stream()
                .filter(bestPricePredicate())
                .map { entity ->
                    val dto = ProductBestPriceInGroupDto()
                    dto.id = entity.id
                    dto.url = entity.url
                    dto.name = entity.name
                    dto.eshopUuid = entity.eshopUuid
                    dto.priceForPackage = entity.priceForPackage
                    dto.priceForOneItemInPackage = entity.priceForOneItemInPackage
                    dto.commonPrice = entity.commonPrice
                    dto.priceForUnit = entity.priceForUnit
                    dto.productAction = entity.productAction
                    dto.actionValidTo = entity.actionValidTo
                    // vypocitam percenta
                    // FIXME presunut nech sa to perzistuje aby som vedel vyhladavat podla najvecsej zlavy...
                    if (entity.priceForPackage != null && entity.commonPrice != null) {
                        val actionInPercentage = CalculationUtils.calculatePercetage(entity.priceForPackage,
                                entity.commonPrice)
                        dto.actionInPercentage = actionInPercentage
                    } else {
                        dto.actionInPercentage = -1
                    }
                    dto
                }
                .toList()
    }

    private fun bestPricePredicate(): Predicate<ProductEntity> {
        return Predicate {

            val groupIdOptional = groupOfProductFindEntityDao.findFirstProductGroupId(it.id)
            if (!groupIdOptional.isPresent) {
                return@Predicate false
            }

            //TODO ak je vo viacerych grupach
            val products = groupEntityDao.findById(groupIdOptional.get()).products
            val withValidPriceForPackage = products.filter { it.priceForUnit != null }.toList()

            // FIXME cez db query
            Collections.sort(withValidPriceForPackage, Comparator.comparing { productEntity: ProductEntity ->
                productEntity.priceForUnit
            })

            withValidPriceForPackage[0].id == it.id
        }
    }

    override fun getStatisticForUpdateForEshop(eshopUuid: EshopUuid, olderThanInHours: Int): StatisticForUpdateForEshopDto {
        notNegativeAndNotZeroValue(olderThanInHours, "olderThanInHours")

        return StatisticForUpdateForEshopDto(
                eshopUuid,
                productEntityDao.countOfProductsWaitingToBeUpdated(eshopUuid, olderThanInHours),
                productEntityDao.countOfProductsAlreadyUpdated(eshopUuid, olderThanInHours))
    }

    override fun removeProduct(productId: Long) {
        removeProduct(productEntityDao.findById(productId))
    }

    override fun removeProductByUrl(productUrl: String) {
        notEmpty(productUrl, "productUrl")

        productEntityDao.findByUrl(productUrl)?.let {
            removeProduct(it)
        }
    }

    override fun removeProductsByCount(eshopUuid: EshopUuid, maxCountToDelete: Long): Int {
        val findByCount = productEntityDao.findByCount(eshopUuid, maxCountToDelete)
        findByCount.forEach {
            removeProduct(it)
        }
        return findByCount.size
    }

    private fun removeProduct(productEntity: ProductEntity) {
        removeProductFromGroup(productEntity)

        productEntityDao.delete(productEntity)
        log.debug("product was removed, id ${productEntity.id} url ${productEntity.url}")
    }

    private fun removeProductFromGroup(productEntity: ProductEntity) {
        for (groupEntity in groupEntityDao.findGroupsForProduct(productEntity.id)) {
            groupEntity.products.remove(productEntity)
            groupEntityDao.update(groupEntity)
            log.debug("product '${productEntity.name}' was removed from group '${groupEntity.name}'")
        }
    }

    override fun findProductsInGroup(groupId: Long, withPriceOnly: Boolean, vararg eshopsToSkip: EshopUuid): List<ProductFullDto> {
        notNull(groupId, "groupId")

        return mapper.mapAsList<Any, ProductFullDto>(
                groupEntityDao.findProductsInGroup(groupId, withPriceOnly, *eshopsToSkip).toTypedArray(),
                ProductFullDto::class.java)
    }

    override fun findProductsNotInAnyGroup(): List<ProductFullDto> {
        return mapper.mapAsList<Any, ProductFullDto>(
                groupOfProductFindEntityDao.findProductsWitchAreNotInAnyGroup().toTypedArray(),
                ProductFullDto::class.java)
    }

    override fun getProduct(productId: Long?): ProductAddingToGroupDto {
        return mapper.map(productEntityDao.findById(productId!!), ProductAddingToGroupDto::class.java)
    }

    override fun existProductWithUrl(productURL: String): Boolean {
        notEmpty(productURL, PRODUCT_URL)

        return productEntityDao.existWithUrl(productURL)
    }

    override fun resetUpdateDateForAllProductsInEshop(eshopUuid: EshopUuid) {

        //FIXME robit bulkovo po 25 ks, nie vsetky natiahnut naraz
        val findByFilter = productEntityDao.findByFilter(ProductFilterUIDto(eshopUuid))
        findByFilter.forEach { productEntity ->
            productEntity.lastTimeDataUpdated = null
            productEntityDao.update(productEntity)
        }
        log.debug("all products ${findByFilter.size} for eshop $eshopUuid marked as not updated yet")
    }

    override fun updateProductCommonPrice(productId: Long, newCommonPrice: BigDecimal) {
        // TODO validacia na vecsie ako nula...

        val product = productEntityDao.findById(productId)
        product.commonPrice = newCommonPrice
        productEntityDao.update(product)

        log.debug("product with id {} was updated with common price {}", productId, newCommonPrice)
    }

    override fun updateProductUrl(productId: Long, newProductUrl: String) {
        notEmpty(newProductUrl, "newProductUrl")

        val productEntity = productEntityDao.findById(productId)
        productEntity.url = newProductUrl
        productEntityDao.update(productEntity)
        log.debug("product with id $productId was updated with url $newProductUrl")
    }

    override fun getEshopForProductId(productId: Long): EshopUuid {
        return productEntityDao.findById(productId).eshopUuid
    }

    override fun findProductForUpdate(productId: Long): ProductDetailInfo {
        return mapper.map(productEntityDao.findById(productId), ProductDetailInfo::class.java)
    }

    override fun getProductForUpdateByUrl(productUrl: String): ProductDetailInfo? {
        val findByUrl = productEntityDao.findByUrl(productUrl)
        return if (findByUrl != null) {
            //TODO zrusit mapper mame kotlin extension function!!!
            mapper.map(findByUrl, ProductDetailInfo::class.java)
        } else {
            null
        }
    }

    override fun updateProduct(updateData: ProductUpdateDataDto) {
        notNull(updateData.id, "id")
        notNullNotEmpty(updateData.name, "name")
        notNullNotEmpty(updateData.url, "url")
        notNull(updateData.priceForPackage, "priceForPackage")

        val productEntity = productDataUpdateEntityDao.findById(updateData.id!!)
        productEntity.name = updateData.name
        // can change because of redirect URL, that why update of url
        productEntity.url = updateData.url

        // prices
        productEntity.priceForPackage = updateData.priceForPackage
        val priceForOneItemInPackage = priceCalculator.calculatePriceForOneItemInPackage(
                updateData.priceForPackage!!,
                productEntity.unitPackageCount!!
        )
        val priceForUnit = priceCalculator.calculatePriceForUnit(
                productEntity.unit,
                productEntity.unitValue,
                priceForOneItemInPackage
        )
        productEntity.priceForOneItemInPackage = priceForOneItemInPackage
        productEntity.priceForUnit = priceForUnit
        // action info
        productEntity.productAction = updateData.productAction
        productEntity.actionValidTo = updateData.actionValidity

        productEntity.productPictureUrl = updateData.pictureUrl

        productEntity.lastTimeDataUpdated = Date()
        productDataUpdateEntityDao.update(productEntity)

        log.info("product with id {} has been updated with price for package {}",
                productEntity.id, updateData.priceForPackage)
    }


    override fun markProductAsUnavailable(productId: Long) {
        val updateEntity = productDataUpdateEntityDao.findById(productId)
        updateEntity.lastTimeDataUpdated = Date()
        // prices
        updateEntity.priceForOneItemInPackage = null
        updateEntity.priceForPackage = null
        updateEntity.priceForUnit = null
        // action
        updateEntity.productAction = null
        updateEntity.actionValidTo = null

        productDataUpdateEntityDao.update(updateEntity)
        log.info("product with id {} was reset/mark as unavailable", productId)
    }

    override fun resetUpdateDateProduct(productId: Long) {
        internalLastTimeDataUpdated(productId, null)
    }


    override fun markProductAsNotInterested(productId: Long) {
        // vyhladam povodny produkt
        val productEntity = productEntityDao.findById(productId)

        // premapujem do noveho
        val notInterestedProductEntity = mapper.map(productEntity, NotInterestedProductEntity::class.java)

        // ulozim ho
        notInterestedProductDbDao.save(notInterestedProductEntity)
        log.debug("created new {} with id {}", notInterestedProductEntity.javaClass.simpleName, notInterestedProductEntity.id)

        // odmazem stary
        for (groupEntity in groupEntityDao.findGroupsForProduct(productId)) {
            groupEntity.products.remove(productEntity)
            groupEntityDao.update(groupEntity)
            log.debug("removed product '{}' from group '{}'", productEntity.name, groupEntity.name)
        }
        productEntityDao.delete(productEntity)
        log.info("deleted {} with id {}", productEntity.javaClass.simpleName, productId)
    }

    override fun findDuplicityProductsByNameAndPriceInEshop(eshopUuid: EshopUuid): List<ProductFullDto> {
        val tmp = HashMap<String, MutableList<ProductEntity>>()
        for (productEntity in productEntityDao.findByFilter(ProductFilterUIDto(eshopUuid))) {
            var values: MutableList<ProductEntity>? = tmp[productEntity.name]
            if (values == null) {
                values = ArrayList()
            }
            values.add(productEntity)
            tmp[productEntity.name] = values
        }

        val result = ArrayList<ProductEntity>()
        for (name in tmp.keys) {
            val productEntities = tmp[name]!!
            if (productEntities.size > 1) {
                result.addAll(productEntities)
            }
        }
        return mapper.mapAsList(result, ProductFullDto::class.java)
    }

    override fun getProductWithUrl(productUrl: String, productIdToIgnore: Long?): Optional<Long> {
        notEmpty(productUrl, "productUrl")
        notNull(productIdToIgnore, "productIdToIgnore")

        return productEntityDao.getProductWithUrl(productUrl, productIdToIgnore)
    }

    private fun internalLastTimeDataUpdated(productId: Long?, lastTimeDataUpdated: Date?) {
        val updateEntity = productDataUpdateEntityDao.findById(productId!!)
        updateEntity.lastTimeDataUpdated = lastTimeDataUpdated
        productDataUpdateEntityDao.update(updateEntity)
    }

}
