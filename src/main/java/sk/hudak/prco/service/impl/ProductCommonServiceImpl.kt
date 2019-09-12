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
import kotlin.streams.toList

@Service("productCommonService")
open class ProductCommonServiceImpl(private val newProductEntityDao: NewProductEntityDbDao,
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
            result.countOfNewProducts = newProductEntityDao.countOfAll
            result.countOfInterestedProducts = productEntityDao.countOfAll
            result.countOfNotInterestedProducts = notInterestedProductDbDao.countOfAll

            result.countOfProductsNotInAnyGroup = groupOfProductFindEntityDao.countOfProductsWitchAreNotInAnyGroup()

            val groupNames = groupEntityDao.findAllGroupNames()
            val countProductInGroup = HashMap<String, Long>(groupNames.size)
            for (groupName in groupNames) {
                countProductInGroup[groupName] = groupOfProductFindEntityDao.countOfProductInGroup(groupName)
            }
            result.countProductInGroup = countProductInGroup

            val eshopProductInfo = EnumMap<EshopUuid, EshopProductInfoDto>(EshopUuid::class.java)
            EshopUuid.values().forEach {
                eshopProductInfo[it] = EshopProductInfoDto(
                        productEntityDao.countOfAllProductInEshop(it),

                        //TODO udaj ohladne hodin zobrat z eshop configu
                        productEntityDao.countOfAllProductInEshopUpdatedMax24Hours(it)
                        //TODO
                        /*0, 0, 0*/)
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
        if (notInterestedProductDbDao.existWithUrl(productURL)) {
            true
        }
        // produkty, o ktore mam zaujem - aktualizuju sa
        return productEntityDao.existWithUrl(productURL)
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

            if (notInterestedProductDbDao.existWithUrl(newProductEntity.url)) {
                log.warn("product with url {} already exist in not interested products -> deleting from new", newProductEntity.url)

            } else {
                // map NewProductEntity -> NotInterestedProductEntity
                val notInterestedProductEntity = mapper.map(newProductEntity, NotInterestedProductEntity::class.java)

                // save new NotInterestedProductEntity
                notInterestedProductDbDao.save(notInterestedProductEntity)
                log.trace("created new {} with id {}", NotInterestedProductEntity::class.java.simpleName, notInterestedProductEntity.id)
            }
            // remove NewProductEntity
            newProductEntityDao.delete(newProductEntity)
            log.debug("deleted {} with id {}", NewProductEntity::class.java.simpleName, newProductEntity.id)
        }
    }

    override fun importNewProducts(newProductList: List<NewProductFullDto>): Long {
        // validacia na povinne parametre
        newProductList.forEach {
            notNull(it, "dto")
            notNullNotEmpty(it.url, "url")
            notNullNotEmpty(it.name, "name")
            notNull(it.confirmValidity, "confirmValidity")
        }

        // filter na tie, ktore este nemam v DB
        val notExistingYet = newProductList.stream()
                .filter { (_, _, _, url) -> !internalExistProductWithURL(url) }
                .toList()

        // premapovanie a save do DB
        notExistingYet.forEach {
            // NewProductFullDto -> NewProductEntity
            newProductEntityDao.save(mapper.map(it, NewProductEntity::class.java))
        }

        return notExistingYet.size.toLong()
    }

    override fun importProducts(productList: List<ProductFullDto>): Long {
        productList.forEach { dto ->
            notNull(dto, "dto")
            notNullNotEmpty(dto.url, "url")
            notNullNotEmpty(dto.name, "name")
            //TODO ostatne validacie na povinne atributy

        }

        // filter na tie, ktore este nemam v DB
        val notExistingYet = productList.stream()
                .filter { !internalExistProductWithURL(it.url) }
                .toList()

        // premapovanie a save do DB
        notExistingYet.forEach { productEntityDao.save(mapper.map(it, ProductEntity::class.java)) }

        return notExistingYet.size.toLong()
    }


    override fun importNotInterestedProducts(notInterestedProductList: List<NotInterestedProductFullDto>): Long {

        notInterestedProductList.forEach {
            notNull(it, "dto")
            notNullNotEmpty(it.url, "url")
            notNullNotEmpty(it.name, "name")
            //TODO ostatne validacie na povinne atributy

        }

        // filter na tie, ktore este nemam v DB
        val notExistingYet = notInterestedProductList.stream()
                .filter {
                    !internalExistProductWithURL(it.url)
                }
                .toList()

        // premapovanie a save do DB
        notExistingYet.forEach {
            notInterestedProductDbDao.save(mapper.map(it, NotInterestedProductEntity::class.java))
        }

        return notExistingYet.size.toLong()
    }
}
