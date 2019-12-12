package sk.hudak.prco.task.handler.update

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.service.InternalTxService
import sk.hudak.prco.task.update.*

@Component
class ProductUpdateDataEvent_4_Handler(prcoObservable: PrcoObservable,
                                       updateProductExecutors: UpdateProductExecutors,
                                       val internalTxService: InternalTxService)

    : UpdateProcessHandler<ProductUpdateDataEvent>(prcoObservable, updateProductExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(ProductUpdateDataEvent_4_Handler::class.java)!!
    }

    override fun isSpecificType(event: CoreEvent): Boolean = event is ProductUpdateDataEvent
    override fun getEshopUuid(event: ProductUpdateDataEvent): EshopUuid? = event.productUpdateData.eshopUuid
    override fun getIdentifier(event: ProductUpdateDataEvent): String = event.identifier

    override fun handle(event: ProductUpdateDataEvent) {
        LOG.trace("handle $event")

        // product is redirected
        if (event.productUpdateData.redirect) {
            // url was changed, try to find product with new URL
            prcoObservable.notify(ProductUpdateDataRedirectEvent(event.productUpdateData, event.productForUpdateData, event.identifier))
            return
        }

        // product is NOT available
        if (!event.productUpdateData.isProductAvailable) {
            //  mark it as unavailable
            prcoObservable.notify(MarkProductAsUnavailableFinalEvent(event.productForUpdateData, event.identifier))
            return
        }

        // product is available -> update product data
        prcoObservable.notify(ProcessProductUpdateDataFinalEvent(event.productUpdateData, event.productForUpdateData, event.identifier))
    }
}




