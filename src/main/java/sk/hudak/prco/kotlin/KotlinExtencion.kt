package sk.hudak.prco.kotlin

import sk.hudak.prco.dto.ProductNewData
import sk.hudak.prco.dto.ProductUpdateData
import sk.hudak.prco.dto.ProductUpdateDataDto
import sk.hudak.prco.dto.product.NewProductCreateDto
//TODO move to mapping
fun ProductNewData.toNewProductCreateDto(): NewProductCreateDto {
    var dto = NewProductCreateDto()
    dto.eshopUuid = this.eshopUuid
    dto.url = this.url
    dto.name = this.name
    dto.unit = this.unit
    dto.unitValue = this.unitValue
    dto.unitPackageCount = this.unitPackageCount
    dto.pictureUrl = this.pictureUrl
    return dto
}

fun ProductUpdateData.toProductUpdateDataDto(productId: Long): ProductUpdateDataDto =
        ProductUpdateDataDto(
                productId,
                this.url,
                this.name,
                this.priceForPackage,
                this.productAction,
                this.actionValidity,
                this.pictureUrl)
