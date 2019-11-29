package sk.hudak.prco.task.ng

import sk.hudak.prco.dto.ProductNewData
import sk.hudak.prco.dto.product.NewProductCreateDto

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