package sk.hudak.prco.dao.db.impl

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.dao.db.GroupProductKeywordsDao
import sk.hudak.prco.model.GroupEntity
import sk.hudak.prco.model.GroupProductKeywordsEntity
import sk.hudak.prco.model.QGroupProductKeywordsEntity
import java.util.stream.Collectors
import javax.persistence.EntityManager

@Component
open class GroupProductKeywordsDaoImpl(em: EntityManager)
    : BaseDaoImpl<GroupProductKeywordsEntity>(em), GroupProductKeywordsDao {

    override fun existGroupWithKeywords(groupId: Long, keywords: String): Boolean {
        return queryFactory
                .select(QGroupProductKeywordsEntity.groupProductKeywordsEntity.id)
                .from(QGroupProductKeywordsEntity.groupProductKeywordsEntity)
                .where(QGroupProductKeywordsEntity.groupProductKeywordsEntity.group.id.eq(groupId)
                        .and(QGroupProductKeywordsEntity.groupProductKeywordsEntity.keyWords.eq(keywords)))
                .fetchOne() != null
    }

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

    override fun findGroupsl(): List<GroupEntity> {
        return JPAQueryFactory(em)
                .select(QGroupProductKeywordsEntity.groupProductKeywordsEntity.group)
                .from(QGroupProductKeywordsEntity.groupProductKeywordsEntity)
                .distinct()
                .fetch()
    }


}
