package sk.hudak.prco.dao

import sk.hudak.prco.model.core.DbEntity

interface BaseDao<T : DbEntity> {

    fun save(entity: T): Long?

    fun update(entity: T)

    fun findById(id: Long): T

    fun delete(entity: T)
}
