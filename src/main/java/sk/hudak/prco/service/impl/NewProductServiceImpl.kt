package sk.hudak.prco.service.impl

import org.apache.commons.lang3.StringUtils
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import sk.hudak.prco.api.ErrorType
import sk.hudak.prco.api.Unit
import sk.hudak.prco.dao.db.NewProductEntityDbDao
import sk.hudak.prco.dto.UnitData
import sk.hudak.prco.dto.UnitTypeValueCount
import sk.hudak.prco.dto.error.ErrorCreateDto
import sk.hudak.prco.dto.internal.ProductNewData
import sk.hudak.prco.dto.newproduct.NewProductCreateDto
import sk.hudak.prco.dto.newproduct.NewProductFilterUIDto
import sk.hudak.prco.dto.newproduct.NewProductFullDto
import sk.hudak.prco.dto.newproduct.NewProductInfoDetail
import sk.hudak.prco.dto.product.ProductUnitDataDto
import sk.hudak.prco.exception.PrcoRuntimeException
import sk.hudak.prco.mapper.PrcoOrikaMapper
import sk.hudak.prco.model.NewProductEntity
import sk.hudak.prco.parser.HtmlParser
import sk.hudak.prco.parser.UnitParser
import sk.hudak.prco.service.ErrorService
import sk.hudak.prco.service.NewProductService
import sk.hudak.prco.utils.Validate.notNegativeAndNotZeroValue
import sk.hudak.prco.utils.Validate.notNull
import sk.hudak.prco.utils.Validate.notNullNotEmpty
import java.util.*
import java.util.stream.Stream

//TODO KT overit si @Autovired anotacia je potrebna

