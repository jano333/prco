package sk.hudak.prco.dao.db

import sk.hudak.prco.dao.BaseDao
import sk.hudak.prco.dto.product.NewProductFilterUIDto
import sk.hudak.prco.model.NewProductEntity
import java.util.*

interface NewProductEntityDbDao : BaseDao<NewProductEntity> {

    //TODO rename na countOfAllNewProducts
    val countOfAllNewProducts: Long

    fun existWithUrl(url: String): Boolean

    fun findAll(): List<NewProductEntity>

    fun findInvalid(maxCountOfInvalid: Int): List<NewProductEntity>

    fun countOfAllInvalidNewProduct(): Long

    fun findFirstInvalid(): Optional<NewProductEntity>

    fun findByFilter(filter: NewProductFilterUIDto): List<NewProductEntity>
}
