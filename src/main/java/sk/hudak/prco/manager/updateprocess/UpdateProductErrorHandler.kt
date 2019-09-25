package sk.hudak.prco.manager.updateprocess

import sk.hudak.prco.dto.product.ProductDetailInfo

interface UpdateProductErrorHandler {
    fun processParsingError(error: Exception, productForUpdate: ProductDetailInfo): ContinueUpdateStatus
}
