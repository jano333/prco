package sk.hudak.prco.dto

import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.kotlin.color
import sk.hudak.prco.utils.ConsoleColor

data class EshopProductInfoDto(
        val countOfNew: Long,
        val countOfInterested: Long,
        val countOfAlreadyUpdated: Long,
        val countOfNotInterested: Long,
        val countOfAllProduct: Long,
        val countOfProductMarkedAsUnavailable: Long
) : DtoAble {

    override fun toString(): String {
        val toStringResult = StringBuilder();
        toStringResult.append("EshopProductInfoDto(")

        toStringResult.append("countOfInterested=")
        if (countOfInterested == 0L) {
            toStringResult.append("$countOfInterested".color(ConsoleColor.RED))
        } else {
            toStringResult.append("$countOfInterested".color(ConsoleColor.GREEN))
        }
        toStringResult.append(", countOfAlreadyUpdated=")
        when {
            countOfAlreadyUpdated == 0L -> toStringResult.append("$countOfAlreadyUpdated".color(ConsoleColor.RED))
            (countOfInterested == countOfAlreadyUpdated) -> toStringResult.append("$countOfAlreadyUpdated".color(ConsoleColor.GREEN))
            else -> toStringResult.append("$countOfAlreadyUpdated".color(ConsoleColor.YELLOW))
        }
        toStringResult.append(", countOfProductMarkedAsUnavailable=")
        if (countOfProductMarkedAsUnavailable == 0L) {
            toStringResult.append("$countOfProductMarkedAsUnavailable".color(ConsoleColor.GREEN))
        } else {
            toStringResult.append("$countOfProductMarkedAsUnavailable".color(ConsoleColor.YELLOW))
        }
        toStringResult.append(", countOfNew=").append("$countOfNotInterested")
        toStringResult.append(", countOfAllProduct=").append("$countOfAllProduct".color(ConsoleColor.BLUE))

        toStringResult.append(")")
        return toStringResult.toString()
    }
}

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
        var s = "ProductStatisticInfoDto{\n" +
                "countOfNewProducts=$countOfNewProducts\n" +
                "countOfInterestedProducts=$countOfInterestedProducts\n" +
                "countOfNotInterestedProducts=$countOfNotInterestedProducts\n" +
                "countOfAllProducts=$countOfAllProducts\n" +
                "countOfProductsNotInAnyGroup=$countOfProductsNotInAnyGroup\n" +
                "countProductInGroup=$countProductInGroup\n" +
                "eshopProductInfo=\n"
        if (eshopProductInfo != null) {
            eshopProductInfo!!.forEach { (eshopUuid, dto) ->
                s = "$s$eshopUuid=$dto\n"
            }
        }

        return s +


                "}"
    }
}