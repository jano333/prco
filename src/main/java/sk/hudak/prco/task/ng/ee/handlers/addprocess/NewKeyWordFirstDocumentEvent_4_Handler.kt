package sk.hudak.prco.task.ng.ee.handlers.addprocess

import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.task.ng.ee.*
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

@Component
class NewKeyWordFirstDocumentEvent_4_Handler(prcoObservable: PrcoObservable,
                                             addProductExecutors: AddProductExecutors,
                                             private val eshopProductsParserHelper: EshopProductsParserHelper)
    : AddProcessHandler(prcoObservable, addProductExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(NewKeyWordFirstDocumentEvent_4_Handler::class.java)!!
    }

    /**
     * 4.a Document -> countOfPages
     * 4.b Document -> firstPageProductURLs[]
     */
    private fun handle(event: FirstDocumentEvent) {
        LOG.trace("handle ${event.javaClass.simpleName}")

        // 4.a Document -> countOfPages
        parseCountOfPages(event.eshopUuid, event.document, event.searchUrl)
                .handle { countOfPages, exception ->
                    if (exception == null) {
                        prcoObservable.notify(CountOfPagesEvent(countOfPages, event.searchKeyWord, event.eshopUuid))
                    } else {
                        prcoObservable.notify(ParseCountOfPagesErrorEvent(event, exception))
                    }
                }

        // 4.b Document -> firstPageProductURLs[]
        val currentPageNumber = 1
        parseProductListURLs(event.eshopUuid, event.document, currentPageNumber)
                .handle { pageProductURLs, exception ->
                    if (exception == null) {
                        prcoObservable.notify(FirstPageProductURLsEvent(pageProductURLs, event.document, event.eshopUuid, event.searchKeyWord, event.searchUrl))
                    } else {
                        prcoObservable.notify(ParseProductListURLsErrorEvent(event, currentPageNumber, exception))
                    }
                }
    }

    private fun parseCountOfPages(eshopUuid: EshopUuid, document: Document, searchUrl: String): CompletableFuture<Int> {
        return CompletableFuture.supplyAsync(
                Supplier {
                    LOG.trace("parseCountOfPages")
                    val parserForEshop = eshopProductsParserHelper.findParserForEshop(eshopUuid)
                    val countOfPages = parserForEshop.parseCountOfPages(document)
                    LOG.debug("count of pages is $countOfPages for URL $searchUrl ")
                    countOfPages
                },
                addProductExecutors.htmlParserExecutor)
    }

    // FIXME toto je spolocna metoda[TODO kde este] do neakej pomocnej...
    private fun parseProductListURLs(eshopUuid: EshopUuid, document: Document, currentPageNumber: Int): CompletableFuture<List<String>> {
        return CompletableFuture.supplyAsync(
                Supplier {
                    LOG.trace("parseProductListURLs")
                    val parserForEshop = eshopProductsParserHelper.findParserForEshop(eshopUuid)
                    val parsePageForProductUrls = parserForEshop.parsePageForProductUrls(document, currentPageNumber)
                    LOG.debug("page: $currentPageNumber, count of products: ${parsePageForProductUrls.size}")
                    parsePageForProductUrls
                },
                addProductExecutors.htmlParserExecutor)
    }

    override fun update(source: Observable?, event: CoreEvent) {
        when (event) {
            is FirstDocumentEvent -> addProductExecutors.handlerTaskExecutor.submit { handle(event) }
        }
    }


}