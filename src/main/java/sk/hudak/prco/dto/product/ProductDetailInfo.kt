package sk.hudak.prco.dto.product

import sk.hudak.prco.api.EshopUuid

data class ProductDetailInfo(
        val id: Long? = null,
        val url: String? = null,
        val eshopUuid: EshopUuid? = null)
