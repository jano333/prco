package sk.hudak.prco.dto.newproduct

import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.Unit
import sk.hudak.prco.dto.DtoAble
import java.math.BigDecimal
import java.util.*

data class NewProductCreateDto(
        var eshopUuid: EshopUuid? = null,
        var url: String? = null,
        var name: String? = null,
        var unit: Unit? = null,
        var unitValue: BigDecimal? = null,
        var unitPackageCount: Int? = null,
        var pictureUrl: String? = null) : DtoAble {


    /**
     * @return true, ak sa podarilo vsetko uspesne vyparsovat(vsetky parametre), inak false
     */
    val isValid: Boolean
        get() {
            if (url == null || url!!.trim { it <= ' ' }.isEmpty()) {
                return false
            }
            return if (name == null || name!!.trim { it <= ' ' }.isEmpty()) {
                false
            } else eshopUuid != null && unit != null && unitValue != null && unitPackageCount != null
        }


}

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

data class NewProductFilterUIDto(
        var eshopUuid: EshopUuid? = null,
        var maxCount: Long? = 10) : DtoAble

class NewProductInfoDetail {

    var id: Long? = null
    var created: Date? = null
    var updated: Date? = null
    var url: String? = null
    var name: String? = null
    var eshopUuid: EshopUuid? = null

    var unit: Unit? = null
    var unitValue: BigDecimal? = null
    var unitPackageCount: Int? = null
    var valid: Boolean? = null

    override fun toString(): String {
        return "NewProductInfoDetail(id=$id, created=$created, updated=$updated, url=$url, name=$name, eshopUuid=$eshopUuid, unit=$unit, unitValue=$unitValue, unitPackageCount=$unitPackageCount, valid=$valid)"
    }


}