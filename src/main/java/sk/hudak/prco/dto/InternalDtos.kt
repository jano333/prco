package sk.hudak.prco.dto

import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.ProductAction
import sk.hudak.prco.api.Unit
import java.math.BigDecimal
import java.util.*

interface InternalMarkerDto

data class ProductNewData(val eshopUuid: EshopUuid,
                          val url: String,
        // nepovinne:
                          var name: String? = null,
                          var pictureUrl: String? = null,
                          var unit: Unit? = null,
                          var unitValue: BigDecimal? = null,
                          var unitPackageCount: Int? = null) : InternalMarkerDto {

    /**
     * @return true, ak sa podarilo vsetko uspesne vyparsovat(vsetky parametre), inak false
     */
    val isValid: Boolean
        get() {
            if (url.trim { it <= ' ' }.isEmpty()) {
                return false
            }
            if (name == null || name!!.trim { it <= ' ' }.isEmpty()) {
                return false
            }
            return unit != null && unitValue != null && unitPackageCount != null
        }
}

// TODO porozmyslat ci toto nema ist do package pre update manager.... nech je to biznisovo spolu vsetko co sa tyka update
// TODO prerobit nech konstruktor je z 2 parametrami povinny !!!
class ProductUpdateData : InternalMarkerDto {

    val url: String
    val eshopUuid: EshopUuid
    val redirect: Boolean
    val isProductAvailable: Boolean

    var name: String? = null
    var priceForPackage: BigDecimal? = null
    var productAction: ProductAction? = null
    var actionValidity: Date? = null
    var pictureUrl: String? = null

    companion object {
        fun createUnavailable(url: String, eshopUuid: EshopUuid, redirect: Boolean): ProductUpdateData {
            return ProductUpdateData(url, eshopUuid, redirect, null, null, null,
                    null, null, true)
        }

        fun createAvailable(url: String, eshopUuid: EshopUuid, redirect: Boolean,
                            name: String?, pictureUrl: String?, priceForPackage: BigDecimal?,
                            productAction: ProductAction?, actionValidity: Date?): ProductUpdateData {
            return ProductUpdateData(url, eshopUuid, redirect, name, priceForPackage, productAction,
                    actionValidity, pictureUrl, true)
        }

    }

    private constructor(url: String, eshopUuid: EshopUuid, redirect: Boolean, name: String?, priceForPackage: BigDecimal?,
                        productAction: ProductAction?, actionValidity: Date?, pictureUrl: String?, isProductAvailable: Boolean) {
        // povinne
        this.url = url
        this.eshopUuid = eshopUuid
        this.redirect = redirect
        // optional
        this.name = name
        this.priceForPackage = priceForPackage
        this.productAction = productAction
        this.actionValidity = actionValidity
        this.pictureUrl = pictureUrl
        this.isProductAvailable = isProductAvailable

    }

    override fun toString(): String {
        return "ProductUpdateData(url='$url', " +
                "eshopUuid=$eshopUuid, " +
                "redirect=$redirect," +
                " name=$name, " +
                "priceForPackage=$priceForPackage, " +
                "productAction=$productAction, " +
                "actionValidity=$actionValidity, " +
                "pictureUrl=$pictureUrl, " +
                "isProductAvailable=$isProductAvailable)"
    }


}

data class StatisticForUpdateForEshopDto(
        val eshopUuid: EshopUuid,
        val countOfProductsWaitingToBeUpdated: Long,
        val countOfProductsAlreadyUpdated: Long) : InternalMarkerDto

data class ProductUpdateDataDto(var id: Long?,
                                var url: String?,
                                var name: String?,
                                var priceForPackage: BigDecimal?,
                                var productAction: ProductAction?,
                                var actionValidity: Date?,
                                var pictureUrl: String?)