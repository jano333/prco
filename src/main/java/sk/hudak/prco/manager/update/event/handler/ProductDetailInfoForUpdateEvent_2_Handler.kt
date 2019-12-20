package sk.hudak.prco.manager.update.event.handler

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.events.helper.BasicDocumentHelper
import sk.hudak.prco.manager.update.event.ProductDetailInfoForUpdateEvent
import sk.hudak.prco.manager.update.event.RetrieveUpdateDocumentForUrlErrorEvent
import sk.hudak.prco.manager.update.event.UpdateProductDocumentEvent
import sk.hudak.prco.manager.update.event.UpdateProductExecutors

@Component
class ProductDetailInfoForUpdateEvent_2_Handler(prcoObservable: PrcoObservable,
                                                updateProductExecutors: UpdateProductExecutors,
                                                private val documentHelper: BasicDocumentHelper)

    : UpdateProcessHandler<ProductDetailInfoForUpdateEvent>(prcoObservable, updateProductExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(ProductDetailInfoForUpdateEvent_2_Handler::class.java)!!
    }

    override fun isSpecificType(event: CoreEvent): Boolean = event is ProductDetailInfoForUpdateEvent
    override fun getEshopUuid(event: ProductDetailInfoForUpdateEvent): EshopUuid? = event.productDetailInfo.eshopUuid
    override fun getIdentifier(event: ProductDetailInfoForUpdateEvent): String = event.identifier

    override fun handle(event: ProductDetailInfoForUpdateEvent) {
        LOG.trace("handle $event")

        documentHelper.retrieveDocumentForUrl(event.productDetailInfo.url, event.productDetailInfo.eshopUuid, event.identifier)
                .handle { document, exception ->
                    if (exception == null) {
                        prcoObservable.notify(UpdateProductDocumentEvent(document, event.productDetailInfo, event.identifier))
                    } else {
                        prcoObservable.notify(RetrieveUpdateDocumentForUrlErrorEvent(event, exception))
                    }
                }
    }
}

