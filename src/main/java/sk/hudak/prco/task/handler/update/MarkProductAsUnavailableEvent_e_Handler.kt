package sk.hudak.prco.task.handler.update

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.service.InternalTxService
import sk.hudak.prco.task.handler.EshopLogSupplier
import sk.hudak.prco.task.update.MarkProductAsUnavailableErrorEvent
import sk.hudak.prco.task.update.MarkProductAsUnavailableEvent
import sk.hudak.prco.task.update.UpdateProductExecutors
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

@Component
class MarkProductAsUnavailableEvent_e_Handler(prcoObservable: PrcoObservable,
                                              updateProductExecutors: UpdateProductExecutors,
                                              val internalTxService: InternalTxService)

    : UpdateProcessHandler<MarkProductAsUnavailableEvent>(prcoObservable, updateProductExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(ProductUpdateDataEvent_4_Handler::class.java)!!
    }

    override fun isSpecificType(event: CoreEvent): Boolean = event is MarkProductAsUnavailableEvent
    override fun getEshopUuid(event: MarkProductAsUnavailableEvent): EshopUuid? = event.productForUpdateData.eshopUuid
    override fun getIdentifier(event: MarkProductAsUnavailableEvent): String = event.identifier

    override fun handle(event: MarkProductAsUnavailableEvent) {
        LOG.trace("handle $event")

        markProductAsUnavailable(event.productForUpdateData.id, event.productForUpdateData.eshopUuid, event.identifier)
                .handle { id, error ->
                    if (error == null) {
                        LOG.debug("product with id $id was marked as unavailable")
                    } else {
                        prcoObservable.notify(MarkProductAsUnavailableErrorEvent(event, error))
                    }
                }
    }

    private fun markProductAsUnavailable(id: Long, eshopUuid: EshopUuid, identifier: String): CompletableFuture<Long> {
        return CompletableFuture.supplyAsync(EshopLogSupplier(eshopUuid, identifier,
                Supplier {
                    LOG.trace("markProductAsUnavailable")
                    internalTxService.markProductAsUnavailable(id)
                    id
                }),
                updateProductExecutors.internalServiceExecutor)
    }

}

