package sk.hudak.prco.task.handler.update

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dto.product.ProductDetailInfo
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.service.InternalTxService
import sk.hudak.prco.task.handler.EshopLogSupplier
import sk.hudak.prco.task.update.LoadProductsToBeUpdatedErrorEvent
import sk.hudak.prco.task.update.ProductDetailInfoForUpdateEvent
import sk.hudak.prco.task.update.UpdateAllProductsInEshopEvent
import sk.hudak.prco.task.update.UpdateProductExecutors
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

@Component
class UpdateAllProductsInEshopEvent_1_Handler(prcoObservable: PrcoObservable,
                                              updateProductExecutors: UpdateProductExecutors,
                                              val internalTxService: InternalTxService)
    : UpdateProcessHandler<UpdateAllProductsInEshopEvent>(prcoObservable, updateProductExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(UpdateAllProductsInEshopEvent_1_Handler::class.java)!!
    }

    override fun isSpecificType(event: CoreEvent): Boolean = event is UpdateAllProductsInEshopEvent
    override fun getEshopUuid(event: UpdateAllProductsInEshopEvent): EshopUuid? = event.eshopUuid
    override fun getIdentifier(event: UpdateAllProductsInEshopEvent): String = event.identifier

    override fun handle(event: UpdateAllProductsInEshopEvent) {
        LOG.trace("handle $event")

        loadProductsToBeUpdated(event.eshopUuid, event.identifier)
                .handle { productDetailInfos, exception ->
                    if (exception == null) {
                        if (productDetailInfos.isNotEmpty()) {
                            var i = 1
                            productDetailInfos.forEach {
                                prcoObservable.notify(ProductDetailInfoForUpdateEvent(it, event.identifier + "_" + i.toString()))
                                i++
                            }
                        } else {
                            LOG.debug("nothing for update")
                        }
                    } else {
                        prcoObservable.notify(LoadProductsToBeUpdatedErrorEvent(event, exception))
                    }
                }
    }

    private fun loadProductsToBeUpdated(eshopUuid: EshopUuid, identifier: String): CompletableFuture<List<ProductDetailInfo>> {
        return CompletableFuture.supplyAsync(EshopLogSupplier(eshopUuid, identifier,
                Supplier {
                    LOG.trace("loadProductsToBeUpdated")
                    val findProductsForUpdate = internalTxService.findProductsForUpdate(eshopUuid, eshopUuid.olderThanInHours)
                    LOG.info("count of products to be updated ${findProductsForUpdate.size}")
                    findProductsForUpdate
                }),
                updateProductExecutors.internalServiceExecutor)
    }
}



