package sk.hudak.prco.dto

import sk.hudak.prco.api.EshopUuid

data class EshopProductInfoDto(
        val countOfAllProduct: Long,
        val countOfAlreadyUpdated: Long) : DtoAble

class ProductStatisticInfoDto {
    var countOfNewProducts: Long = 0
    var countOfInterestedProducts: Long = 0
    var countOfNotInterestedProducts: Long = 0
    val countOfAllProducts: Long
        get() {
            return countOfNewProducts.plus(countOfInterestedProducts).plus(countOfNotInterestedProducts)
        }

    var countOfProductsNotInAnyGroup: Long = -1

    // key is group name, value is count of products
    var countProductInGroup: Map<String, Long>? = null

    var eshopProductInfo: Map<EshopUuid, EshopProductInfoDto>? = null

    override fun toString(): String {
        return "ProductStatisticInfoDto{\n" +
                "countOfNewProducts=$countOfNewProducts\n" +
                "countOfInterestedProducts=$countOfInterestedProducts\n" +
                "countOfNotInterestedProducts=$countOfNotInterestedProducts\n" +
                "countOfAllProducts=$countOfAllProducts\n" +
                "countOfProductsNotInAnyGroup=$countOfProductsNotInAnyGroup\n" +
                "countProductInGroup=$countProductInGroup\n" +
                "eshopProductInfo=$eshopProductInfo\n}"
    }
}