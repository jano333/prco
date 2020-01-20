package sk.hudak.prco.dto.product

import sk.hudak.prco.api.BestPriceInGroup
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.ProductAction
import sk.hudak.prco.api.Unit
import sk.hudak.prco.dto.DtoAble
import sk.hudak.prco.dto.GroupIdNameDto
import java.math.BigDecimal
import java.util.*

class ProductAddingToGroupDto : DtoAble {
    var id: Long? = null
    var url: String? = null
    var name: String? = null
    var eshopUuid: EshopUuid? = null
    var productPictureUrl: String? = null
    var groupId: Long? = null

    constructor()

    constructor(id: Long?, url: String, name: String, eshopUuid: EshopUuid, productPictureUrl: String, groupId: Long?) {
        this.id = id
        this.url = url
        this.name = name
        this.eshopUuid = eshopUuid
        this.productPictureUrl = productPictureUrl
        this.groupId = groupId
    }

    override fun toString(): String {
        return "ProductAddingToGroupDto{" +
                "id=" + id +
                ", url='" + url + '\''.toString() +
                ", name='" + name + '\''.toString() +
                ", eshopUuid=" + eshopUuid +
                ", productPictureUrl='" + productPictureUrl + '\''.toString() +
                ", groupId=" + groupId +
                '}'.toString()
    }
}

class ProductBestPriceInGroupDto : DtoAble {

    var id: Long? = null
    var url: String? = null
    var name: String? = null
    var eshopUuid: EshopUuid? = null
    // prices
    var priceForPackage: BigDecimal? = null
    var priceForOneItemInPackage: BigDecimal? = null
    var priceForUnit: BigDecimal? = null
    var commonPrice: BigDecimal? = null
    // action info
    var productAction: ProductAction? = null
    var actionValidTo: Date? = null
    var actionInPercentage: Int = 0

    override fun toString(): String {
        return "ProductBestPriceInGroupDto{" +
                "id=" + id +
                ", url='" + url + '\''.toString() +
                ", name='" + name + '\''.toString() +
                ", eshopUuid=" + eshopUuid +
                ", priceForPackage=" + priceForPackage +
                ", priceForOneItemInPackage=" + priceForOneItemInPackage +
                ", priceForUnit=" + priceForUnit +
                ", commonPrice=" + commonPrice +
                ", productAction=" + productAction +
                ", actionValidTo=" + actionValidTo +
                ", actionInPercentage=" + actionInPercentage +
                '}'.toString()
    }
}
//FIXME premenovat na ProductData??? nieco lepsie dat ...
data class ProductDetailInfo(val id: Long,
                             val url: String,
                             val eshopUuid: EshopUuid)

//TODO data class
class ProductFilterUIDto : DtoAble {

    enum class ORDER_BY {
        NAME, PRICE_FOR_UNIT
    }

    var eshopUuid: EshopUuid? = null
    var onlyInAction: Boolean? = null
    var orderBy: ORDER_BY = ORDER_BY.PRICE_FOR_UNIT

    companion object {
        val EMPTY: ProductFilterUIDto = ProductFilterUIDto()
        fun withEshopOnly(eshopUuid: EshopUuid): ProductFilterUIDto = ProductFilterUIDto(eshopUuid)
        fun withActionOnly(): ProductFilterUIDto = ProductFilterUIDto(true)
    }

    constructor()

    private constructor(onlyInAction: Boolean?) {
        this.onlyInAction = onlyInAction
    }

    constructor(eshopUuid: EshopUuid) {
        this.eshopUuid = eshopUuid
    }

    constructor(eshopUuid: EshopUuid, onlyInAction: Boolean?) {
        this.eshopUuid = eshopUuid
        this.onlyInAction = onlyInAction
    }

    fun orderBy(orderBy: ORDER_BY): ProductFilterUIDto {
        this.orderBy = orderBy
        return this
    }

    override fun toString(): String {
        return "ProductFilterUIDto{" +
                "eshopUuid=" + eshopUuid +
                ", onlyInAction=" + onlyInAction +
                '}'.toString()
    }
}

open class ProductFullDto : DtoAble {

    var id: Long? = null
    var created: Date? = null
    var updated: Date? = null

    var url: String? = null
    var name: String? = null
    var eshopUuid: EshopUuid? = null
    var unit: Unit? = null
    var unitValue: BigDecimal? = null
    var unitPackageCount: Int? = null
    var priceForPackage: BigDecimal? = null
    var priceForOneItemInPackage: BigDecimal? = null
    var priceForUnit: BigDecimal? = null
    var commonPrice: BigDecimal? = null
    var lastTimeDataUpdated: Date? = null
    var productAction: ProductAction? = null
    var actionValidTo: Date? = null
    var productPictureUrl: String? = null
    var groupList: List<GroupIdNameDto> = ArrayList(1)

    override fun toString(): String {
        return "ProductFullDto{" +
                "id=" + id +
                ", created=" + created +
                ", updated=" + updated +
                ", url='" + url + '\''.toString() +
                ", name='" + name + '\''.toString() +
                ", eshopUuid=" + eshopUuid +
                ", unit=" + unit +
                ", unitValue=" + unitValue +
                ", unitPackageCount=" + unitPackageCount +
                ", priceForPackage=" + priceForPackage +
                ", priceForOneItemInPackage=" + priceForOneItemInPackage +
                ", priceForUnit=" + priceForUnit +
                ", commonPrice=" + commonPrice +
                ", lastTimeDataUpdated=" + lastTimeDataUpdated +
                ", productAction=" + productAction +
                ", actionValidTo=" + actionValidTo +
                ", productPictureUrl='" + productPictureUrl + '\''.toString() +
                ", groupList=" + groupList +
                '}'.toString()
    }
}

class ProductInActionDto : DtoAble {

    var id: Long? = null
    var url: String? = null
    var name: String? = null
    var eshopUuid: EshopUuid? = null
    var priceForPackage: BigDecimal? = null
    var priceForOneItemInPackage: BigDecimal? = null
    var commonPrice: BigDecimal? = null

    // action info
    var productAction: ProductAction? = null
    var actionValidTo: Date? = null

    //TODO
    var actionInPercentage: Int = 0 // aka je akcia
    var bestPriceInGroup: BestPriceInGroup? = null

    override fun toString(): String {
        return "ProductInActionDto{" +
                "id=" + id +
                ", url='" + url + '\''.toString() +
                ", name='" + name + '\''.toString() +
                ", eshopUuid=" + eshopUuid +
                ", priceForPackage=" + priceForPackage +
                ", priceForOneItemInPackage=" + priceForOneItemInPackage +
                ", commonPrice=" + commonPrice +
                ", productAction=" + productAction +
                ", actionValidTo=" + actionValidTo +
                ", actionInPercentage=" + actionInPercentage +
                ", bestPriceInGroup=" + bestPriceInGroup +
                '}'.toString()
    }
}

data class ProductUnitDataDto(
        // read only
        var id: Long? = null,
        var name: String? = null,
        // edit:
        var unit: String? = null,
        var unitValue: BigDecimal? = null,
        var unitPackageCount: Int? = null) : DtoAble

class ProductNotInAnyGroupDto : ProductFullDto() {
    var groupName: String? = null
    var groupId: Long? = null
}