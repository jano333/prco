package sk.hudak.prco.task.ng.ee.handlers

import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.events.PrcoObserver
import sk.hudak.prco.parser.eshop.EshopProductsParser
import sk.hudak.prco.task.ng.ee.*
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

@Component
class NewKeyWordFirstDocumentEvent_4_Handler(private val prcoObservable: PrcoObservable,
                                             private val executors: Executors)
    : PrcoObserver {

    companion object {
        private val LOG = LoggerFactory.getLogger(NewKeyWordFirstDocumentEvent_4_Handler::class.java)!!
    }

    // registering itself as observer
    init {
        prcoObservable.addObserver(this)
    }

    /**
     * 4.a Document -> countOfPages
     * 4.b Document -> firstPageProductURLs[]
     */
    private fun handle(event: FirstDocumentEvent) {
        LOG.trace("handle ${event.javaClass.simpleName}")

        // 4.a Document -> countOfPages
        parseCountOfPages(event.eshopParser, event.document, event.searchUrl)
                .handle { countOfPages, exception ->
                    if (exception == null) {
                        prcoObservable.notify(CountOfPagesEvent(countOfPages, event.document, event.searchUrl))
                    } else {
                        prcoObservable.notify(ParseCountOfPagesErrorEvent(event, exception))
                    }
                }

        // 4.b Document -> firstPageProductURLs[]
        val currentPageNumber = 1
        parseProductListURLs(event.eshopParser, event.document, currentPageNumber)
                .handle { pageProductURLs, exception ->
                    if (exception == null) {
                        prcoObservable.notify(FirstPageProductURLsEvent(pageProductURLs, event.document, event.eshopParser.eshopUuid, event.searchKeyWord, event.searchUrl))
                    } else {
                        prcoObservable.notify(ParseProductListURLsErrorEvent(event, currentPageNumber, exception))
                    }
                }
    }

    private fun parseCountOfPages(eshopParser: EshopProductsParser, document: Document, searchUrl: String): CompletableFuture<Int> {
        return CompletableFuture.supplyAsync(
                Supplier {
                    LOG.trace("parseCountOfPages")
                    val countOfPages = eshopParser.parseCountOfPages(document)
                    LOG.debug("count of pages is $countOfPages for URL $searchUrl ")
                    countOfPages
                },
                executors.htmlParserExecutor)
    }

    // FIXME toto je spolocna metoda do neakej pomocnej...
    private fun parseProductListURLs(eshopParser: EshopProductsParser, document: Document, currentPageNumber: Int): CompletableFuture<List<String>> {
        return CompletableFuture.supplyAsync(
                Supplier {
                    LOG.trace("parseProductListURLs")
                    val parsePageForProductUrls = eshopParser.parsePageForProductUrls(document, currentPageNumber)
                    LOG.debug("page: $currentPageNumber, count of products: ${parsePageForProductUrls.size}")
                    parsePageForProductUrls
                },
                executors.htmlParserExecutor)
    }

    override fun update(source: Observable?, event: CoreEvent) {
        when (event) {
            is FirstDocumentEvent -> executors.handlerTaskExecutor.submit { handle(event) }
        }
    }


}