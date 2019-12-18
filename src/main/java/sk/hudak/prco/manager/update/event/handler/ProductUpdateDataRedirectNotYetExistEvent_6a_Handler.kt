package sk.hudak.prco.manager.update.event.handler

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.manager.update.event.*
import sk.hudak.prco.service.InternalTxService

@Component
class ProductUpdateDataRedirectNotYetExistEvent_6a_Handler(prcoObservable: PrcoObservable,
                                                           updateProductExecutors: UpdateProductExecutors,
                                                           val internalTxService: InternalTxService)

    : UpdateProcessHandler<ProductUpdateDataRedirectNotYetExistEvent>(prcoObservable, updateProductExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(ProductUpdateDataRedirectEvent_5a_Handler::class.java)!!
    }

    override fun isSpecificType(event: CoreEvent): Boolean = event is ProductUpdateDataRedirectNotYetExistEvent
    override fun getEshopUuid(event: ProductUpdateDataRedirectNotYetExistEvent): EshopUuid? = event.productUpdateData.eshopUuid
    override fun getIdentifier(event: ProductUpdateDataRedirectNotYetExistEvent): String = event.identifier

    override fun handle(event: ProductUpdateDataRedirectNotYetExistEvent) {
        LOG.trace("handle $event")

        // if not available -> update only URL
        if (!event.productUpdateData.isProductAvailable) {
            // update only URL of product
            prcoObservable.notify(ProcessProductUpdateUrlFinalEvent(event.productUpdateData, event.productForUpdateData, event.identifier))
            //  mark it as unavailable
            prcoObservable.notify(MarkProductAsUnavailableFinalEvent(event.productForUpdateData, event.identifier))
        } else {
            // product is available -> update product data
            prcoObservable.notify(ProcessProductUpdateDataFinalEvent(event.productUpdateData, event.productForUpdateData, event.identifier))
        }

    }
}