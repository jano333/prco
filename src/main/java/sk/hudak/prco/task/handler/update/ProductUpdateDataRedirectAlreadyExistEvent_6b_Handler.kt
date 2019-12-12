package sk.hudak.prco.task.handler.update

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.service.InternalTxService
import sk.hudak.prco.task.update.*

@Component
class ProductUpdateDataRedirectAlreadyExistEvent_6b_Handler(prcoObservable: PrcoObservable,
                                                            updateProductExecutors: UpdateProductExecutors,
                                                            val internalTxService: InternalTxService)

    : UpdateProcessHandler<ProductUpdateDataRedirectAlreadyExistEvent>(prcoObservable, updateProductExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(ProductUpdateDataRedirectEvent_5a_Handler::class.java)!!
    }

    override fun isSpecificType(event: CoreEvent): Boolean = event is ProductUpdateDataRedirectAlreadyExistEvent
    override fun getEshopUuid(event: ProductUpdateDataRedirectAlreadyExistEvent): EshopUuid? = event.productUpdateData.eshopUuid
    override fun getIdentifier(event: ProductUpdateDataRedirectAlreadyExistEvent): String = event.identifier

    override fun handle(event: ProductUpdateDataRedirectAlreadyExistEvent) {
        LOG.trace("handle $event")

        LOG.debug("product with redirect URL: ${event.productUpdateData.url} exist, id: ${event.newProductForUpdateData.id}")

        // remove product with old URL
        prcoObservable.notify(ProcessRemoveOldProductFinalEvent(event.productUpdateData, event.productForUpdateData, event.identifier))

        // if not available -> continue to next one
        if (!event.productUpdateData.isProductAvailable) {
            //  mark it as unavailable
            prcoObservable.notify(MarkProductAsUnavailableFinalEvent(event.newProductForUpdateData, event.identifier))

        } else {
            // update product data
//            internalTxService.updateProduct(updateData.toProductUpdateDataDto(newProductForUpdate.id))
            prcoObservable.notify(ProcessProductUpdateDataForRedirectFinalEvent(event.newProductForUpdateData, event.productUpdateData, event.identifier))

        }

    }
}



