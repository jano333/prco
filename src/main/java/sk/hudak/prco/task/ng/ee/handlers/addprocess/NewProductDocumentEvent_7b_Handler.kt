package sk.hudak.prco.task.ng.ee.handlers.addprocess

import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dto.ProductNewData
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.task.ng.ee.*
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

@Component
class NewProductDocumentEvent_7b_Handler(prcoObservable: PrcoObservable,
                                         addProductExecutors: AddProductExecutors,
                                         private val eshopProductsParserHelper: EshopProductsParserHelper)
    : AddProcessHandler(prcoObservable, addProductExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(NewProductDocumentEvent_7b_Handler::class.java)!!
    }

    private fun handle(event: NewProductDocumentEvent) {
        LOG.trace("handle ${event.javaClass.simpleName}")

        parseProductNewData(event.document, event.newProductUrl, event.eshopUuid)
                .handle { productNewData, exception ->
                    if (exception == null) {
                        prcoObservable.notify(ProductNewDataEvent(productNewData))
                    } else {
                        prcoObservable.notify(ParseProductNewDataErrorEvent(event, exception))
                    }
                }
    }


    private fun parseProductNewData(document: Document, newProductUrl: String, eshopUuid: EshopUuid): CompletableFuture<ProductNewData> {
        return CompletableFuture.supplyAsync(
                Supplier {
                    LOG.trace("parseProductNewData")
                    eshopProductsParserHelper.findParserForEshop(eshopUuid).parseProductNewData(document, newProductUrl)
                },
                addProductExecutors.htmlParserExecutor)
    }

    override fun update(source: Observable?, event: CoreEvent) {
        when (event) {
            is NewProductDocumentEvent -> addProductExecutors.handlerTaskExecutor.submit { handle(event) }
        }
    }
}

