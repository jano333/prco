package sk.hudak.prco.service

import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dto.product.NotInterestedProductFindDto
import sk.hudak.prco.dto.product.NotInterestedProductFullDto

interface NotInterestedProductService {

    fun deleteNotInterestedProducts(vararg notInterestedProductIds: Long)

    fun deleteNotInterestedProducts(eshopUuid: EshopUuid)

    fun findNotInterestedProducts(findDto: NotInterestedProductFindDto): List<NotInterestedProductFullDto>

    fun removeNotInterestedProductsByCount(eshopUuid: EshopUuid, maxCountToDelete: Long): Int
}
