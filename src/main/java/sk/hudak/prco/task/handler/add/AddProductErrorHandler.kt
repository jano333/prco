package sk.hudak.prco.task.handler.add

import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.ErrorType
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dto.ErrorCreateDto
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.events.PrcoObserver
import sk.hudak.prco.service.InternalTxService
import sk.hudak.prco.task.add.*
import java.util.*

@Component
class AddProductErrorHandler(prcoObservable: PrcoObservable,
                             private val addProductExecutors: AddProductExecutors,
                             private val internalTxService: InternalTxService)
    : PrcoObserver {

    companion object {
        private val LOG = LoggerFactory.getLogger(AddProductErrorHandler::class.java)!!
    }

    // registering itself as observer
    init {
        prcoObservable.addObserver(this)
    }

    override fun update(source: Observable?, event: CoreEvent) {
        if (event !is BasicErrorEvent) {
            return
        }
        when (event) {
            is RetrieveKeywordBaseOnKeywordIdErrorEvent -> handle_RetrieveKeywordBaseOnKeywordIdErrorEvent(event)
            is BuildSearchUrlForKeywordErrorEvent -> handle_BuildSearchUrlForKeywordErrorEvent(event)
            is RetrieveDocumentForSearchUrlErrorEvent -> handle_RetrieveDocumentForSearchUrlErrorEvent(event)
            is ParseCountOfPagesErrorEvent -> handle_ParseCountOfPagesErrorEvent(event)
            is ParseProductListURLsErrorEvent -> handle_ParseProductListURLsErrorEvent(event)
            is FilterDuplicityErrorEvent -> handle_FilterDuplicityErrorEvent(event)
            is FilterNotExistingErrorEvent -> handle_FilterNotExistingErrorEvent(event)
            is RetrieveDocumentForUrlErrorEvent -> handle_RetrieveDocumentForUrlErrorEvent(event)
            is ParseProductNewDataErrorEvent -> handle_ParseProductNewDataErrorEvent(event)
            is SaveProductNewDataErrorEvent -> handle_SaveProductNewDataErrorEvent(event)
            is BuildNextPageSearchUrlErrorEvent -> handle_BuildNextPageSearchUrlErrorEvent(event)
            is FilterNotExistingProductErrorEvent -> handle_FilterNotExistingProductErrorEvent(event)
            is ParseEshopUuidErrorEvent -> handle_ParseEshopUuidErrorEvent(event)
            else -> {
                //TODO
                logErrorEvent(event as BasicErrorEvent)
            }
        }
    }

    private fun handle_ParseEshopUuidErrorEvent(errorEvent: ParseEshopUuidErrorEvent) {
        logErrorEvent(errorEvent)
        internalTxService.createError(ErrorCreateDto(
                //TODO eshop je poviiny ale tu ho nemame...
                EshopUuid.OBI,
                ErrorType.UNKNOWN, null,
                errorEvent.error.message,
                ExceptionUtils.getStackTrace(errorEvent.error),
                errorEvent.event.productUrl,
                errorEvent.event.toString()))
    }

    private fun handle_RetrieveKeywordBaseOnKeywordIdErrorEvent(errorEvent: RetrieveKeywordBaseOnKeywordIdErrorEvent) {
        logErrorEvent(errorEvent)
        addProductExecutors.shutdownNowAllExecutors()
    }

    private fun handle_BuildSearchUrlForKeywordErrorEvent(errorEvent: BuildSearchUrlForKeywordErrorEvent) {
        logErrorEvent(errorEvent)
        addProductExecutors.shutdownNowAllExecutors()
    }

    private fun logErrorEvent(errorEvent: BasicErrorEvent) {
        LOG.error("error while processing event ${errorEvent.event.javaClass.simpleName}")
        LOG.error("source event ${errorEvent.event}")
        LOG.error("${errorEvent.error.message}", errorEvent.error)
    }

    private fun handle_FilterNotExistingProductErrorEvent(errorEvent: FilterNotExistingProductErrorEvent) {
        logErrorEvent(errorEvent)
        internalTxService.createError(ErrorCreateDto(
                //TODO eshop je poviiny ale tu ho nemame...
                EshopUuid.OBI,
                ErrorType.UNKNOWN, null,
                errorEvent.error.message,
                ExceptionUtils.getStackTrace(errorEvent.error),
                null,
                errorEvent.event.toString()))
    }

    private fun handle_BuildNextPageSearchUrlErrorEvent(errorEvent: BuildNextPageSearchUrlErrorEvent) {
        logErrorEvent(errorEvent)
        internalTxService.createError(ErrorCreateDto(
                errorEvent.event.eshopUuid,
                ErrorType.UNKNOWN, null,
                errorEvent.error.message,
                ExceptionUtils.getStackTrace(errorEvent.error),
                null,
                errorEvent.event.toString()))
    }

    private fun handle_RetrieveDocumentForSearchUrlErrorEvent(errorEvent: RetrieveDocumentForSearchUrlErrorEvent) {
        logErrorEvent(errorEvent)
        internalTxService.createError(ErrorCreateDto(
                errorEvent.event.eshopUuid,
                ErrorType.PARSING_PRODUCT_NEW_DATA, null,
                errorEvent.error.message,
                ExceptionUtils.getStackTrace(errorEvent.error),
                errorEvent.event.searchUrl,
                errorEvent.event.toString()))
    }

    private fun handle_SaveProductNewDataErrorEvent(errorEvent: SaveProductNewDataErrorEvent) {
        logErrorEvent(errorEvent)
        internalTxService.createError(ErrorCreateDto(
                errorEvent.event.productNewData.eshopUuid,
                ErrorType.UNKNOWN, null,
                errorEvent.error.message,
                ExceptionUtils.getStackTrace(errorEvent.error),
                null,
                errorEvent.event.toString()))
    }

    private fun handle_RetrieveDocumentForUrlErrorEvent(errorEvent: RetrieveDocumentForUrlErrorEvent) {
        logErrorEvent(errorEvent)
        internalTxService.createError(ErrorCreateDto(
                errorEvent.event.eshopUuid,
                ErrorType.UNKNOWN, null,
                errorEvent.error.message,
                ExceptionUtils.getStackTrace(errorEvent.error),
                null,
                errorEvent.event.toString()))
    }

    private fun handle_ParseProductNewDataErrorEvent(errorEvent: ParseProductNewDataErrorEvent) {
        logErrorEvent(errorEvent)

        internalTxService.createError(ErrorCreateDto(
                errorEvent.event.eshopUuid,
                ErrorType.PARSING_PRODUCT_NEW_DATA, null,
                errorEvent.error.message,
                ExceptionUtils.getStackTrace(errorEvent.error),
                errorEvent.event.newProductUrl,
                errorEvent.event.toString()))
    }

    private fun handle_FilterNotExistingErrorEvent(errorEvent: FilterNotExistingErrorEvent) {
        logErrorEvent(errorEvent)

        internalTxService.createError(ErrorCreateDto(
                errorEvent.event.eshopUuid,
                ErrorType.UNKNOWN, null,
                errorEvent.error.message,
                ExceptionUtils.getStackTrace(errorEvent.error),
                null,
                errorEvent.event.toString()))
    }

    private fun handle_FilterDuplicityErrorEvent(errorEvent: FilterDuplicityErrorEvent) {
        logErrorEvent(errorEvent)
        internalTxService.createError(ErrorCreateDto(
                errorEvent.event.eshopUuid,
                ErrorType.UNKNOWN, null,
                errorEvent.error.message,
                ExceptionUtils.getStackTrace(errorEvent.error),
                null,
                errorEvent.event.toString()))
    }

    private fun handle_ParseCountOfPagesErrorEvent(errorEvent: ParseCountOfPagesErrorEvent) {
        logErrorEvent(errorEvent)
        internalTxService.createError(ErrorCreateDto(
                errorEvent.event.eshopUuid,
                ErrorType.UNKNOWN, null,
                errorEvent.error.message,
                ExceptionUtils.getStackTrace(errorEvent.error),
                null,
                errorEvent.event.toString()))
    }

    private fun handle_ParseProductListURLsErrorEvent(errorEvent: ParseProductListURLsErrorEvent) {
        logErrorEvent(errorEvent)
        internalTxService.createError(ErrorCreateDto(
                errorEvent.event.eshopUuid,
                ErrorType.UNKNOWN, null,
                errorEvent.error.message,
                ExceptionUtils.getStackTrace(errorEvent.error),
                null,
                errorEvent.event.toString()))
    }

}