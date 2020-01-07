package sk.hudak.prco.dao.db

import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dao.BaseDao
import sk.hudak.prco.dto.product.NotInterestedProductFindDto
import sk.hudak.prco.model.NotInterestedProductEntity

interface NotInterestedProductDbDao : BaseDao<NotInterestedProductEntity> {

    fun countOfAll(): Long

    fun existWithUrl(url: String): Boolean

    fun findAll(findDto: NotInterestedProductFindDto): List<NotInterestedProductEntity>

    fun findFistTenURL(): List<String>

    fun findByCount(eshopUuid: EshopUuid, maxCountToDelete: Long): List<NotInterestedProductEntity>

    fun countOfAllProductInEshop(eshopUuid: EshopUuid): Long

}
