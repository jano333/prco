package sk.hudak.prco.service

import sk.hudak.prco.dto.GroupProductKeywordsCreateDto
import sk.hudak.prco.dto.GroupProductKeywordsFullDto

interface GroupProductKeywordsService {

    /**
     * @param createDto data from creating of new keyword for given group
     * @return primary key id
     */
    fun createGroupProductKeywords(createDto: GroupProductKeywordsCreateDto): Long

    /**
     * @param groupId group id
     * @return
     */
    fun getGroupProductKeywordsByGroupId(groupId: Long): GroupProductKeywordsFullDto?

    /**
     * @param groupId group id
     */
    fun removeAllKeywordForGroupId(groupId: Long)

    fun findAllGroupProductKeywords(): List<GroupProductKeywordsFullDto>
}
