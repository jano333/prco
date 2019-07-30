package sk.hudak.prco.dto

import java.math.BigDecimal

class WatchDogAddDto {

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