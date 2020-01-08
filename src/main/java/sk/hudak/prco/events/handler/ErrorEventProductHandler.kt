package sk.hudak.prco.events.handler

import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.ErrorType
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dto.ErrorCreateDto
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.ErrorEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.events.PrcoObserver
import sk.hudak.prco.exception.CoreParserException
import sk.hudak.prco.exception.ProductPriceNotFoundException
import sk.hudak.prco.manager.add.event.*
import sk.hudak.prco.manager.update.event.*
import sk.hudak.prco.service.InternalTxService
import java.util.*
import java.util.concurrent.CompletionException
import javax.net.ssl.SSLHandshakeException

// TODO urobit to tiez asynchronne cez osobintne vlakla tu update metodu tak ako je add alebo update event handlery

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

    override fun update(source: Observable?, errorEvent: CoreEvent) {
        if (errorEvent !is ErrorEvent) {
            return
        }
        // create error log
        logErrorEvent(errorEvent)

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
            }
        }

        // process specifig errors if needed...
        when (errorEvent) {
            // add process
            is RetrieveKeywordBaseOnKeywordIdErrorEvent -> handle_RetrieveKeywordBaseOnKeywordIdErrorEvent(errorEvent)
            is BuildSearchUrlForKeywordErrorEvent -> handle_BuildSearchUrlForKeywordErrorEvent(errorEvent)
            is RetrieveDocumentForSearchUrlErrorEvent -> handle_RetrieveDocumentForSearchUrlErrorEvent(errorEvent)
            is ParseCountOfPagesErrorEvent -> handle_ParseCountOfPagesErrorEvent(errorEvent)
            is ParseProductListURLsErrorEvent -> handle_ParseProductListURLsErrorEvent(errorEvent)
            is FilterDuplicityErrorEvent -> handle_FilterDuplicityErrorEvent(errorEvent)
            is FilterNotExistingErrorEvent -> handle_FilterNotExistingErrorEvent(errorEvent)
            is RetrieveDocumentForUrlErrorEvent -> handle_RetrieveDocumentForUrlErrorEvent(errorEvent)
            is ParseProductNewDataErrorEvent -> handle_ParseProductNewDataErrorEvent(errorEvent)
            is SaveProductNewDataErrorEvent -> handle_SaveProductNewDataErrorEvent(errorEvent)
            is BuildNextPageSearchUrlErrorEvent -> handle_BuildNextPageSearchUrlErrorEvent(errorEvent)
            is FilterNotExistingProductErrorEvent -> handle_FilterNotExistingProductErrorEvent(errorEvent)
            is ParseEshopUuidErrorEvent -> handle_ParseEshopUuidErrorEvent(errorEvent)

            // update process
            is LoadNextProductToBeUpdatedErrorEvent -> handle_LoadNextProductToBeUpdatedErrorEvent(errorEvent)
            is RetrieveUpdateDocumentForUrlErrorEvent -> {
                //nic je na to samostatny handler
            }
            is ParseProductUpdateDataErrorEvent -> handle_ParseProductUpdateDataErrorEvent(errorEvent)
            is ProcessProductUpdateDataErrorEvent -> handle_ProcessProductUpdateDataErrorEvent(errorEvent)
            is MarkProductAsUnavailableErrorEvent -> handle_MarkProductAsUnavailableErrorEvent(errorEvent)
            is FindRedirectProductByUrlErrorEvent -> handle_FindRedirectProductByUrlErrorEvent(errorEvent)
            is UpdateProductWithNewUrlErrorEvent -> handle_UpdateProductWithNewUrlErrorEvent(errorEvent)
            is RemoveProductWithOldUrlErrorEvent -> handle_RemoveProductWithOldUrlErrorEvent(errorEvent)
            is ProcessProductUpdateDataForRedirectErrorEvent -> handle_ProcessProductUpdateDataForRedirectErrorEvent(errorEvent)
            is LoadProductsToBeUpdatedErrorEvent -> handle_LoadProductsToBeUpdatedErrorEvent(errorEvent)
        }
    }

    private fun handle_LoadProductsToBeUpdatedErrorEvent(errorEvent: LoadProductsToBeUpdatedErrorEvent) {
        saveGeneric(errorEvent, errorEvent.event.eshopUuid, null)
    }

    private fun handle_ProcessProductUpdateDataErrorEvent(errorEvent: ProcessProductUpdateDataErrorEvent) {
        saveGeneric(errorEvent, errorEvent.event.productForUpdateData.eshopUuid, errorEvent.event.productForUpdateData.url)
    }

    private fun handle_ProcessProductUpdateDataForRedirectErrorEvent(errorEvent: ProcessProductUpdateDataForRedirectErrorEvent) {
        saveGeneric(errorEvent, errorEvent.event.newProductForUpdateData.eshopUuid, errorEvent.event.newProductForUpdateData.url)
    }

    private fun handle_RemoveProductWithOldUrlErrorEvent(errorEvent: RemoveProductWithOldUrlErrorEvent) {
        saveGeneric(errorEvent, errorEvent.event.productForUpdateData.eshopUuid, errorEvent.event.productForUpdateData.url)
    }

    private fun handle_UpdateProductWithNewUrlErrorEvent(errorEvent: UpdateProductWithNewUrlErrorEvent) {
        saveGeneric(errorEvent, errorEvent.event.productForUpdateData.eshopUuid, errorEvent.event.productForUpdateData.url)
    }

    private fun handle_FindRedirectProductByUrlErrorEvent(errorEvent: FindRedirectProductByUrlErrorEvent) {
        saveGeneric(errorEvent, errorEvent.event.productForUpdate.eshopUuid, errorEvent.event.productForUpdate.url)
    }

    private fun handle_MarkProductAsUnavailableErrorEvent(errorEvent: MarkProductAsUnavailableErrorEvent) {
        saveGeneric(errorEvent, errorEvent.event.productForUpdateData.eshopUuid, errorEvent.event.productForUpdateData.url)
    }

    private fun handle_LoadNextProductToBeUpdatedErrorEvent(errorEvent: LoadNextProductToBeUpdatedErrorEvent) {
        saveGeneric(errorEvent, errorEvent.event.eshopUuid, null)
    }

    private fun handle_ParseProductUpdateDataErrorEvent(errorEvent: ParseProductUpdateDataErrorEvent) {
        when (errorEvent.error.cause) {
            is ProductPriceNotFoundException -> {
                internalTxService.createError(ErrorCreateDto(
                        eshopUuid = errorEvent.event.productForUpdate.eshopUuid,
                        errorType = ErrorType.PARSING_PRODUCT_UPDATE_DATA,
                        url = errorEvent.event.productForUpdate.url,
                        message = errorEvent.error.message,
                        fullMsg = ExceptionUtils.getStackTrace(errorEvent.error),
                        additionalInfo = errorEvent.event.toString()))
            }
            else -> {
                saveGeneric(errorEvent, errorEvent.event.productForUpdate.eshopUuid, errorEvent.event.productForUpdate.url)
            }
        }
    }

    private fun saveGeneric(errorEvent: ErrorEvent, eshopUuid: EshopUuid, url: String?) {
        internalTxService.createError(ErrorCreateDto(
                eshopUuid,
                ErrorType.UNKNOWN, null,
                errorEvent.error.message,
                ExceptionUtils.getStackTrace(errorEvent.error),
                url,
                errorEvent.event.toString()))
    }

    private fun logErrorEvent(errorEvent: ErrorEvent) {
        LOG.error("error event: ${errorEvent.javaClass.simpleName}")
        LOG.error("error while processing source event: ${errorEvent.event}")
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
        saveGeneric(errorEvent, errorEvent.event.eshopUuid, null)
    }

    private fun handle_BuildSearchUrlForKeywordErrorEvent(errorEvent: BuildSearchUrlForKeywordErrorEvent) {
        addProductExecutors.shutdownNowAllExecutors()
        saveGeneric(errorEvent, errorEvent.event.eshopUuid, null)
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

        if (errorEvent.error.cause is CoreParserException && (errorEvent.error.cause as CoreParserException).cause is SSLHandshakeException) {
            internalTxService.createError(ErrorCreateDto(
                    errorEvent.event.eshopUuid,
                    ErrorType.HTTP_SSL_ERR, null,
                    errorEvent.error.message,
                    ExceptionUtils.getStackTrace(errorEvent.error),
                    errorEvent.event.searchUrl,
                    errorEvent.event.toString()))
        } else {
            //TODO zle ...
            internalTxService.createError(ErrorCreateDto(
                    errorEvent.event.eshopUuid,
                    ErrorType.PARSING_PRODUCT_NEW_DATA, null,
                    errorEvent.error.message,
                    ExceptionUtils.getStackTrace(errorEvent.error),
                    errorEvent.event.searchUrl,
                    errorEvent.event.toString()))
        }


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