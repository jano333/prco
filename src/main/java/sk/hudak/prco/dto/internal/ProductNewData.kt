package sk.hudak.prco.dto.internal

import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.Unit
import java.math.BigDecimal

data class ProductNewData(
        var eshopUuid: EshopUuid? = null,
        var url: String? = null,
        var name: String? = null,
        var unit: Unit? = null,
        var unitValue: BigDecimal? = null,
        var unitPackageCount: Int? = null,
        var pictureUrl: String? = null) : InternalDto {
    //TODO poprehadzovat na optional tie ktore mozu byt null


    /**
     * @return true, ak sa podarilo vsetko uspesne vyparsovat(vsetky parametre), inak false
     */
    val isValid: Boolean
        get() {
            if (url == null || url!!.trim { it <= ' ' }.isEmpty()) {
                return false
            }
            if (name == null || name!!.trim { it <= ' ' }.isEmpty()) {
                return false
            }
            return eshopUuid != null && unit != null && unitValue != null && unitPackageCount != null
        }


}
