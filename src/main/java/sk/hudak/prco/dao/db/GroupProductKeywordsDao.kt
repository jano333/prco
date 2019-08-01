package sk.hudak.prco.dao.db

import sk.hudak.prco.dao.BaseDao
import sk.hudak.prco.model.GroupProductKeywordsEntity

interface GroupProductKeywordsDao : BaseDao<GroupProductKeywordsEntity> {

    fun findByGroupId(groupId: Long?): List<GroupProductKeywordsEntity>

    fun findKeywordsForGroupId(groupId: Long?): List<Array<String>>
}
