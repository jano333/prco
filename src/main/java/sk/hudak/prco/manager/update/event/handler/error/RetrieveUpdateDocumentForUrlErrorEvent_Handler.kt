package sk.hudak.prco.manager.update.event.handler.error

import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.ErrorType
import sk.hudak.prco.dto.ErrorCreateDto
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.events.PrcoObserver
import sk.hudak.prco.exception.HttpStatusParserException
import sk.hudak.prco.exception.ProductPageNotFoundHttpParserException
import sk.hudak.prco.manager.add.event.AddProductExecutors
import sk.hudak.prco.manager.update.event.RetrieveUpdateDocumentForUrlErrorEvent
import sk.hudak.prco.service.InternalTxService
import java.util.*
import java.util.concurrent.CompletionException

//TODO osobitne vo vlakne lebo sa tu odmazava z db produkt
@Component
class RetrieveUpdateDocumentForUrlErrorEvent_Handler(prcoObservable: PrcoObservable,
                                                     private val addProductExecutors: AddProductExecutors,
                                                     private val internalTxService: InternalTxService)
    : PrcoObserver {

    companion object {
        private val LOG = LoggerFactory.getLogger(RetrieveUpdateDocumentForUrlErrorEvent_Handler::class.java)!!
    }

    // registering itself as observer
    init {
        prcoObservable.addObserver(this)
    }

    override fun update(source: Observable?, errorEvent: CoreEvent) {
        if (errorEvent !is RetrieveUpdateDocumentForUrlErrorEvent) {
            return
        }
        when (errorEvent.error) {
            !is CompletionException -> {
                //TODO log nie je to CompletionException
                return
            }
            else -> {
                if (errorEvent.error.cause == null) {
                    //TODO log
                    return
                }
                when (errorEvent.error.cause) {
                    is ProductPageNotFoundHttpParserException -> {
                        LOG.warn("getting 404 for ${errorEvent.event.productDetailInfo.url}")
                        internalTxService.removeProduct(errorEvent.event.productDetailInfo.id)
                    }
                    is HttpStatusParserException -> {
                        internalTxService.createError(ErrorCreateDto(
                                eshopUuid = errorEvent.event.productDetailInfo.eshopUuid,
                                errorType = ErrorType.HTTP_STATUS_ERR,
                                statusCode = (errorEvent.error.cause as HttpStatusParserException).httpStatus.toString(),
                                url = errorEvent.event.productDetailInfo.url,
                                message = errorEvent.error.message,
                                fullMsg = ExceptionUtils.getStackTrace(errorEvent.error),
                                additionalInfo = errorEvent.event.toString()))
                    }
                    else -> {
                        internalTxService.createError(ErrorCreateDto(
                                eshopUuid = errorEvent.event.productDetailInfo.eshopUuid,
                                errorType = ErrorType.PARSING_PRODUCT_UPDATE_DATA,
                                url = errorEvent.event.productDetailInfo.url,
                                message = errorEvent.error.message,
                                fullMsg = ExceptionUtils.getStackTrace(errorEvent.error),
                                additionalInfo = errorEvent.event.toString()))
                    }
                }
            }
        }

    }


}