package sk.hudak.prco.dto.product

import sk.hudak.prco.dto.DtoAble
import java.math.BigDecimal

data class ProductUnitDataDto(
        // read only
        var id: Long? = null,
        var name: String? = null,
        // edit:
        var unit: String? = null,
        var unitValue: BigDecimal? = null,
        var unitPackageCount: Int? = null) : DtoAble
