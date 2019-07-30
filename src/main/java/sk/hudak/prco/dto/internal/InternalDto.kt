package sk.hudak.prco.dto.internal

import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.ProductAction
import sk.hudak.prco.api.Unit
import java.math.BigDecimal
import java.util.*

interface InternalMarkerDto

data class ProductNewData(
        var eshopUuid: EshopUuid? = null,
        var url: String? = null,
        var name: String? = null,
        var unit: Unit? = null,
        var unitValue: BigDecimal? = null,
        var unitPackageCount: Int? = null,
        var pictureUrl: String? = null) : InternalMarkerDto {
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

class ProductUpdateData : InternalMarkerDto {

    val url: String
    val eshopUuid: EshopUuid

    var name: String? = null
    var priceForPackage: BigDecimal? = null
    var productAction: ProductAction? = null
    var actionValidity: Date? = null
    var pictureUrl: String? = null
    val isProductAvailable: Boolean
        get() = name != null && priceForPackage != null

    constructor(url: String, eshopUuid: EshopUuid) {
        this.url = url
        this.eshopUuid = eshopUuid
    }


    constructor(url: String, eshopUuid: EshopUuid,
                name: String?, priceForPackage: BigDecimal?, productAction: ProductAction?, actionValidity: Date, pictureUrl: String) {
        // povinne
        this.url = url
        this.eshopUuid = eshopUuid
        // optional
        this.name = name
        this.priceForPackage = priceForPackage
        this.productAction = productAction
        this.actionValidity = actionValidity
        this.pictureUrl = pictureUrl
    }

    override fun toString(): String {
        return "ProductUpdateData(url=$url, " +
                "eshopUuid=$eshopUuid, " +
                "name=$name, " +
                "priceForPackage=$priceForPackage, " +
                "productAction=$productAction, " +
                "actionValidity=$actionValidity, " +
                "pictureUrl=$pictureUrl)"
    }


}

data class StatisticForUpdateForEshopDto(
        val eshopUuid: EshopUuid,
        val countOfProductsWaitingToBeUpdated: Long,
        val countOfProductsAlreadyUpdated: Long) : InternalMarkerDto

class ParsingDataResponse : InternalMarkerDto {

    var productUpdateData: ProductUpdateData? = null
    var error: Exception? = null

    val isError: Boolean
        get() = error != null

    constructor(productUpdateData: ProductUpdateData) {
        this.productUpdateData = productUpdateData
    }

    constructor(error: Exception) {
        this.error = error
    }

}