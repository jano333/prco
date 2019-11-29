package sk.hudak.prco.task.ng.ee.handlers

import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.events.PrcoObserver
import sk.hudak.prco.exception.EshopParserNotFoundException
import sk.hudak.prco.parser.eshop.EshopProductsParser
import sk.hudak.prco.task.ng.ee.Executors
import sk.hudak.prco.task.ng.ee.FirstDocumentEvent
import sk.hudak.prco.task.ng.ee.NewKeyWordUrlEvent
import sk.hudak.prco.task.ng.ee.RetrieveDocumentForSearchUrlErrorEvent
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ScheduledExecutorService
import java.util.function.Supplier

/**
 * searchKeywordURL -> Document
 */
@Component
class NewKeyWordSearchUrlEvent_3_Handler(private val prcoObservable: PrcoObservable,
                                         private val productParsers: List<EshopProductsParser>,
                                         private val executors: Executors)
    : PrcoObserver {

    companion object {
        private val LOG = LoggerFactory.getLogger(NewKeyWordSearchUrlEvent_3_Handler::class.java)!!
    }

    // registering itself as observer
    init {
        prcoObservable.addObserver(this)
    }

    /**
     * searchKeywordURL -> Document
     */
    private fun handle(event: NewKeyWordUrlEvent) {
        LOG.trace("handle ${event.javaClass.simpleName}")
        val eshopParser = findParserForEshop(event.eshopUuid)
        val eshopExecutor: ScheduledExecutorService = executors.getEshopExecutor(event.eshopUuid)

        retrieveDocumentForSearchUrl(event.searchUrl, eshopParser, eshopExecutor)
                .handle { document, exception ->
                    if (exception == null) {
                        prcoObservable.notify(FirstDocumentEvent(document, event.searchKeyWord, event.searchUrl, eshopParser))
                    } else {
                        prcoObservable.notify(RetrieveDocumentForSearchUrlErrorEvent(event, exception))
                    }
                }
    }

    private fun retrieveDocumentForSearchUrl(searchUrl: String,
                                             eshopParser: EshopProductsParser,
                                             eshopExecutor: ScheduledExecutorService): CompletableFuture<Document> {
        return CompletableFuture.supplyAsync(
                Supplier {
                    LOG.trace("retrieveDocumentForUrl")
                    eshopParser.retrieveDocument(searchUrl)
                },
                eshopExecutor)

    }

    override fun update(source: Observable?, event: CoreEvent) {
        when (event) {
            is NewKeyWordUrlEvent -> executors.handlerTaskExecutor.submit { handle(event) }
        }
    }

    private fun findParserForEshop(eshopUuid: EshopUuid): EshopProductsParser {
        return productParsers.stream()
                .filter { it.eshopUuid == eshopUuid }
                .findFirst()
                .orElseThrow { EshopParserNotFoundException(eshopUuid) }
    }


}