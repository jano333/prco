package sk.hudak.prco.dao.db.impl

import com.querydsl.core.types.EntityPath
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.beans.factory.annotation.Autowired
import sk.hudak.prco.dao.BaseDao
import sk.hudak.prco.exception.PrcoRuntimeException
import sk.hudak.prco.model.core.DbEntity
import java.util.*
import javax.persistence.EntityManager

abstract class BaseDaoImpl<T : DbEntity> : BaseDao<T> {

    @Autowired
    protected val em: EntityManager? = null

    protected val queryFactory: JPAQueryFactory
        get() = JPAQueryFactory(em)

    override fun save(entity: T): Long {
        em!!
        entity.created = Date()
        entity.updated = entity.created
        em.persist(entity)
        return entity.id!!
    }

    override fun update(entity: T) {
        em!!
        entity.updated = Date()
        em.merge(entity)
    }

    protected fun findById(clazz: Class<T>, id: Long?): T {
        em!!
        return em.find(clazz, id) ?: throw PrcoRuntimeException("Entity ${clazz.simpleName} not found by id $id")
    }

    override fun delete(entity: T) {
        em!!
        em.remove(entity)
    }

    protected fun from(from: EntityPath<T>): JPAQuery<T> {
        //FIXME skusit s jednou instanciou factory...
        return JPAQueryFactory(em).selectFrom(from)
    }
}