@Service("newProductService")
class NewProductServiceImpl(@Autowired private val newProductEntityDao: NewProductEntityDbDao,
                            @Autowired private val mapper: PrcoOrikaMapper,
                            @Autowired private val unitParser: UnitParser,
                            @Autowired private val errorService: ErrorService,
                            @Autowired private val htmlParser: HtmlParser) : NewProductService {

    companion object {
        val log = LoggerFactory.getLogger(NewProductServiceImpl::class.java)!!

        private const val NEW_PRODUCT_ID = "newProductId"
    }

    override val countOfInvalidNewProduct: Long
        get() {
            return newProductEntityDao.countOfAllInvalidNewProduct()
        }

    override val countOfAllNewProducts: Long
        get() {
            return newProductEntityDao.countOfAllNewProducts
        }

    override fun createNewProduct(newProductCreateDto: NewProductCreateDto): Long {
        try {
            //TODO prejst kontroly na not null
            notNull(newProductCreateDto.eshopUuid, "eshopUuid")
            notNullNotEmpty(newProductCreateDto.url, "url")
            notNullNotEmpty(newProductCreateDto.name, "name")

            // check if product with given URL already exist
            // TODO impl exist method
            if (existProductWithUrl(newProductCreateDto.url)) {
                throw PrcoRuntimeException("Product with URL ${newProductCreateDto.url} already exist.")
            }

            val entity = NewProductEntity()
            entity.eshopUuid = newProductCreateDto.eshopUuid
            entity.url = newProductCreateDto.url
            entity.name = newProductCreateDto.name
            entity.valid = newProductCreateDto.isValid
            entity.confirmValidity = java.lang.Boolean.FALSE
            // nepovinne:
            entity.unit = newProductCreateDto.unit
            entity.unitValue = newProductCreateDto.unitValue
            entity.unitPackageCount = newProductCreateDto.unitPackageCount
            entity.pictureUrl = newProductCreateDto.pictureUrl

            val id = newProductEntityDao.save(entity)
            log.debug("create new entity {} with id {}", entity.javaClass.simpleName, entity.id)
            return id!!

        } catch (e: PrcoRuntimeException) {
            throw e

        } catch (e: Exception) {
            val errMsg = "error creating " + ProductNewData::class.java.simpleName
            log.debug(errMsg, e)
            throw PrcoRuntimeException(errMsg, e)
        }

    }

    override fun getNewProduct(newProductId: Long): NewProductFullDto {
        return mapper.map(newProductEntityDao.findById(newProductId), NewProductFullDto::class.java)
    }

    override fun findFirstInvalidNewProduct(): NewProductInfoDetail? {
        return newProductEntityDao.findFirstInvalid()
                .map { newProductEntity -> mapper.map(newProductEntity, NewProductInfoDetail::class.java) }
                .orElse(null)
    }


    override fun repairInvalidUnitForNewProduct(newProductId: Long?, correctUnitData: UnitData) {
        notNull(newProductId, NEW_PRODUCT_ID)
        notNull(correctUnitData, "correctUnitData")
        notNull(correctUnitData.unit, "unit")
        notNull(correctUnitData.unitValue, "unitValue")
        notNull(correctUnitData.unitPackageCount, "unitPackageCount")
        //TODO validacie na plusove hodnoty pre unit value a package count...

        //toto by malo hodit mandatory exception ak sa nepodari...
        val entity = newProductEntityDao.findById(newProductId)
        entity.unit = correctUnitData.unit
        entity.unitValue = correctUnitData.unitValue
        entity.unitPackageCount = correctUnitData.unitPackageCount

        entity.valid = java.lang.Boolean.TRUE
        entity.confirmValidity = java.lang.Boolean.TRUE

        newProductEntityDao.update(entity)

        log.debug("product with id {} has been updated to {}", newProductId, correctUnitData)
    }

    private fun existProductWithUrl(url: String?): Boolean {
        //TODO impl
        return false
    }

    override fun reprocessProductData(newProductId: Long?) {
        notNull(newProductId, NEW_PRODUCT_ID)

        val productEntity = newProductEntityDao.findById(newProductId)
        // parsujem
        val productNewData = htmlParser.parseProductNewData(productEntity.url!!)

        // name
        if (StringUtils.isNoneBlank(productNewData.name)) {
            productEntity.name = productNewData.name
        }

        // picture URL
        if (StringUtils.isNoneBlank(productNewData.pictureUrl)) {
            productEntity.pictureUrl = productNewData.pictureUrl
        }

        if (productNewData.unit != null) {
            productEntity.unit = productNewData.unit
            productEntity.unitValue = productNewData.unitValue
            productEntity.unitPackageCount = productNewData.unitPackageCount
            productEntity.valid = java.lang.Boolean.TRUE
            log.debug("new product with id {} was updated with unit data {}", productEntity.id,
                    UnitTypeValueCount(productNewData.unit!!,
                            productNewData.unitValue!!, productNewData.unitPackageCount))

        } else {
            log.warn("parsing unit data failed for name {}", productEntity.name)
            errorService.createError(ErrorCreateDto(
                    errorType = ErrorType.PARSING_PRODUCT_UNIT_ERR,
                    url = productEntity.url,
                    eshopUuid = productEntity.eshopUuid,
                    additionalInfo = productEntity.name))
        }

        newProductEntityDao.update(productEntity)
    }

    override fun confirmUnitDataForNewProducts(newProductIds: Array<Long>) {
        //TODO validacia
//        atLeastOneIsNotNull(newProductIds, "newProductIds")

        Stream.of(newProductIds)
                .map { (newProductId: Long) -> newProductEntityDao.findById(newProductId) }
                .forEach { entity ->
                    entity.confirmValidity = java.lang.Boolean.TRUE
                    newProductEntityDao.update(entity)
                    log.debug("unit data for product ${entity.id} mark as confirm")
                }
    }

    override fun fixAutomaticallyProductUnitData(maxCountOfInvalid: Int): Long {
        notNegativeAndNotZeroValue(maxCountOfInvalid, "maxCountOfInvalid")

        val invalidProductList = newProductEntityDao.findInvalid(maxCountOfInvalid)
        log.debug("count of invalid products: {}", invalidProductList.size)

        val countOfRepaired = intArrayOf(0)

        invalidProductList.forEach { productEntity ->
            val unitTypeValueCountOpt = unitParser.parseUnitTypeValueCount(productEntity.name!!)
            unitTypeValueCountOpt.ifPresent { (unit, value, packageCount) ->
                productEntity.unit = unit
                productEntity.unitValue = value
                productEntity.unitPackageCount = packageCount
                productEntity.valid = java.lang.Boolean.TRUE
                newProductEntityDao.update(productEntity)

                countOfRepaired[0]++
            }
        }
        log.info("count of repaired products: {}", countOfRepaired[0])
        return countOfRepaired[0].toLong()
    }

    override fun findNewProducts(filter: NewProductFilterUIDto): List<NewProductFullDto> {
        val entities = newProductEntityDao.findByFilter(filter)

        //FIXME toto nefunguje(opravit aby fungovalo cez oriku):
        //        List<NewProductFullDto> result = mapper.mapAsList(entities, NewProductFullDto.class);

        val result = ArrayList<NewProductFullDto>(entities.size)
        for (entity in entities) {
            result.add(NewProductFullDto(
                    created = entity.created,
                    updated = entity.updated,
                    id = entity.id,
                    url = entity.url,
                    name = entity.name,
                    eshopUuid = entity.eshopUuid,
                    unit = entity.unit,
                    unitValue = entity.unitValue,
                    unitPackageCount = entity.unitPackageCount,
                    valid = entity.valid,
                    confirmValidity = entity.confirmValidity,
                    pictureUrl = entity.pictureUrl))
        }
        return result
    }

    override fun findNewProductsForExport(): List<NewProductFullDto> {
        return mapper.mapAsList(newProductEntityDao.findAll(), NewProductFullDto::class.java)
    }

    override fun updateProductUnitData(productUnitDataDto: ProductUnitDataDto) {
        notNull(productUnitDataDto, "productUnitDataDto")
        notNull(productUnitDataDto.id, "id")
        notNullNotEmpty(productUnitDataDto.unit, "unit")
        notNull(productUnitDataDto.unitValue, "unitValue")
        notNull(productUnitDataDto.unitPackageCount, "unitPackageCount")

        // name is ignored

        val entity = newProductEntityDao.findById(productUnitDataDto.id)
        entity.unit = Unit.valueOf(productUnitDataDto.unit!!)
        entity.unitValue = productUnitDataDto.unitValue
        entity.unitPackageCount = productUnitDataDto.unitPackageCount
        entity.valid = java.lang.Boolean.TRUE
        newProductEntityDao.update(entity)

        log.debug("new product with id {} was updated for unit data values", productUnitDataDto.id)

    }

    override fun deleteNewProducts(newProductIds: Array<Long>) {
        newProductIds.forEach {
            newProductEntityDao.delete(newProductEntityDao.findById(it))
        }
    }


}
