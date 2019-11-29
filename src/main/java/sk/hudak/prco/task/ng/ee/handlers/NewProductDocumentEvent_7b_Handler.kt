package sk.hudak.prco.task.ng.ee.handlers

import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.dto.ProductNewData
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.events.PrcoObserver
import sk.hudak.prco.parser.eshop.EshopProductsParser
import sk.hudak.prco.task.ng.ee.Executors
import sk.hudak.prco.task.ng.ee.NewProductDocumentEvent
import sk.hudak.prco.task.ng.ee.ParseProductNewDataErrorEvent
import sk.hudak.prco.task.ng.ee.ProductNewDataEvent
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

@Component
class NewProductDocumentEvent_7b_Handler(private val prcoObservable: PrcoObservable,
                                         private val executors: Executors)
    : PrcoObserver {

    companion object {
        private val LOG = LoggerFactory.getLogger(NewProductDocumentEvent_7b_Handler::class.java)!!
    }

    // registering itself as observer
    init {
        prcoObservable.addObserver(this)
    }

    private fun handle(event: NewProductDocumentEvent) {
        LOG.trace("handle ${event.javaClass.simpleName}")

        parseProductNewData(event.document, event.newProductUrl, event.eshopParser)
                .handle { productNewData, exception ->
                    if (exception == null) {
                        prcoObservable.notify(ProductNewDataEvent(productNewData))
                    } else {
                        prcoObservable.notify(ParseProductNewDataErrorEvent(event, exception))
                    }
                }
    }


    private fun parseProductNewData(document: Document, newProductUrl: String, eshopParser: EshopProductsParser): CompletableFuture<ProductNewData> {
        return CompletableFuture.supplyAsync(
                Supplier {
                    LOG.trace("parseProductNewData")
                    eshopParser.parseProductNewData(document, newProductUrl)
                },
                executors.htmlParserExecutor)
    }

    override fun update(source: Observable?, event: CoreEvent) {
        when (event) {
            is NewProductDocumentEvent -> executors.handlerTaskExecutor.submit { handle(event) }
        }
    }
}

