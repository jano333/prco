package sk.hudak.prco.service

import sk.hudak.prco.dto.GroupProductKeywordsCreateDto
import sk.hudak.prco.dto.GroupProductKeywordsFullDto
import java.util.*

interface GroupProductKeywordsService {

    /**
     * @param groupProductKeywordsCreateDto data from creating of new keyword for given group
     * @return primary key id
     */
    fun createGroupProductKeywords(groupProductKeywordsCreateDto: GroupProductKeywordsCreateDto): Long?

    /**
     * @param groupId group id
     * @return
     */
    fun getGroupProductKeywordsByGroupId(groupId: Long?): Optional<GroupProductKeywordsFullDto>

    /**
     * @param groupId group id
     */
    fun removeAllKeywordForGroupId(groupId: Long?)
}
