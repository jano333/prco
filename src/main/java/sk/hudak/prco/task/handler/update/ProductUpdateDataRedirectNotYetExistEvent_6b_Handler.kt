package sk.hudak.prco.task.handler.update

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.service.InternalTxService
import sk.hudak.prco.task.update.*

@Component
class ProductUpdateDataRedirectNotYetExistEvent_6b_Handler(prcoObservable: PrcoObservable,
                                                           updateProductExecutors: UpdateProductExecutors,
                                                           val internalTxService: InternalTxService)

    : UpdateProcessHandler<ProductUpdateDataRedirectNotYetExistEvent>(prcoObservable, updateProductExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(ProductUpdateDataRedirectEvent_5c_Handler::class.java)!!
    }

    override fun isSpecificType(event: CoreEvent): Boolean = event is ProductUpdateDataRedirectNotYetExistEvent
    override fun getEshopUuid(event: ProductUpdateDataRedirectNotYetExistEvent): EshopUuid? = event.productUpdateData.eshopUuid
    override fun getIdentifier(event: ProductUpdateDataRedirectNotYetExistEvent): String = event.identifier

    override fun handle(event: ProductUpdateDataRedirectNotYetExistEvent) {
        LOG.trace("handle $event")

        // if not available -> update only URL
        if (!event.productUpdateData.isProductAvailable) {
            // update only URL of product
            prcoObservable.notify(ProcessProductUpdateUrlEvent(event.productUpdateData, event.productForUpdateData, event.identifier))
            //  mark it as unavailable
            prcoObservable.notify(MarkProductAsUnavailableEvent(event.productForUpdateData, event.identifier))
        } else {
            // product is available -> update product data
            prcoObservable.notify(ProcessProductUpdateDataEvent(event.productUpdateData, event.productForUpdateData, event.identifier))
        }

    }
}