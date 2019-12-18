package sk.hudak.prco.manager.update.event.handler

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.events.handler.EshopLogSupplier
import sk.hudak.prco.manager.update.event.ProcessRemoveOldProductFinalEvent
import sk.hudak.prco.manager.update.event.RemoveProductWithOldUrlErrorEvent
import sk.hudak.prco.manager.update.event.UpdateProductExecutors
import sk.hudak.prco.service.InternalTxService
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

@Component
class ProcessRemoveOldProductFinalEvent_7a_Handler(prcoObservable: PrcoObservable,
                                                   updateProductExecutors: UpdateProductExecutors,
                                                   val internalTxService: InternalTxService)

    : UpdateProcessHandler<ProcessRemoveOldProductFinalEvent>(prcoObservable, updateProductExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(ProcessRemoveOldProductFinalEvent_7a_Handler::class.java)!!
    }

    override fun isSpecificType(event: CoreEvent): Boolean = event is ProcessRemoveOldProductFinalEvent
    override fun getEshopUuid(event: ProcessRemoveOldProductFinalEvent): EshopUuid? = event.productUpdateData.eshopUuid
    override fun getIdentifier(event: ProcessRemoveOldProductFinalEvent): String = event.identifier

    override fun handle(event: ProcessRemoveOldProductFinalEvent) {
        LOG.trace("handle $event")

        removeProductWithOldUrl(event.productForUpdateData.id, event.productForUpdateData.eshopUuid, event.identifier)
                .handle { productId, error ->
                    if (error == null) {
                        LOG.debug("product with id $productId was removed")
                    } else {
                        prcoObservable.notify(RemoveProductWithOldUrlErrorEvent(event, error))
                    }
                }
    }

    private fun removeProductWithOldUrl(productId: Long, eshopUuid: EshopUuid, identifier: String): CompletableFuture<Long> {
        return CompletableFuture.supplyAsync(EshopLogSupplier(eshopUuid, identifier,
                Supplier {
                    LOG.trace("removeProductWithOldUrl")
                    // remove product with old URL
                    internalTxService.removeProduct(productId)
                    productId
                }),
                updateProductExecutors.internalServiceExecutor)
    }
}

