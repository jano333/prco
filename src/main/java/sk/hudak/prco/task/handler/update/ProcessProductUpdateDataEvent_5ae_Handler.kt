package sk.hudak.prco.task.handler.update

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dto.ProductUpdateData
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.service.InternalTxService
import sk.hudak.prco.task.handler.EshopLogSupplier
import sk.hudak.prco.task.ng.toProductUpdateDataDto
import sk.hudak.prco.task.update.ProcessProductUpdateDataErrorEvent
import sk.hudak.prco.task.update.ProcessProductUpdateDataEvent
import sk.hudak.prco.task.update.UpdateProductExecutors
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

@Component
class ProcessProductUpdateDataEvent_5ae_Handler(prcoObservable: PrcoObservable,
                                                updateProductExecutors: UpdateProductExecutors,
                                                val internalTxService: InternalTxService)

    : UpdateProcessHandler<ProcessProductUpdateDataEvent>(prcoObservable, updateProductExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(ProcessProductUpdateDataEvent_5ae_Handler::class.java)!!
    }

    override fun isSpecificType(event: CoreEvent): Boolean = event is ProcessProductUpdateDataEvent
    override fun getEshopUuid(event: ProcessProductUpdateDataEvent): EshopUuid? = event.productForUpdateData.eshopUuid
    override fun getIdentifier(event: ProcessProductUpdateDataEvent): String = event.identifier

    override fun handle(event: ProcessProductUpdateDataEvent) {
        LOG.trace("handle $event")

        updateProductData(event.productUpdateData, event.productForUpdateData.id, event.identifier)
                .handle { id, error ->
                    if (error == null) {
                        LOG.debug("product with id $id was updated")
                    } else {
                        prcoObservable.notify(ProcessProductUpdateDataErrorEvent(event, error))
                    }
                }
    }

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