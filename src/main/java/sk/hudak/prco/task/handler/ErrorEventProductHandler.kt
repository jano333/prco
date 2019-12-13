package sk.hudak.prco.task.handler

import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.ErrorType
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dto.ErrorCreateDto
import sk.hudak.prco.events.BasicErrorEvent
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.events.PrcoObserver
import sk.hudak.prco.service.InternalTxService
import sk.hudak.prco.task.add.*
import java.util.*

@Component
class ErrorEventProductHandler(prcoObservable: PrcoObservable,
                               private val addProductExecutors: AddProductExecutors,
                               private val internalTxService: InternalTxService)
    : PrcoObserver {

    companion object {
        private val LOG = LoggerFactory.getLogger(ErrorEventProductHandler::class.java)!!
    }

    // registering itself as observer
    init {
        prcoObservable.addObserver(this)
    }

    override fun update(source: Observable?, event: CoreEvent) {
        if (event !is BasicErrorEvent) {
            return
        }
        // create error log
        logErrorEvent(event)

        // process specifig errors if needed...
        when (event) {
            // add process
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

            // update process
        }
    }

    private fun logErrorEvent(errorEvent: BasicErrorEvent) {
        LOG.error("error while processing event ${errorEvent.event.javaClass.simpleName}")
        LOG.error("source event ${errorEvent.event}")
        LOG.error("${errorEvent.error.message}", errorEvent.error)
    }

    private fun handle_ParseEshopUuidErrorEvent(errorEvent: ParseEshopUuidErrorEvent) {
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
        addProductExecutors.shutdownNowAllExecutors()
    }

    private fun handle_BuildSearchUrlForKeywordErrorEvent(errorEvent: BuildSearchUrlForKeywordErrorEvent) {
        addProductExecutors.shutdownNowAllExecutors()
    }

    private fun handle_FilterNotExistingProductErrorEvent(errorEvent: FilterNotExistingProductErrorEvent) {
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
        internalTxService.createError(ErrorCreateDto(
                errorEvent.event.eshopUuid,
                ErrorType.UNKNOWN, null,
                errorEvent.error.message,
                ExceptionUtils.getStackTrace(errorEvent.error),
                null,
                errorEvent.event.toString()))
    }

    private fun handle_RetrieveDocumentForSearchUrlErrorEvent(errorEvent: RetrieveDocumentForSearchUrlErrorEvent) {
        internalTxService.createError(ErrorCreateDto(
                errorEvent.event.eshopUuid,
                ErrorType.PARSING_PRODUCT_NEW_DATA, null,
                errorEvent.error.message,
                ExceptionUtils.getStackTrace(errorEvent.error),
                errorEvent.event.searchUrl,
                errorEvent.event.toString()))
    }

    private fun handle_SaveProductNewDataErrorEvent(errorEvent: SaveProductNewDataErrorEvent) {
        internalTxService.createError(ErrorCreateDto(
                errorEvent.event.productNewData.eshopUuid,
                ErrorType.UNKNOWN, null,
                errorEvent.error.message,
                ExceptionUtils.getStackTrace(errorEvent.error),
                null,
                errorEvent.event.toString()))
    }

    private fun handle_RetrieveDocumentForUrlErrorEvent(errorEvent: RetrieveDocumentForUrlErrorEvent) {
        internalTxService.createError(ErrorCreateDto(
                errorEvent.event.eshopUuid,
                ErrorType.UNKNOWN, null,
                errorEvent.error.message,
                ExceptionUtils.getStackTrace(errorEvent.error),
                null,
                errorEvent.event.toString()))
    }

    private fun handle_ParseProductNewDataErrorEvent(errorEvent: ParseProductNewDataErrorEvent) {
        internalTxService.createError(ErrorCreateDto(
                errorEvent.event.eshopUuid,
                ErrorType.PARSING_PRODUCT_NEW_DATA, null,
                errorEvent.error.message,
                ExceptionUtils.getStackTrace(errorEvent.error),
                errorEvent.event.newProductUrl,
                errorEvent.event.toString()))
    }

    private fun handle_FilterNotExistingErrorEvent(errorEvent: FilterNotExistingErrorEvent) {
        internalTxService.createError(ErrorCreateDto(
                errorEvent.event.eshopUuid,
                ErrorType.UNKNOWN, null,
                errorEvent.error.message,
                ExceptionUtils.getStackTrace(errorEvent.error),
                null,
                errorEvent.event.toString()))
    }

    private fun handle_FilterDuplicityErrorEvent(errorEvent: FilterDuplicityErrorEvent) {
        internalTxService.createError(ErrorCreateDto(
                errorEvent.event.eshopUuid,
                ErrorType.UNKNOWN, null,
                errorEvent.error.message,
                ExceptionUtils.getStackTrace(errorEvent.error),
                null,
                errorEvent.event.toString()))
    }

    private fun handle_ParseCountOfPagesErrorEvent(errorEvent: ParseCountOfPagesErrorEvent) {
        internalTxService.createError(ErrorCreateDto(
                errorEvent.event.eshopUuid,
                ErrorType.UNKNOWN, null,
                errorEvent.error.message,
                ExceptionUtils.getStackTrace(errorEvent.error),
                null,
                errorEvent.event.toString()))
    }

    private fun handle_ParseProductListURLsErrorEvent(errorEvent: ParseProductListURLsErrorEvent) {
        internalTxService.createError(ErrorCreateDto(
                errorEvent.event.eshopUuid,
                ErrorType.UNKNOWN, null,
                errorEvent.error.message,
                ExceptionUtils.getStackTrace(errorEvent.error),
                null,
                errorEvent.event.toString()))
    }

}