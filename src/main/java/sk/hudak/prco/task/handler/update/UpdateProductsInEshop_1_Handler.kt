package sk.hudak.prco.task.handler.update

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dto.product.ProductDetailInfo
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.service.InternalTxService
import sk.hudak.prco.task.handler.EshopLogSupplier
import sk.hudak.prco.task.update.LoadNextProductToBeUpdatedErrorEvent
import sk.hudak.prco.task.update.ProductDetailInfoForUpdateEvent
import sk.hudak.prco.task.update.UpdateProductExecutors
import sk.hudak.prco.task.update.UpdateProductsInEshopEvent
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

@Component
class UpdateProductsInEshop_1_Handler(prcoObservable: PrcoObservable,
                                      updateProductExecutors: UpdateProductExecutors,
                                      val internalTxService: InternalTxService)
    : UpdateProcessHandler<UpdateProductsInEshopEvent>(prcoObservable, updateProductExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(UpdateProductsInEshop_1_Handler::class.java)!!
    }

    override fun isSpecificType(event: CoreEvent): Boolean = event is UpdateProductsInEshopEvent
    override fun getEshopUuid(event: UpdateProductsInEshopEvent): EshopUuid? = event.eshopUuid
    override fun getIdentifier(event: UpdateProductsInEshopEvent): String = event.identifier

    override fun handle(event: UpdateProductsInEshopEvent) {
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
                    internalTxService.findProductForUpdate(eshopUuid, eshopUuid.olderThanInHours)
                }),
                updateProductExecutors.internalServiceExecutor)
    }
}


