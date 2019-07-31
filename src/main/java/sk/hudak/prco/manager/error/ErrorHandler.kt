package sk.hudak.prco.manager.error

import sk.hudak.prco.dto.product.ProductDetailInfo
import sk.hudak.prco.manager.updateprocess.UpdateProcessResult

interface ErrorHandler {
    //FIXME premenovat
    fun processParsingError(error: Exception, productDetailInfo: ProductDetailInfo): UpdateProcessResult
}
