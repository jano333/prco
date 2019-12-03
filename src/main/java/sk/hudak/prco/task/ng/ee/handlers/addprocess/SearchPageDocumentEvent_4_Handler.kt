package sk.hudak.prco.task.ng.ee.handlers.addprocess

import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.task.ng.ee.*
import sk.hudak.prco.task.ng.ee.helper.EshopProductsParserHelper
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

@Component
class SearchPageDocumentEvent_4_Handler(prcoObservable: PrcoObservable,
                                        addProductExecutors: AddProductExecutors,
                                        private val eshopProductsParserHelper: EshopProductsParserHelper)
    : AddProcessHandler(prcoObservable, addProductExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(SearchPageDocumentEvent_4_Handler::class.java)!!
    }

    /**
     * 4.a Document -> countOfPages
     * 4.b Document -> pageProductURLs[]
     */
    private fun handle(event: SearchPageDocumentEvent) {
        LOG.trace("handle ${event.javaClass.simpleName}")

        val currentPageNumber = event.pageNumber

        if (currentPageNumber == 1) {
            // 4.a Document -> countOfPages
            parseCountOfPages(event.eshopUuid, event.searchDocument, event.searchUrl)
                    .handle { countOfPages, exception ->
                        if (exception == null) {
                            prcoObservable.notify(CountOfPagesEvent(countOfPages, event.searchKeyWord, event.eshopUuid))
                        } else {
                            prcoObservable.notify(ParseCountOfPagesErrorEvent(event, exception))
                        }
                    }
        }

        // 4.b Document -> pageProductURLs[]
        parseProductListURLs(event.eshopUuid, event.searchDocument, currentPageNumber)
                .handle { pageProductURLs, exception ->
                    if (exception == null) {
                        prcoObservable.notify(NewProductUrlsEvent(pageProductURLs, event.searchDocument, currentPageNumber, event.eshopUuid, event.searchKeyWord, event.searchUrl))
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
            is SearchPageDocumentEvent -> addProductExecutors.handlerTaskExecutor.submit { handle(event) }
        }
    }


}