package sk.hudak.prco.dto.newproduct

import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.Unit
import sk.hudak.prco.dto.DtoAble

import java.math.BigDecimal

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
