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
import sk.hudak.prco.task.update.ProcessProductUpdateDataFinalEvent
import sk.hudak.prco.task.update.UpdateProductExecutors
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

@Component
class ProcessProductUpdateDataFinalEvent_Handler(prcoObservable: PrcoObservable,
                                                 updateProductExecutors: UpdateProductExecutors,
                                                 val internalTxService: InternalTxService)

    : UpdateProcessHandler<ProcessProductUpdateDataFinalEvent>(prcoObservable, updateProductExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(ProcessProductUpdateDataFinalEvent_Handler::class.java)!!
    }

    override fun isSpecificType(event: CoreEvent): Boolean = event is ProcessProductUpdateDataFinalEvent
    override fun getEshopUuid(event: ProcessProductUpdateDataFinalEvent): EshopUuid? = event.productForUpdateData.eshopUuid
    override fun getIdentifier(event: ProcessProductUpdateDataFinalEvent): String = event.identifier

    override fun handle(event: ProcessProductUpdateDataFinalEvent) {
        LOG.trace("handle $event")

        updateProductData(event.productUpdateData, event.productForUpdateData.id, event.identifier)
                .handle { _, error ->
                    if (error != null) {
                        prcoObservable.notify(ProcessProductUpdateDataErrorEvent(event, error))
                    }
                }
    }

    //TODO tato metoda je aj v ProcessProductUpdateDataForRedirectEvent_7b_Handler
    private fun updateProductData(productUpdateData: ProductUpdateData, id: Long, identifier: String): CompletableFuture<Long> {
        return CompletableFuture.supplyAsync(EshopLogSupplier(productUpdateData.eshopUuid, identifier,
                Supplier {
                    LOG.trace("updateProductData")
                    internalTxService.updateProduct(productUpdateData.toProductUpdateDataDto(id))
                    LOG.debug("product with id $id was updated")
                    id
                }),
                updateProductExecutors.internalServiceExecutor)
    }
}