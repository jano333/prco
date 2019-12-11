package sk.hudak.prco.task.handler.update

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.service.InternalTxService
import sk.hudak.prco.task.update.ProductUpdateDataRedirectNotYetExistEvent
import sk.hudak.prco.task.update.UpdateProductExecutors

@Component
class ProductUpdateDataRedirectNotYetExistEvent_6c_Handler(prcoObservable: PrcoObservable,
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


    }


//    log.debug("product with redirect URL ${updateData.url} not exist")
//    // if not available -> update only URL
//    if (!updateData.isProductAvailable) {
//        // update only URL of product
//        internalTxService.updateProductUrl(productForUpdate.id, updateData.url)
//        // mark it as unavailable
//        internalTxService.markProductAsUnavailable(productForUpdate.id)
//        return ContinueUpdateStatus.CONTINUE_TO_NEXT_ONE_ERROR
//    }
//
//    // update product data
//    internalTxService.updateProduct(updateData.toProductUpdateDataDto(productForUpdate.id))
//    return ContinueUpdateStatus.CONTINUE_TO_NEXT_ONE_OK
}