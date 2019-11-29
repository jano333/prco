package sk.hudak.prco.task.ng.ee.handlers

import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.events.PrcoObserver
import sk.hudak.prco.exception.EshopParserNotFoundException
import sk.hudak.prco.parser.eshop.EshopProductsParser
import sk.hudak.prco.task.ng.ee.Executors
import sk.hudak.prco.task.ng.ee.NewProductUrlEvent
import sk.hudak.prco.task.ng.ee.ProductDocumentEvent
import sk.hudak.prco.task.ng.ee.RetrieveDocumentForUrlErrorEvent
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ScheduledExecutorService
import java.util.function.Supplier

class NewProductUrlEvent_6_Handler(private val prcoObservable: PrcoObservable,
                                   private val productParsers: List<EshopProductsParser>,
                                   private val executors: Executors)
    : PrcoObserver {

    companion object {
        private val LOG = LoggerFactory.getLogger(NewProductUrlEvent_6_Handler::class.java)!!
    }

    // registering itself as observer
    init {
        prcoObservable.addObserver(this)
    }


    private fun handle(event: NewProductUrlEvent) {
        LOG.trace("handle ${event.javaClass.simpleName}")

        // productURL -> Document
        // FIXME toto je aj v NewKeyWordSearchUrlEvent_3_Handler
        val eshopParser = findParserForEshop(event.eshopUuid)
        val eshopExecutor: ScheduledExecutorService = executors.getEshopExecutor(event.eshopUuid)

        retrieveDocumentForUrl(event.newProductUrl, eshopParser, eshopExecutor)
                .handle { document, exception ->
                    if (exception == null) {
                        prcoObservable.notify(ProductDocumentEvent(document, event.newProductUrl, eshopParser))
                    } else {
                        prcoObservable.notify(RetrieveDocumentForUrlErrorEvent(event, exception))
                    }
                }
    }

    //FIXME dana metoda je aj v NewKeyWordSearchUrlEvent_3_Handler
    private fun retrieveDocumentForUrl(productUrl: String,
                                       eshopParser: EshopProductsParser,
                                       eshopExecutor: ScheduledExecutorService): CompletableFuture<Document> {
        return CompletableFuture.supplyAsync(Supplier {
            LOG.trace("retrieveDocumentForUrl")
            eshopParser.retrieveDocument(productUrl)
        }, eshopExecutor)

    }

    // FIXME toto je aj v NewKeyWordSearchUrlEvent_3_Handler
    private fun findParserForEshop(eshopUuid: EshopUuid): EshopProductsParser {
        return productParsers.stream()
                .filter { it.eshopUuid == eshopUuid }
                .findFirst()
                .orElseThrow { EshopParserNotFoundException(eshopUuid) }
    }

    override fun update(source: Observable?, event: CoreEvent) {
        when (event) {
            is NewProductUrlEvent -> executors.handlerTaskExecutor.submit { handle(event) }
        }
    }
}