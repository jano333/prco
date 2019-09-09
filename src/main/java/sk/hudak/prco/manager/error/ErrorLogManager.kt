package sk.hudak.prco.manager.error

import org.apache.commons.lang3.exception.ExceptionUtils
import org.springframework.stereotype.Component
import sk.hudak.prco.api.ErrorType
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dto.ErrorCreateDto
import sk.hudak.prco.exception.HttpSocketTimeoutParserException
import sk.hudak.prco.exception.HttpStatusParserException
import sk.hudak.prco.service.InternalTxService

@Component
class ErrorLogManager(private val internalTxService: InternalTxService) {

    //FIXME prejst nazvy a ujednotit ich

    fun logErrorParsingProductUrls(eshopUuid: EshopUuid, searchKeyWord: String, e: Exception) {
        internalTxService.createError(ErrorCreateDto(
                eshopUuid,
                ErrorType.PARSING_PRODUCT_NEW_DATA, null,
                e.message,
                ExceptionUtils.getStackTrace(e), null,
                searchKeyWord))
    }

    fun logErrorParsingProductNameForNewProduct(eshopUuid: EshopUuid, productUrl: String) {
        internalTxService.createError(ErrorCreateDto(
                eshopUuid,
                ErrorType.PARSING_PRODUCT_NAME_FOR_NEW_PRODUCT, null, null, null,
                productUrl, null))
    }

    fun logErrorParsingProductNewData(eshopUuid: EshopUuid, e: Exception) {
        internalTxService.createError(ErrorCreateDto(
                eshopUuid,
                //TODO typ je nspravny...
                ErrorType.PARSING_PRODUCT_URLS, null,
                e.message,
                ExceptionUtils.getStackTrace(e), null,
                null))
    }

    fun saveInvalidHttpStatusError(eshopUuid: EshopUuid, url: String, message: String?, e: HttpStatusParserException) {
        internalTxService.createError(ErrorCreateDto(
                errorType = if (404 == e.httpStatus) ErrorType.HTTP_STATUS_404_ERR else ErrorType.HTTP_STATUS_ERR,
                eshopUuid = eshopUuid,
                url = url,
                message = message,
                statusCode = e.httpStatus.toString(),
                fullMsg = ExceptionUtils.getStackTrace(e))
        )
    }

    fun saveGenericParsingError(eshopUuid: EshopUuid, url: String, message: String?, e: Exception) {
        internalTxService.createError(ErrorCreateDto(
                errorType = ErrorType.PARSING_PRODUCT_INFO_ERR,
                eshopUuid = eshopUuid,
                url = url,
                message = message,
                fullMsg = ExceptionUtils.getStackTrace(e))
        )
    }

    fun saveTimeout4Error(eshopUuid: EshopUuid, url: String, message: String?, e: HttpSocketTimeoutParserException) {
        internalTxService.createError(ErrorCreateDto(
                errorType = ErrorType.TIME_OUT_ERR,
                eshopUuid = eshopUuid,
                url = url,
                message = message,
                fullMsg = ExceptionUtils.getStackTrace(e))
        )
    }


}