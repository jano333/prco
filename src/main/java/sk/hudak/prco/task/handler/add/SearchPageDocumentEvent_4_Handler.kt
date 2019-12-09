package sk.hudak.prco.task.handler.add

import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.task.add.*
import sk.hudak.prco.task.handler.EshopLogSupplier
import sk.hudak.prco.task.helper.EshopProductsParserHelper
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
        LOG.trace("handle $event")

        val currentPageNumber = event.pageNumber

        if (currentPageNumber == 1) {
            // 4.a Document -> countOfPages
            parseCountOfPages(event.eshopUuid, event.searchDocument, event.searchUrl, event.identifier)
                    .handle { countOfPages, exception ->
                        if (exception == null) {
                            prcoObservable.notify(CountOfPagesEvent(countOfPages, event.searchUrl, event.searchKeyWord, event.eshopUuid, event.identifier))
                        } else {
                            prcoObservable.notify(ParseCountOfPagesErrorEvent(event, exception))
                        }
                    }
        }

        // 4.b Document -> pageProductURLs[]
        parseProductListURLs(event.eshopUuid, event.searchDocument, currentPageNumber, event.identifier)
                .handle { pageProductURLs, exception ->
                    if (exception == null) {
                        prcoObservable.notify(NewProductEshopUrlsEvent(pageProductURLs, event.searchDocument, currentPageNumber,
                                event.eshopUuid, event.searchKeyWord, event.searchUrl, event.identifier))
                    } else {
                        prcoObservable.notify(ParseProductListURLsErrorEvent(event, currentPageNumber, exception))
                    }
                }
    }

    private fun parseCountOfPages(eshopUuid: EshopUuid, document: Document, searchUrl: String, identifier: String): CompletableFuture<Int> {
        return CompletableFuture.supplyAsync(EshopLogSupplier(eshopUuid, identifier,
                Supplier {
                    LOG.trace("parseCountOfPages")
                    val parserForEshop = eshopProductsParserHelper.findParserForEshop(eshopUuid)
                    val countOfPages = parserForEshop.parseCountOfPages(document)
                    LOG.debug("count of pages is $countOfPages for URL $searchUrl ")
                    countOfPages
                }),
                addProductExecutors.htmlParserExecutor)
    }

    // FIXME toto je spolocna metoda[TODO kde este] do neakej pomocnej...
    private fun parseProductListURLs(eshopUuid: EshopUuid, document: Document, currentPageNumber: Int, identifier: String): CompletableFuture<List<String>> {
        return CompletableFuture.supplyAsync(EshopLogSupplier(eshopUuid, identifier,
                Supplier {
                    LOG.trace("parseProductListURLs")
                    val parserForEshop = eshopProductsParserHelper.findParserForEshop(eshopUuid)
                    val parsePageForProductUrls = parserForEshop.parsePageForProductUrls(document, currentPageNumber)
                    LOG.debug("page: $currentPageNumber, count of products: ${parsePageForProductUrls.size}")
                    parsePageForProductUrls
                }),
                addProductExecutors.htmlParserExecutor)
    }

    override fun update(source: Observable?, event: CoreEvent) {
        when (event) {
            is SearchPageDocumentEvent -> {
                LOG.trace(">> update ${event.javaClass.simpleName}")
                addProductExecutors.handlerTaskExecutor.submit {
                    MDC.put("eshop", event.eshopUuid.toString())
                    MDC.put("identifier", event.identifier)
                    handle(event)
                    MDC.remove("eshop")
                    MDC.remove("identifier")
                }
                LOG.trace("<< update ${event.javaClass.simpleName}")
            }
        }
    }


}