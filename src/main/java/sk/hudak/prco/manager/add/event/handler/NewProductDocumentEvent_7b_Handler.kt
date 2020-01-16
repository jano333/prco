package sk.hudak.prco.manager.add.event.handler

import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dto.ProductNewData
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.events.handler.EshopLogSupplier
import sk.hudak.prco.events.helper.EshopProductsParserHelper
import sk.hudak.prco.manager.add.event.AddProductExecutors
import sk.hudak.prco.manager.add.event.NewProductDocumentEvent
import sk.hudak.prco.manager.add.event.ParseProductNewDataErrorEvent
import sk.hudak.prco.manager.add.event.ProductNewDataEvent
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

@Component
class NewProductDocumentEvent_7b_Handler(prcoObservable: PrcoObservable,
                                         addProductExecutors: AddProductExecutors,
                                         private val eshopProductsParserHelper: EshopProductsParserHelper)
    : AddProcessHandler<NewProductDocumentEvent>(prcoObservable, addProductExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(NewProductDocumentEvent_7b_Handler::class.java)!!
    }

    override fun isSpecificType(event: CoreEvent): Boolean = event is NewProductDocumentEvent
    override fun getEshopUuid(event: NewProductDocumentEvent): EshopUuid? = event.eshopUuid
    override fun getIdentifier(event: NewProductDocumentEvent): String = event.identifier

    override fun handle(event: NewProductDocumentEvent) {

        parseProductNewData(event.document, event.newProductUrl, event.eshopUuid, event.identifier)
                .handle { productNewData, exception ->
                    if (exception == null) {
                        prcoObservable.notify(ProductNewDataEvent(productNewData, event.identifier))
                    } else {
                        prcoObservable.notify(ParseProductNewDataErrorEvent(event, exception))
                    }
                }
    }

    private fun parseProductNewData(document: Document, newProductUrl: String, eshopUuid: EshopUuid, identifier: String): CompletableFuture<ProductNewData> {
        return CompletableFuture.supplyAsync(EshopLogSupplier(eshopUuid, identifier,
                Supplier {
                    LOG.trace("parseProductNewData")
                    eshopProductsParserHelper.findParserForEshop(eshopUuid).parseProductNewData(document, newProductUrl)
                }),
                addProductExecutors.htmlParserExecutor)
    }
}

