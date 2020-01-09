package sk.hudak.prco.manager.update.event.handler

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dto.product.ProductDetailInfo
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.events.handler.NoEshopLogSupplier
import sk.hudak.prco.manager.update.event.LoadProductToBeUpdatedErrorEvent
import sk.hudak.prco.manager.update.event.ProductDetailInfoForUpdateEvent
import sk.hudak.prco.manager.update.event.UpdateProductDataForProductIdEvent
import sk.hudak.prco.manager.update.event.UpdateProductExecutors
import sk.hudak.prco.service.InternalTxService
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

@Component
class UpdateProductDataForProductIdEvent_1_Handler(prcoObservable: PrcoObservable,
                                                   updateProductExecutors: UpdateProductExecutors,
                                                   val internalTxService: InternalTxService)
    : UpdateProcessHandler<UpdateProductDataForProductIdEvent>(prcoObservable, updateProductExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(UpdateProductDataForProductIdEvent_1_Handler::class.java)!!
    }

    override fun isSpecificType(event: CoreEvent): Boolean = event is UpdateProductDataForProductIdEvent
    override fun getEshopUuid(event: UpdateProductDataForProductIdEvent): EshopUuid? = null
    override fun getIdentifier(event: UpdateProductDataForProductIdEvent): String = event.identifier

    override fun handle(event: UpdateProductDataForProductIdEvent) {
        LOG.trace("handle $event")
        findProductForUpdate(event.productId, event.identifier)
                .handle { productDetailInfo, exception ->
                    if (exception == null) {
                        prcoObservable.notify(ProductDetailInfoForUpdateEvent(productDetailInfo, event.identifier))
                    } else {
                        prcoObservable.notify(LoadProductToBeUpdatedErrorEvent(event, exception))
                    }
                }
    }

    private fun findProductForUpdate(productId: Long, identifier: String): CompletableFuture<ProductDetailInfo> {
        return CompletableFuture.supplyAsync(NoEshopLogSupplier(identifier,
                Supplier {
                    LOG.trace("findProductForUpdate")
                    internalTxService.findProductById(productId)
                }),
                updateProductExecutors.internalServiceExecutor)
    }

}

