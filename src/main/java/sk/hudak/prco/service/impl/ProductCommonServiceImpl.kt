package sk.hudak.prco.service.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dao.db.*
import sk.hudak.prco.dto.EshopProductInfoDto
import sk.hudak.prco.dto.ProductStatisticInfoDto
import sk.hudak.prco.dto.product.NewProductFullDto
import sk.hudak.prco.dto.product.NotInterestedProductFullDto
import sk.hudak.prco.dto.product.ProductFullDto
import sk.hudak.prco.exception.PrcoRuntimeException
import sk.hudak.prco.mapper.PrcoOrikaMapper
import sk.hudak.prco.model.NewProductEntity
import sk.hudak.prco.model.NotInterestedProductEntity
import sk.hudak.prco.model.ProductEntity
import sk.hudak.prco.service.ProductCommonService
import sk.hudak.prco.utils.Validate.notNull
import sk.hudak.prco.utils.Validate.notNullNotEmpty
import java.util.*
import java.util.stream.Collectors

//TODO preco to kontlin chce ze to ma byt ope?

@Service("productCommonService")
open class ProductCommonServiceImpl(
        private val newProductEntityDao: NewProductEntityDbDao,
        private val notInterestedProductDbDao: NotInterestedProductDbDao,
        private val productEntityDao: ProductEntityDao,
        private val groupEntityDao: GroupEntityDao,
        private val groupOfProductFindEntityDao: GroupOfProductFindEntityDao,
        private val mapper: PrcoOrikaMapper
) : ProductCommonService {

    companion object {
        val log = LoggerFactory.getLogger(ProductCommonServiceImpl::class.java)!!
    }

    // FIXME cez stream prepisat
    override val statisticsOfProducts: ProductStatisticInfoDto
        @Transactional
        get() {
            val result = ProductStatisticInfoDto()
            result.countOfAllProducts = productEntityDao.count()
            result.countOfProductsNotInAnyGroup = groupOfProductFindEntityDao.countOfProductsWitchAreNotInAnyGroup()

            val groupNames = groupEntityDao.findAllGroupNames()
            val countProductInGroup = HashMap<String, Long>(groupNames.size)
            for (groupName in groupNames) {
                countProductInGroup[groupName] = groupOfProductFindEntityDao.countOfProductInGroup(groupName)
            }
            result.countProductInGroup = countProductInGroup

            val eshopProductInfo = EnumMap<EshopUuid, EshopProductInfoDto>(EshopUuid::class.java)
            EshopUuid.values().forEach { eshopUuid ->
                val countOfAllProduct = productEntityDao.countOfAllProductInEshop(eshopUuid)
                val countOfAlreadyUpdated = productEntityDao.countOfAllProductInEshopUpdatedMax24Hours(eshopUuid)
                eshopProductInfo[eshopUuid] = EshopProductInfoDto(countOfAllProduct, countOfAlreadyUpdated)
            }
            result.eshopProductInfo = eshopProductInfo

            return result
        }


    override fun existProductWithURL(productURL: String): Boolean {
        return internalExistProductWithURL(productURL)
    }

    private fun internalExistProductWithURL(productURL: String?): Boolean {
        notNullNotEmpty("productURL", productURL!!)

        // nove produkty
        if (newProductEntityDao.existWithUrl(productURL)) {
            return true
        }
        // produkty, o ktore nemam zaujem
        return if (notInterestedProductDbDao.existWithUrl(productURL)) {
            true
        } else productEntityDao.existWithUrl(productURL)
        // produkty, o ktore mam zaujem - aktualizuju sa
    }

    override fun markNewProductAsInterested(vararg newProductIds: Long) {
        //TODO
        //        atLeastOneIsNotNull(newProductIds, "newProductIds");

        Arrays.stream(newProductIds).forEach { newProductId ->
            // find existing new product
            val newProductEntity = newProductEntityDao.findById(newProductId)

            if (java.lang.Boolean.TRUE != newProductEntity.confirmValidity) {
                throw PrcoRuntimeException(newProductEntity.javaClass.simpleName + " with id " + newProductEntity.id + " is not confirmed.")
            }

            // overim ci uz s takou URL neexistuje PRODUCT... ak ano tak ho len odsranim z NEW a nerobim save do ProductEntity
            if (productEntityDao.existWithUrl(newProductEntity.url!!)) {
                log.debug("product with url {} already exist in products -> deleting from new", newProductEntity.url)

            } else {
                // map NewProductEntity -> ProductEntity
                val productEntity = mapper.map(newProductEntity, ProductEntity::class.java)

                // save new ProductEntity
                productEntityDao.save(productEntity)
                log.debug("created new {} with id {}", productEntity.javaClass.simpleName, productEntity.id)
            }

            // remove NewProductEntity
            newProductEntityDao.delete(newProductEntity)
            log.debug("deleted {} with id {}", newProductEntity.javaClass.simpleName, newProductEntity.id)
        }
    }

    override fun markNewProductAsNotInterested(vararg newProductIds: Long) {
        //        atLeastOneIsNotNull(newProductIds, "newProductIds");

        for (newProductId in newProductIds) {

            // find existing product
            val newProductEntity = newProductEntityDao.findById(newProductId)

            //TODO tak ako v metode hore impl -> overim ci uz s takou URL neexistuje PRODUCT...

            // map NewProductEntity -> NotInterestedProductEntity
            val notInterestedProductEntity = mapper.map(newProductEntity, NotInterestedProductEntity::class.java)

            // save new NotInterestedProductEntity
            notInterestedProductDbDao.save(notInterestedProductEntity)
            log.trace("created new {} with id {}", NotInterestedProductEntity::class.java.simpleName, notInterestedProductEntity.id)

            // remove NewProductEntity
            newProductEntityDao.delete(newProductEntity)
            log.debug("deleted {} with id {}", NewProductEntity::class.java.simpleName, newProductEntity.id)
        }
    }

    override fun importNewProducts(newProductList: List<NewProductFullDto>): Long {
        notNull(newProductList, "newProductList")

        // validacia na povinne parametre
        newProductList.forEach { dto ->
            notNull(dto, "dto")
            notNullNotEmpty(dto.url, "url")
            notNullNotEmpty(dto.name, "name")
            notNull(dto.confirmValidity, "confirmValidity")
        }

        // filter na tie, ktore este nemam v DB
        val notExistingYet = newProductList.stream()
                .filter { (_, _, _, url) -> !internalExistProductWithURL(url) }
                .collect(Collectors.toList())


        // premapovanie a save do DB
        notExistingYet.forEach { dto ->
            // NewProductFullDto -> NewProductEntity
            newProductEntityDao.save(mapper.map(dto, NewProductEntity::class.java))
        }

        return notExistingYet.size.toLong()
    }

    override fun importProducts(productList: List<ProductFullDto>): Long {
        notNull(productList, "productList")

        productList.forEach { dto ->
            notNull(dto, "dto")
            notNullNotEmpty(dto.url, "url")
            notNullNotEmpty(dto.name, "name")
            //TODO ostatne validacie na povinne atributy

        }

        // filter na tie, ktore este nemam v DB
        val notExistingYet = productList.stream()
                .filter { dto -> !internalExistProductWithURL(dto.url) }
                .collect(Collectors.toList())

        // premapovanie a save do DB
        notExistingYet.forEach { dto -> productEntityDao.save(mapper.map(dto, ProductEntity::class.java)) }

        return notExistingYet.size.toLong()
    }


    override fun importNotInterestedProducts(productList: List<NotInterestedProductFullDto>): Long {
        notNull(productList, "productList")

        productList.forEach { dto ->
            notNull(dto, "dto")
            notNullNotEmpty(dto.url, "url")
            notNullNotEmpty(dto.name, "name")
            //TODO ostatne validacie na povinne atributy

        }

        // filter na tie, ktore este nemam v DB
        val notExistingYet = productList.stream()
                .filter { dto -> !internalExistProductWithURL(dto.url) }
                .collect(Collectors.toList())

        // premapovanie a save do DB
        notExistingYet.forEach { dto -> notInterestedProductDbDao.save(mapper.map(dto, NotInterestedProductEntity::class.java)) }

        return notExistingYet.size.toLong()
    }
}
