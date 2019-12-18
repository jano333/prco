package sk.hudak.prco.z.old

import sk.hudak.prco.dto.product.ProductDetailInfo

interface UpdateProductErrorHandler {
    fun processParsingError(error: Exception, productForUpdate: ProductDetailInfo): ContinueUpdateStatus
}
