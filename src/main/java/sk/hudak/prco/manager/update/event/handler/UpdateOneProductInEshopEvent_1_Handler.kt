package sk.hudak.prco.manager.update.event.handler

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dto.product.ProductDetailInfo
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.events.handler.EshopLogSupplier
import sk.hudak.prco.manager.update.event.LoadNextProductToBeUpdatedErrorEvent
import sk.hudak.prco.manager.update.event.ProductDetailInfoForUpdateEvent
import sk.hudak.prco.manager.update.event.UpdateOneProductInEshopEvent
import sk.hudak.prco.manager.update.event.UpdateProductExecutors
import sk.hudak.prco.service.InternalTxService
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

@Component
class UpdateOneProductInEshopEvent_1_Handler(prcoObservable: PrcoObservable,
                                             updateProductExecutors: UpdateProductExecutors,
                                             val internalTxService: InternalTxService)
    : UpdateProcessHandler<UpdateOneProductInEshopEvent>(prcoObservable, updateProductExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(UpdateOneProductInEshopEvent_1_Handler::class.java)!!
    }

    override fun isSpecificType(event: CoreEvent): Boolean = event is UpdateOneProductInEshopEvent
    override fun getEshopUuid(event: UpdateOneProductInEshopEvent): EshopUuid? = event.eshopUuid
    override fun getIdentifier(event: UpdateOneProductInEshopEvent): String = event.identifier

    override fun handle(event: UpdateOneProductInEshopEvent) {
        LOG.trace("handle $event")

        loadNextProductToBeUpdated(event.eshopUuid, event.identifier)
                .handle { productDetailInfo, exception ->
                    if (exception == null) {
                        if (productDetailInfo != null) {
                            prcoObservable.notify(ProductDetailInfoForUpdateEvent(productDetailInfo, event.identifier))
                        } else {
                            LOG.debug("nothing for update")
                        }
                    } else {
                        prcoObservable.notify(LoadNextProductToBeUpdatedErrorEvent(event, exception))
                    }
                }
    }


    private fun loadNextProductToBeUpdated(eshopUuid: EshopUuid, identifier: String): CompletableFuture<ProductDetailInfo?> {
        return CompletableFuture.supplyAsync(EshopLogSupplier(eshopUuid, identifier,
                Supplier {
                    LOG.trace("loadNextProductToBeUpdated")
                    internalTxService.findProductInEshopForUpdate(eshopUuid, eshopUuid.olderThanInHours)
                }),
                updateProductExecutors.internalServiceExecutor)
    }
}


