package sk.hudak.prco.manager

import sk.hudak.prco.dto.product.ProductDetailInfo
import sk.hudak.prco.manager.impl.UpdateProcessResult

interface ErrorHandler {
    //FIXME premenovat
    fun processParsingError(error: Exception, productDetailInfo: ProductDetailInfo): UpdateProcessResult
}
