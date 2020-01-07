package sk.hudak.prco.dao.db

import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dao.BaseDao
import sk.hudak.prco.dto.product.NewProductFilterUIDto
import sk.hudak.prco.model.NewProductEntity

interface NewProductEntityDbDao : BaseDao<NewProductEntity> {

    fun countOfAll(): Long

    fun existWithUrl(url: String): Boolean

    fun findAll(): List<NewProductEntity>

    fun findInvalid(maxCountOfInvalid: Int): List<NewProductEntity>

    fun countOfAllInvalidNewProduct(): Long

    fun findFirstInvalid(): NewProductEntity?

    fun findByFilter(filter: NewProductFilterUIDto): List<NewProductEntity>

    fun findByCount(eshopUuid: EshopUuid, maxCountToDelete: Long): List<NewProductEntity>

    fun countOfAllProductInEshop(eshopUuid: EshopUuid): Long

}
