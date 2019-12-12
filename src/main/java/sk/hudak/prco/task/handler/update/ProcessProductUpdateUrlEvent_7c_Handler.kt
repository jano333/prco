package sk.hudak.prco.task.handler.update

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.service.InternalTxService
import sk.hudak.prco.task.handler.EshopLogSupplier
import sk.hudak.prco.task.update.ProcessProductUpdateUrlEvent
import sk.hudak.prco.task.update.UpdateProductExecutors
import sk.hudak.prco.task.update.UpdateProductWithNewUrlErrorEvent
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

@Component
class ProcessProductUpdateUrlEvent_7c_Handler(prcoObservable: PrcoObservable,
                                              updateProductExecutors: UpdateProductExecutors,
                                              val internalTxService: InternalTxService)

    : UpdateProcessHandler<ProcessProductUpdateUrlEvent>(prcoObservable, updateProductExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(ProductUpdateDataRedirectEvent_5c_Handler::class.java)!!
    }

    override fun isSpecificType(event: CoreEvent): Boolean = event is ProcessProductUpdateUrlEvent
    override fun getEshopUuid(event: ProcessProductUpdateUrlEvent): EshopUuid? = event.productUpdateData.eshopUuid
    override fun getIdentifier(event: ProcessProductUpdateUrlEvent): String = event.identifier

    override fun handle(event: ProcessProductUpdateUrlEvent) {
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

