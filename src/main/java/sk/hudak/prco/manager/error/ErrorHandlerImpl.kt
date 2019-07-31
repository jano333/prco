package sk.hudak.prco.manager.error

import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.ErrorType.*
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dto.ErrorCreateDto
import sk.hudak.prco.dto.product.ProductDetailInfo
import sk.hudak.prco.exception.HttpSocketTimeoutPrcoRuntimeException
import sk.hudak.prco.exception.HttpStatusErrorPrcoException
import sk.hudak.prco.manager.updateprocess.UpdateProcessResult
import sk.hudak.prco.manager.updateprocess.UpdateProcessResult.*
import sk.hudak.prco.service.InternalTxService

@Component
class ErrorHandlerImpl(val internalTxService: InternalTxService) : ErrorHandler {

    companion object {
        val log = LoggerFactory.getLogger(ErrorHandlerImpl::class.java)
    }

    override fun processParsingError(error: Exception, productDetailInfo: ProductDetailInfo): UpdateProcessResult {
        log.error("error while parsing product data for product $productDetailInfo.url", error)

        //FIXME skusit cez when

        if (error is HttpStatusErrorPrcoException) {
            return handleHttpErrorPrcoRuntimeException(error, productDetailInfo)
        }

        if (error is HttpSocketTimeoutPrcoRuntimeException) {
            return handleHttpSocketTimeoutPrcoRuntimeException(error, productDetailInfo)
        }

        saveGenericParsingError(productDetailInfo.eshopUuid!!, productDetailInfo.url!!, error.message, error)
        return ERR_PARSING_ERROR_GENERIC
    }

    private fun handleHttpErrorPrcoRuntimeException(e: HttpStatusErrorPrcoException, productDetailInfo: ProductDetailInfo): UpdateProcessResult {
        log.error("http status: " + e.httpStatus)
        if (404 == e.httpStatus) {
            internalTxService.removeProduct(productDetailInfo.id)
            return ERR_PARSING_ERROR_HTTP_STATUS_404
        }
        saveInvalidHttpStatusError(productDetailInfo.eshopUuid!!, productDetailInfo.url!!, e.message, e)
        return ERR_PARSING_ERROR_HTTP_STATUS_INVALID
    }

    private fun handleHttpSocketTimeoutPrcoRuntimeException(error: HttpSocketTimeoutPrcoRuntimeException, productDetailInfo: ProductDetailInfo): UpdateProcessResult {
        saveTimeout4Error(productDetailInfo.eshopUuid!!, productDetailInfo.url!!, error.message, error)
        return ERR_PARSING_ERROR_HTTP_TIMEOUT
    }

    private fun saveInvalidHttpStatusError(eshopUuid: EshopUuid, url: String, message: String?, e: HttpStatusErrorPrcoException) {
        internalTxService.createError(ErrorCreateDto(
                errorType = if (404 == e.httpStatus) HTTP_STATUS_404_ERR else HTTP_STATUS_ERR,
                eshopUuid = eshopUuid,
                url = url,
                message = message,
                statusCode = e.httpStatus.toString(),
                fullMsg = ExceptionUtils.getStackTrace(e))
        )
    }

    private fun saveGenericParsingError(eshopUuid: EshopUuid, url: String, message: String?, e: Exception) {
        internalTxService.createError(ErrorCreateDto(
                errorType = PARSING_PRODUCT_INFO_ERR,
                eshopUuid = eshopUuid,
                url = url,
                message = message,
                fullMsg = ExceptionUtils.getStackTrace(e))
        )
    }

    private fun saveTimeout4Error(eshopUuid: EshopUuid, url: String, message: String?, e: HttpSocketTimeoutPrcoRuntimeException) {
        internalTxService.createError(ErrorCreateDto(
                errorType = TIME_OUT_ERR,
                eshopUuid = eshopUuid,
                url = url,
                message = message,
                fullMsg = ExceptionUtils.getStackTrace(e))
        )
    }
}
