package sk.hudak.prco.dto

import sk.hudak.prco.api.EshopUuid
import java.math.BigDecimal

class WatchDogAddCustomDto {

    var productUrl: String? = null
    var maxPriceToBeInterestedIn: BigDecimal? = null

    constructor() {}

    constructor(productUrl: String, maxPriceToBeInterestedIn: BigDecimal) {
        this.productUrl = productUrl
        this.maxPriceToBeInterestedIn = maxPriceToBeInterestedIn
    }

    override fun toString(): String {
        return "WatchDogAddDto{" +
                "productUrl='" + productUrl + '\''.toString() +
                ", maxPriceToBeInterestedIn=" + maxPriceToBeInterestedIn +
                '}'.toString()
    }
}

class WatchDogDto {

    var id: Long? = null
    var productUrl: String? = null
    var eshopUuid: EshopUuid? = null
    var maxPriceToBeInterestedIn: BigDecimal? = null

    override fun toString(): String {
        return "WatchDogDto{" +
                "id=" + id +
                ", productUrl='" + productUrl + '\''.toString() +
                ", eshopUuid=" + eshopUuid +
                ", maxPriceToBeInterestedIn=" + maxPriceToBeInterestedIn +
                '}'.toString()
    }
}

class WatchDogNotifyUpdateDto {
    var id: Long? = null
    var productUrl: String? = null
    var productName: String? = null
    var eshopUuid: EshopUuid? = null
    var maxPriceToBeInterestedIn: BigDecimal? = null

    var currentPrice: BigDecimal? = null

    override fun toString(): String {
        return "WatchDogNotifyUpdateDto{" +
                "id=" + id +
                ", productUrl='" + productUrl + '\''.toString() +
                ", productName='" + productName + '\''.toString() +
                ", eshopUuid=" + eshopUuid +
                ", maxPriceToBeInterestedIn=" + maxPriceToBeInterestedIn +
                ", currentPrice=" + currentPrice +
                '}'.toString()
    }
}