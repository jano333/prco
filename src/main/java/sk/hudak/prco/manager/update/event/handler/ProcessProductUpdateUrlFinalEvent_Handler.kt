package sk.hudak.prco.manager.update.event.handler

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.events.handler.EshopLogSupplier
import sk.hudak.prco.manager.update.event.ProcessProductUpdateUrlFinalEvent
import sk.hudak.prco.manager.update.event.UpdateProductExecutors
import sk.hudak.prco.manager.update.event.UpdateProductWithNewUrlErrorEvent
import sk.hudak.prco.service.InternalTxService
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

@Component
class ProcessProductUpdateUrlFinalEvent_Handler(prcoObservable: PrcoObservable,
                                                updateProductExecutors: UpdateProductExecutors,
                                                val internalTxService: InternalTxService)

    : UpdateProcessHandler<ProcessProductUpdateUrlFinalEvent>(prcoObservable, updateProductExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(ProductUpdateDataRedirectEvent_5a_Handler::class.java)!!
    }

    override fun isSpecificType(event: CoreEvent): Boolean = event is ProcessProductUpdateUrlFinalEvent
    override fun getEshopUuid(event: ProcessProductUpdateUrlFinalEvent): EshopUuid? = event.productUpdateData.eshopUuid
    override fun getIdentifier(event: ProcessProductUpdateUrlFinalEvent): String = event.identifier

    override fun handle(event: ProcessProductUpdateUrlFinalEvent) {
        LOG.trace("handle $event")

        updateProductWithNewUrl(event.productForUpdateData.id, event.productUpdateData.url, event.productForUpdateData.eshopUuid, event.identifier)
                .handle { productId, error ->
                    if (error == null) {
                        LOG.info("product with id $productId was updated with url ${event.productUpdateData.url}")
                    } else {
                        prcoObservable.notify(UpdateProductWithNewUrlErrorEvent(event, error))
                    }
                }
    }

    private fun updateProductWithNewUrl(productId: Long, newProductUrl: String, eshopUuid: EshopUuid, identifier: String): CompletableFuture<Long> {
        return CompletableFuture.supplyAsync(EshopLogSupplier(eshopUuid, identifier,
                Supplier {
                    LOG.trace("updateProductWithNewUrl")
                    internalTxService.updateProductUrl(productId, newProductUrl)
                    productId
                }),
                updateProductExecutors.internalServiceExecutor)
    }

}

