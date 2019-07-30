package sk.hudak.prco.dto.newproduct

import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.Unit
import sk.hudak.prco.dto.DtoAble
import java.math.BigDecimal
import java.util.*

data class NewProductFullDto(
        var id: Long? = null,
        var created: Date? = null,
        var updated: Date? = null,
        var url: String? = null,
        var name: String? = null,
        var eshopUuid: EshopUuid? = null,
        var unit: Unit? = null,
        var unitValue: BigDecimal? = null,
        var unitPackageCount: Int? = null,
        var valid: Boolean? = null,
        var confirmValidity: Boolean? = null,
        var pictureUrl: String? = null) : DtoAble
