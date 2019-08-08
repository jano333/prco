package sk.hudak.prco.dao.db

import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dao.BaseDao
import sk.hudak.prco.model.WatchDogEntity

interface WatchDogEntityDao : BaseDao<WatchDogEntity> {

    fun findAll(): List<WatchDogEntity>

    fun existWithUrl(productUrl: String): Boolean

    fun findByCount(eshopUuid: EshopUuid, maxCountToDelete: Long): List<WatchDogEntity>
}
