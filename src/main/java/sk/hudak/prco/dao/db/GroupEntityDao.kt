package sk.hudak.prco.dao.db

import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dao.BaseDao
import sk.hudak.prco.dto.GroupFilterDto
import sk.hudak.prco.model.GroupEntity
import sk.hudak.prco.model.ProductEntity

interface GroupEntityDao : BaseDao<GroupEntity> {

    //FIXME navratova hodnata nech je boolen ci existuje!!
    fun findGroupByName(groupName: String): GroupEntity?

    //TODO zmenit navratovu hodnotu na Optional<GroupEntity>
    fun existGroupByName(groupName: String, groupIdToSkip: Long?): Boolean

    fun findGroupsWithoutProduct(productId: Long?): List<GroupEntity>

    fun findGroups(groupFilterDto: GroupFilterDto): List<GroupEntity>

    //TODO move to ProductService...
    fun findProductsInGroup(groupId: Long?, withPriceOnly: Boolean, vararg eshopsToSkip: EshopUuid): List<ProductEntity>

    fun findAllGroupNames(): List<String>

    /**
     * Zoznam vsetkych group, v ktorych sa dany product nachadza.
     *
     * @param productId
     * @return
     */
    fun findGroupsForProduct(productId: Long?): List<GroupEntity>
}
