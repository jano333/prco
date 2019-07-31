package sk.hudak.prco.service

import sk.hudak.prco.api.EshopUuid

interface NotInterestedProductService {

    fun deleteNotInterestedProducts(vararg notInterestedProductIds: Long)

    fun deleteNotInterestedProducts(eshopUuid: EshopUuid)
}
