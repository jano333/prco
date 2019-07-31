package sk.hudak.prco.service

import sk.hudak.prco.dto.*

interface GroupService {

    fun createGroup(createDto: GroupCreateDto): Long

    fun updateGroup(updateDto: GroupUpdateDto)

    fun addProductsToGroup(groupId: Long, vararg productIds: Long)

    fun removeProductsFromGroup(groupId: Long?, vararg productIds: Long)

    fun getGroupsWithoutProduct(productId: Long?): List<GroupListDto>

    fun findGroups(groupFilterDto: GroupFilterDto): List<GroupListDto>

    fun findAllGroupExtended(): List<GroupListExtendedDto>

    fun getGroupById(groupId: Long?): GroupIdNameDto
}
