package sk.hudak.prco.task.handler.update

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dto.product.ProductDetailInfo
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.service.InternalTxService
import sk.hudak.prco.task.handler.EshopLogSupplier
import sk.hudak.prco.task.update.*
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

@Component
class ProductUpdateDataRedirectEvent_5c_Handler(prcoObservable: PrcoObservable,
                                                updateProductExecutors: UpdateProductExecutors,
                                                val internalTxService: InternalTxService)

    : UpdateProcessHandler<ProductUpdateDataRedirectEvent>(prcoObservable, updateProductExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(ProductUpdateDataRedirectEvent_5c_Handler::class.java)!!
    }

    override fun isSpecificType(event: CoreEvent): Boolean = event is ProductUpdateDataRedirectEvent
    override fun getEshopUuid(event: ProductUpdateDataRedirectEvent): EshopUuid? = event.productUpdateData.eshopUuid
    override fun getIdentifier(event: ProductUpdateDataRedirectEvent): String = event.identifier

    override fun handle(event: ProductUpdateDataRedirectEvent) {
        LOG.trace("handle $event")

        findRedirectProductByUrl(event.productUpdateData.url, event.productForUpdate.eshopUuid, event.identifier)
                .handle { newProductForUpdate, error ->
                    if (error == null) {
                        if (newProductForUpdate == null) {
                            LOG.debug("product with redirect URL: ${event.productUpdateData.url} not exist")
                            prcoObservable.notify(ProductUpdateDataRedirectNotYetExistEvent(event.productUpdateData, event.productForUpdate, event.identifier))
                        } else {
                            LOG.debug("product with redirect URL: ${event.productUpdateData.url} exist, id: ${newProductForUpdate.id}")
                            prcoObservable.notify(ProductUpdateDataRedirectAlreadyExistEvent(newProductForUpdate, event.productUpdateData, event.productForUpdate, event.identifier))
                        }
                    } else {
                        prcoObservable.notify(FindRedirectProductByUrlErrorEvent(event, error))
                    }
                }
    }

    private fun findRedirectProductByUrl(redirectUrl: String, eshopUuid: EshopUuid, identifier: String): CompletableFuture<ProductDetailInfo?> {
        return CompletableFuture.supplyAsync(EshopLogSupplier(eshopUuid, identifier,
                Supplier {
                    LOG.trace("findRedirectProductByUrl")
                    internalTxService.getProductForUpdateByUrl(redirectUrl)
                }),
                updateProductExecutors.internalServiceExecutor)
    }
}



