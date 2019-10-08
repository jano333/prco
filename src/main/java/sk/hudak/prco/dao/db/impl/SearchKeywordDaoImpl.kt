package sk.hudak.prco.dao.db.impl

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository
import sk.hudak.prco.dao.db.SearchKeywordDao
import sk.hudak.prco.model.QSearchKeywordEntity
import sk.hudak.prco.model.SearchKeywordEntity
import javax.persistence.EntityManager

@Repository
open class SearchKeywordDaoImpl(em: EntityManager) : BaseDaoImpl<SearchKeywordEntity>(em), SearchKeywordDao {

    override fun findById(id: Long): SearchKeywordEntity =
            findById(SearchKeywordEntity::class.java, id)

    override fun existSearchKeywordByName(name: String, idToSkip: Long): Boolean {
        return JPAQueryFactory(em)
                .select(QSearchKeywordEntity.searchKeywordEntity.id)
                .from(QSearchKeywordEntity.searchKeywordEntity)
                .where(QSearchKeywordEntity.searchKeywordEntity.id.ne(idToSkip!!))
                .where(QSearchKeywordEntity.searchKeywordEntity.name.eq(name))
                .fetchFirst() != null
    }

    override fun findAll(): List<SearchKeywordEntity> =
            from(QSearchKeywordEntity.searchKeywordEntity).fetch()

}