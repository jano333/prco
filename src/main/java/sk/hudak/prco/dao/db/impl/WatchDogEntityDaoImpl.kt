package sk.hudak.prco.dao.db.impl

import org.springframework.stereotype.Repository
import sk.hudak.prco.dao.db.WatchDogEntityDao
import sk.hudak.prco.model.QWatchDogEntity
import sk.hudak.prco.model.WatchDogEntity
import javax.persistence.EntityManager

@Repository
open class WatchDogEntityDaoImpl(em: EntityManager) : BaseDaoImpl<WatchDogEntity>(em), WatchDogEntityDao {

    override fun findById(id: Long): WatchDogEntity =
            findById(WatchDogEntity::class.java, id)

    override fun findAll(): List<WatchDogEntity> =
            from(QWatchDogEntity.watchDogEntity).fetch()

    override fun existWithUrl(productUrl: String): Boolean {
        val query = from(QWatchDogEntity.watchDogEntity)
        query.where(QWatchDogEntity.watchDogEntity.productUrl.eq(productUrl))
        return query.fetchCount() == 1L
    }
}
