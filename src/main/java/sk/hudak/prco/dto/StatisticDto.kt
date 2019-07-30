package sk.hudak.prco.dto

import sk.hudak.prco.api.EshopUuid

class EshopProductInfoDto(
        val countOfAllProduct: Long,
        val countOfAlreadyUpdated: Long) : DtoAble {

    override fun toString(): String {
        return "EshopProductInfoDto{" +
                "countOfAllProduct=" + countOfAllProduct +
                ", countOfAlreadyUpdated=" + countOfAlreadyUpdated +
                '}'.toString()
    }
}

class ProductStatisticInfoDto {

    // only interested in
    var countOfAllProducts: Long = -1

    var countOfProductsNotInAnyGroup: Long = -1

    // key is group name, value is count of products
    var countProductInGroup: Map<String, Long>? = null

    var eshopProductInfo: Map<EshopUuid, EshopProductInfoDto>? = null

    override fun toString(): String {
        return "ProductStatisticInfoDto{" +
                "countOfAllProducts=" + countOfAllProducts +
                ", countOfProductsNotInAnyGroup=" + countOfProductsNotInAnyGroup +
                ", countProductInGroup=" + countProductInGroup +
                ", eshopProductInfo=" + eshopProductInfo +
                '}'.toString()
    }
}