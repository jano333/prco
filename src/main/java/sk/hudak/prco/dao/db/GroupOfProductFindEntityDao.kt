package sk.hudak.prco.dao.db

import sk.hudak.prco.model.ProductEntity
import java.util.*

interface GroupOfProductFindEntityDao {

    fun findProductsWitchAreNotInAnyGroup(): List<ProductEntity>

    fun countOfProductsWitchAreNotInAnyGroup(): Long

    fun countOfProductInGroup(groupName: String): Long

    fun findFirstProductGroupId(productId: Long?): Optional<Long>

    fun findGroupIdsWithProductId(productId: Long?): List<Long>
}
