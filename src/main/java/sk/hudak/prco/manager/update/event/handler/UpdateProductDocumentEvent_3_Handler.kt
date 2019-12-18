package sk.hudak.prco.manager.update.event.handler

import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dto.ProductUpdateData
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.events.handler.EshopLogSupplier
import sk.hudak.prco.events.helper.EshopProductsParserHelper
import sk.hudak.prco.manager.update.event.ParseProductUpdateDataErrorEvent
import sk.hudak.prco.manager.update.event.ProductUpdateDataEvent
import sk.hudak.prco.manager.update.event.UpdateProductDocumentEvent
import sk.hudak.prco.manager.update.event.UpdateProductExecutors
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

@Component
class UpdateProductDocumentEvent_3_Handler(prcoObservable: PrcoObservable,
                                           updateProductExecutors: UpdateProductExecutors,
                                           private val eshopProductsParserHelper: EshopProductsParserHelper)

    : UpdateProcessHandler<UpdateProductDocumentEvent>(prcoObservable, updateProductExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(UpdateProductDocumentEvent_3_Handler::class.java)!!
    }

    override fun isSpecificType(event: CoreEvent): Boolean = event is UpdateProductDocumentEvent
    override fun getEshopUuid(event: UpdateProductDocumentEvent): EshopUuid? = event.productForUpdate.eshopUuid
    override fun getIdentifier(event: UpdateProductDocumentEvent): String = event.identifier

    override fun handle(event: UpdateProductDocumentEvent) {
        LOG.trace("handle $event")

        parseProductUpdateData(event.document, event.productForUpdate.url, event.productForUpdate.eshopUuid, event.identifier)
                .handle { productUpdateData, exception ->
                    if (exception == null) {
                        prcoObservable.notify(ProductUpdateDataEvent(productUpdateData, event.productForUpdate, event.identifier))
                    } else {
                        prcoObservable.notify(ParseProductUpdateDataErrorEvent(event, exception))
                    }
                }
    }

    private fun parseProductUpdateData(document: Document, updateProductUrl: String, eshopUuid: EshopUuid, identifier: String): CompletableFuture<ProductUpdateData> {
        return CompletableFuture.supplyAsync(EshopLogSupplier(eshopUuid, identifier,
                Supplier {
                    LOG.trace("parseProductUpdateData")
                    eshopProductsParserHelper.findParserForEshop(eshopUuid).parseProductUpdateData(document, updateProductUrl)
                }),
                updateProductExecutors.htmlParserExecutor)
    }
}


