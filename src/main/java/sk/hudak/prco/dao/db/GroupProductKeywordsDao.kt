package sk.hudak.prco.dao.db

import sk.hudak.prco.dao.BaseDao
import sk.hudak.prco.model.GroupEntity
import sk.hudak.prco.model.GroupProductKeywordsEntity

interface GroupProductKeywordsDao : BaseDao<GroupProductKeywordsEntity> {

    fun existGroupWithKeywords(groupId: Long, keywords: String): Boolean

    fun findByGroupId(groupId: Long?): List<GroupProductKeywordsEntity>

    fun findKeywordsForGroupId(groupId: Long?): List<Array<String>>

    fun findGroupsl(): List<GroupEntity>
}
