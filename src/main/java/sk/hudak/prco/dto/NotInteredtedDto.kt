package sk.hudak.prco.dto

import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.Unit
import java.math.BigDecimal
import java.util.*

data class NotInterestedProductFindDto(
        val eshopUuid: EshopUuid? = null) : DtoAble

class NotInterestedProductFullDto {
    var created: Date? = null
    var updated: Date? = null
    var id: Long? = null
    var url: String? = null
    var name: String? = null
    var eshopUuid: EshopUuid? = null
    var unit: Unit? = null
    var unitValue: BigDecimal? = null
    var unitPackageCount: Int? = null

    override fun toString(): String {
        return "NotInterestedProductFullDto{" +
                "created=" + created +
                ", updated=" + updated +
                ", id=" + id +
                ", url='" + url + '\''.toString() +
                ", name='" + name + '\''.toString() +
                ", eshopUuid=" + eshopUuid +
                ", unit=" + unit +
                ", unitValue=" + unitValue +
                ", unitPackageCount=" + unitPackageCount +
                '}'.toString()
    }
}