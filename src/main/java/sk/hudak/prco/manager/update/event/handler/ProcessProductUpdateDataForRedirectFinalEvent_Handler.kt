package sk.hudak.prco.manager.update.event.handler

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dto.ProductUpdateData
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.events.handler.EshopLogSupplier
import sk.hudak.prco.kotlin.toProductUpdateDataDto
import sk.hudak.prco.manager.update.event.ProcessProductUpdateDataForRedirectErrorEvent
import sk.hudak.prco.manager.update.event.ProcessProductUpdateDataForRedirectFinalEvent
import sk.hudak.prco.manager.update.event.UpdateProductExecutors
import sk.hudak.prco.service.InternalTxService
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

@Component
class ProcessProductUpdateDataForRedirectFinalEvent_Handler(prcoObservable: PrcoObservable,
                                                            updateProductExecutors: UpdateProductExecutors,
                                                            val internalTxService: InternalTxService)

    : UpdateProcessHandler<ProcessProductUpdateDataForRedirectFinalEvent>(prcoObservable, updateProductExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(ProcessProductUpdateDataForRedirectFinalEvent_Handler::class.java)!!
    }

    override fun isSpecificType(event: CoreEvent): Boolean = event is ProcessProductUpdateDataForRedirectFinalEvent
    override fun getEshopUuid(event: ProcessProductUpdateDataForRedirectFinalEvent): EshopUuid? = event.productUpdateData.eshopUuid
    override fun getIdentifier(event: ProcessProductUpdateDataForRedirectFinalEvent): String = event.identifier

    override fun handle(event: ProcessProductUpdateDataForRedirectFinalEvent) {
        LOG.trace("handle $event")

        updateProductData(event.productUpdateData, event.newProductForUpdateData.id, event.identifier)
                .handle { id, error ->
                    if (error == null) {
                        LOG.debug("product with id $id was updated")
                    } else {
                        prcoObservable.notify(ProcessProductUpdateDataForRedirectErrorEvent(event, error))
                    }
                }
    }
        //FIXME duplicity s ProcessProductUpdateDataFinalEvent_Handler
    // urobit tak ako je osobitny handler
    private fun updateProductData(productUpdateData: ProductUpdateData, id: Long, identifier: String): CompletableFuture<Long> {
        return CompletableFuture.supplyAsync(EshopLogSupplier(productUpdateData.eshopUuid, identifier,
                Supplier {
                    LOG.trace("updateProductData")
                    internalTxService.updateProduct(productUpdateData.toProductUpdateDataDto(id))
                    id
                }),
                updateProductExecutors.internalServiceExecutor)
    }
}

