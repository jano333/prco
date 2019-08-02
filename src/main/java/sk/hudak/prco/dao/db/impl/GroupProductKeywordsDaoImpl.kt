package sk.hudak.prco.dao.db.impl

import org.springframework.stereotype.Component
import sk.hudak.prco.dao.db.GroupProductKeywordsDao
import sk.hudak.prco.model.GroupProductKeywordsEntity
import sk.hudak.prco.model.QGroupProductKeywordsEntity
import java.util.stream.Collectors

@Component
open class GroupProductKeywordsDaoImpl : BaseDaoImpl<GroupProductKeywordsEntity>(), GroupProductKeywordsDao {

    override fun findById(id: Long): GroupProductKeywordsEntity {
        return findById(GroupProductKeywordsEntity::class.java, id)
    }

    override fun findByGroupId(groupId: Long?): List<GroupProductKeywordsEntity> {
        return queryFactory
                .select(QGroupProductKeywordsEntity.groupProductKeywordsEntity)
                .from(QGroupProductKeywordsEntity.groupProductKeywordsEntity)
                .where(QGroupProductKeywordsEntity.groupProductKeywordsEntity.group.id.eq(groupId!!))
                .fetch()
    }

    override fun findKeywordsForGroupId(groupId: Long?): List<Array<String>> {
        return queryFactory
                .select(QGroupProductKeywordsEntity.groupProductKeywordsEntity.keyWords)
                .from(QGroupProductKeywordsEntity.groupProductKeywordsEntity)
                .where(QGroupProductKeywordsEntity.groupProductKeywordsEntity.group.id.eq(groupId!!))
                .fetch()
                .stream()
                .map { value -> value.split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray() }
                .collect(Collectors.toList())
    }
}
