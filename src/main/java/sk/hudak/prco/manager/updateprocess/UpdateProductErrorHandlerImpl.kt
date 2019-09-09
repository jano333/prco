package sk.hudak.prco.manager.updateprocess

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.dto.product.ProductDetailInfo
import sk.hudak.prco.exception.HttpSocketTimeoutParserException
import sk.hudak.prco.exception.HttpStatusParserException
import sk.hudak.prco.manager.error.ErrorLogManager
import sk.hudak.prco.service.InternalTxService

@Component
class UpdateProductErrorHandlerImpl(private val internalTxService: InternalTxService,
                                    private val errorLogManager: ErrorLogManager)
    : UpdateProductErrorHandler {

    companion object {
        val log = LoggerFactory.getLogger(UpdateProductErrorHandlerImpl::class.java)!!
    }

    override fun processParsingError(error: Exception, productForUpdate: ProductDetailInfo) {
        log.error("error while parsing product update data for product ${productForUpdate.url}", error)

        when (error) {
            is HttpStatusParserException -> {
                handleHttpStatusError(error, productForUpdate)
            }
            is HttpSocketTimeoutParserException -> {
                handleHttpSocketTimeoutError(error, productForUpdate)
            }
            else -> {
                errorLogManager.saveGenericParsingError(productForUpdate.eshopUuid, productForUpdate.url, error.message, error)
            }
        }
    }

    private fun handleHttpStatusError(e: HttpStatusParserException, productDetailInfo: ProductDetailInfo) {
        log.error("http status: ${e.httpStatus}")
        if (404 == e.httpStatus) {
            internalTxService.removeProduct(productDetailInfo.id)
        }
        errorLogManager.saveInvalidHttpStatusError(productDetailInfo.eshopUuid, productDetailInfo.url, e.message, e)
    }

    private fun handleHttpSocketTimeoutError(error: HttpSocketTimeoutParserException, productDetailInfo: ProductDetailInfo) {
        errorLogManager.saveTimeout4Error(productDetailInfo.eshopUuid, productDetailInfo.url, error.message, error)
    }


}
