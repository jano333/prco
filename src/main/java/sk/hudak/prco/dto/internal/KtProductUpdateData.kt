package sk.hudak.prco.dto.internal

import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.ProductAction
import java.math.BigDecimal
import java.util.*

data class KtProductUpdateData(val url: String?,
                               val eshopUuid: EshopUuid?,
        // nepovinne:
                               var name: String?,
                               var priceForPackage: BigDecimal?,

                               var productAction: ProductAction?,
                               var actionValidity: Date?) : InternallDto {

    fun isProductAvailable(): Boolean {
        return name != null && priceForPackage != null
    }
}

