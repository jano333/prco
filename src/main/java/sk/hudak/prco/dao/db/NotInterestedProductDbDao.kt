package sk.hudak.prco.dao.db

import sk.hudak.prco.dao.BaseDao
import sk.hudak.prco.dto.product.NotInterestedProductFindDto
import sk.hudak.prco.model.NotInterestedProductEntity

interface NotInterestedProductDbDao : BaseDao<NotInterestedProductEntity> {

    fun existWithUrl(url: String): Boolean

    fun findAll(findDto: NotInterestedProductFindDto): List<NotInterestedProductEntity>

    fun findFistTenURL(): List<String>
}
