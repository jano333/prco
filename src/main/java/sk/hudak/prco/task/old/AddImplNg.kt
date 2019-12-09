package sk.hudak.prco.task.old

import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.builder.SearchUrlBuilder
import sk.hudak.prco.dto.ProductNewData
import sk.hudak.prco.exception.EshopParserNotFoundException
import sk.hudak.prco.parser.eshop.EshopProductsParser
import sk.hudak.prco.parser.html.HtmlParser
import sk.hudak.prco.service.InternalTxService
import java.util.*
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Consumer
import java.util.function.Supplier


@Deprecated("use diffrent version")
@Component
class AddImplNg(private val internalTxService: InternalTxService,
                private val htmlParser: HtmlParser,
                private val productParsers: List<EshopProductsParser>,
                private var searchUrlBuilder: SearchUrlBuilder) {

    companion object {
        val LOG = LoggerFactory.getLogger(AddImplNg::class.java)!!
    }

    private val internalServiceExecutor: ExecutorService = createInternalThreadExecutor("db-service", 20)
    private val searchUrlBuilderExecutor: ExecutorService = createInternalThreadExecutor("search-url", 2)
    private val htmlParserExecutor: ExecutorService = createInternalThreadExecutor("html-parser", 10)
    private val otherTaskExecutor: ExecutorService = createInternalThreadExecutor("other-task", 10)
    private val eshopDocumentExecutor = EnumMap<EshopUuid, ScheduledExecutorService>(EshopUuid::class.java)

    init {
        //TODO should down of executor
        EshopUuid.values().forEach {
            eshopDocumentExecutor[it] = createEshopThreadExecutor(it)
        }
    }

    private fun createInternalThreadExecutor(prefix: String, nThreads: Int): ExecutorService {
        return ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS,
                LinkedBlockingQueue(),
                InternalThreadFactory(prefix))
    }

    private fun createEshopThreadExecutor(eshopUuid: EshopUuid): ScheduledExecutorService {
        return EshopScheduledExecutor(eshopUuid, ThreadFactory {
            val thread = Thread(it, "${eshopUuid.name}-thread")
            thread.uncaughtExceptionHandler = PrcoUncaughtExceptionHandler(eshopUuid)
            thread
        })
    }

    private fun getEshopExecutor(eshopUuid: EshopUuid): ScheduledExecutorService {
        return eshopDocumentExecutor[eshopUuid]!!
    }


    fun addNewProductsByKeywordForAllEshops(eshopUuid: EshopUuid, searchKeyWordId: Long) {
        //TODO tato metoda uz predpoklada ze dane eshop urcite podporuje dane id( pozri eshopUuid.config.supportedSearchKeywordIds)
        val eshopExecutor: ScheduledExecutorService = getEshopExecutor(eshopUuid)
        val eshopParser = findParserForEshop(eshopUuid)

        val countOfPagesFuture: CompletableFuture<DocumentWithPageCountData>
        try {
            countOfPagesFuture =
                    // 1. searchKeywordId -> searchKeyword
                    retrieveKeywordBaseOnKeywordId(searchKeyWordId)
                            // 2. searchKeyword ->  searchKeywordRL
                            .thenComposeAsync { searchKeyword ->
                                buildSearchUrlForKeyword(eshopUuid, searchKeyword)
                            }
                            // 3. zavolam preklopenie URL na Document
                            .thenComposeAsync { searchUrlWithKeywordData ->
                                retrieveDocumentForSearchUrl(searchUrlWithKeywordData, eshopParser, eshopExecutor)
                            }
                            // 4. vyparsujem pocet stranok z Document-u
                            .thenComposeAsync { documentData ->
                                parseCountOfPages(eshopParser, documentData)
                            }


        } catch (e: Exception) {
            //TODO error processing
            LOG.error("error while retrieving count of product, error type class: ${e.javaClass.simpleName}", e)
            //TODO close executors...
            return
        }

        try {
            // 5. spustim procesovanie dokumentu ktory obsahuje pocet stran
            val resultOfAll = countOfPagesFuture.thenAcceptAsync(
                    processDocumentWithPageNumber(eshopParser, eshopExecutor), otherTaskExecutor)

        } catch (e: Exception) {
            //TODO error processing
            LOG.error("error while processing, error type class: ${e.javaClass.simpleName}")
            LOG.error("error", e)
            //TODO close executors...
            return
        }

    }

    private fun findParserForEshop(eshopUuid: EshopUuid): EshopProductsParser {
        return productParsers.stream()
                .filter { it.eshopUuid == eshopUuid }
                .findFirst()
                .orElseThrow { EshopParserNotFoundException(eshopUuid) }
    }

    private fun retrieveKeywordBaseOnKeywordId(searchKeyWordId: Long): CompletableFuture<String> {
        return CompletableFuture.supplyAsync(Supplier {
            LOG.trace("retrieveKeywordBaseOnKeywordId")
            internalTxService.getSearchKeywordById(searchKeyWordId)
        }, internalServiceExecutor)
    }

    private fun buildSearchUrlForKeyword(eshopUuid: EshopUuid, searchKeyword: String): CompletableFuture<SearchUrlWithKeywordData> {
        return CompletableFuture.supplyAsync(Supplier {
            LOG.trace("buildSearchUrlForKeyword")

            val searchUrl = searchUrlBuilder.buildSearchUrl(eshopUuid, searchKeyword)
            LOG.debug("build url for keyword $searchKeyword : $searchUrl")
            SearchUrlWithKeywordData(eshopUuid, searchKeyword, searchUrl)
        }, searchUrlBuilderExecutor)
    }

    private fun retrieveDocumentForSearchUrl(searchUrlWithKeywordData: SearchUrlWithKeywordData,
                                             eshopParser: EshopProductsParser,
                                             eshopExecutor: ScheduledExecutorService): CompletableFuture<DocumentData> {
        return CompletableFuture.supplyAsync(Supplier {
            LOG.trace("retrieveDocumentForUrl")

            DocumentData(searchUrlWithKeywordData.eshopUuid,
                    searchUrlWithKeywordData.searchKeyword,
                    searchUrlWithKeywordData.searchUrl,
                    eshopParser.retrieveDocument(searchUrlWithKeywordData.searchUrl))
        }, eshopExecutor)

    }

    private fun parseCountOfPages(eshopParser: EshopProductsParser, documentData: DocumentData): CompletionStage<DocumentWithPageCountData> {
        return CompletableFuture.supplyAsync(Supplier {
            LOG.trace("retrieveCountOfPages")

            val countOfPages = eshopParser.parseCountOfPages(documentData.document)
            LOG.debug("count of pages is $countOfPages for URL ${documentData.searchUrl} ")
            DocumentWithPageCountData(documentData.eshopUuid,
                    documentData.searchKeyword,
                    documentData.document,
                    countOfPages)
        }, htmlParserExecutor)
    }

    private fun processDocumentWithPageNumber(eshopParser: EshopProductsParser, eshopExecutor: ScheduledExecutorService)
            : Consumer<in DocumentWithPageCountData> {

        return Consumer { documentWithPageCountData ->
            LOG.trace("parseDocumentPages")
            val countOfPages = documentWithPageCountData.countOfPages

            // parse current document(1 page)
            // 1. Document -> productURLs[]
            parseProductListURLs(eshopParser, documentWithPageCountData.document, 1)
                    // 2. process productURLs[]
                    .thenAcceptAsync(processPageUrls(documentWithPageCountData.document.location()), otherTaskExecutor)

            // ak mame viac ako jednu stranku tak spusti processing dalsich stranok
            if (countOfPages != 1) {
                for (currentPageNumber in 2..countOfPages) {
                    processNextPage(eshopParser, eshopExecutor, documentWithPageCountData.searchKeyword, currentPageNumber)
                }
            }
        }
    }

    private fun processPageUrls(baseUrl: String): Consumer<in List<String>> {
        return Consumer { productUrls ->
            val countOfAll = productUrls.size
            var currentIndex = 0
            for (productUrl in productUrls) {
                currentIndex++
                LOG.debug("starting $currentIndex/$countOfAll for base URL $baseUrl")
                LOG.debug("processing URL $productUrl")

                try {
                    processProductUrl(productUrl)

                } catch (e: Exception) {
                    LOG.error("error while parsing product URL $productUrl", e)
                    //TODO logiku ci pokracovat s parsovanim dalsieho alebo nie...
                    break
                }

            } // end of for loop

        }
    }

    private fun processProductUrl(productUrl: String) {
        val supplyAsync = CompletableFuture.supplyAsync(
                Supplier {
                    LOG.trace("processProductUrl")
                    val existProductWithGivenURL = existProductWithGivenURL(productUrl)

                    existProductWithGivenURL.thenAccept { exist ->
                        if (exist) {
                            LOG.debug("product $productUrl already existing")
                        } else {
                            // 2 na zaklade danej URL vyparsujem ProductNewData
                            CompletableFuture.supplyAsync(parseProductNewData(productUrl), htmlParserExecutor)
                                    .handle { result, exception ->
                                        if (exception == null) {
                                            processProductNewData(result)
                                        } else {
                                            processExceptionDuringParsingProductNewData(exception)
                                        }
                                    }
                        }
                    }
                },
                otherTaskExecutor)
    }

    private fun processProductNewData(result: ProductNewData) {
        LOG.debug("processing new product ${result.name} url ${result.url}")
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

    }

    private fun processExceptionDuringParsingProductNewData(exception: Throwable?) {
        LOG.error("processExceptionDuringParsingProductNewData", exception)
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun processNextPage(eshopParser: EshopProductsParser,
                                eshopExecutor: ScheduledExecutorService,
                                searchKeyword: String,
                                currentPageNumber: Int) {
        LOG.debug("start processing page $currentPageNumber")

        // 1. currentPageNumber -> searchUrlWithPageNumber
        val productUrlListFuture = buildSearchUrlForGivenPageNumber(eshopParser, searchKeyword, currentPageNumber)
                // 2. searchUrlWithPageNumber -> Document
                .thenComposeAsync { searchUrlWithPageNumber ->
                    retrieveDocumentForSearchUrlWithPageNumber(eshopParser, eshopExecutor, searchUrlWithPageNumber)
                }
                // 3. Document -> productURLs[]
                .thenComposeAsync { document ->
                    parseProductListURLs(eshopParser, document, currentPageNumber)
                }

        // 4. spracuj kazdu URL produktu
        productUrlListFuture.thenAcceptAsync(processPageUrls("FIXME"), otherTaskExecutor)

        // dalsi callback(ak by sme chceli este nieco robit s tym vysledkom)
        productUrlListFuture.thenAcceptAsync(Consumer { productUrls ->
            // FIXME

        }, otherTaskExecutor)
    }

    private fun buildSearchUrlForGivenPageNumber(eshopParser: EshopProductsParser,
                                                 searchKeyword: String,
                                                 currentPageNumber: Int): CompletableFuture<String> {
        return CompletableFuture.supplyAsync(Supplier {
            LOG.trace("buildSearchUrlForGivenPageNumber")
            val buildSearchUrl = searchUrlBuilder.buildSearchUrl(eshopParser.eshopUuid, searchKeyword, currentPageNumber)
            LOG.debug("search URL for page $currentPageNumber : $buildSearchUrl")

            buildSearchUrl
        }, searchUrlBuilderExecutor)
    }

    private fun parseProductNewData(productUrl: String): Supplier<ProductNewData> {
        //TODO je zle.... toto ma ist v eshop thead... nie takto
        // teda najprv daj mi dokument
        // a potom vyparsuj nove data
        //

        return Supplier {
            htmlParser.parseProductNewData(productUrl)
        }
    }

    private fun existProductWithGivenURL(productURL: String): CompletableFuture<Boolean> {
        return CompletableFuture.supplyAsync(Supplier {
            internalTxService.existProductWithURL(productURL)
        }, internalServiceExecutor)
    }

    private fun retrieveDocumentForSearchUrlWithPageNumber(eshopParser: EshopProductsParser,
                                                           eshopExecutor: ScheduledExecutorService,
                                                           searchUrlWithPageNumber: String): CompletableFuture<Document> {
        return CompletableFuture.supplyAsync(Supplier {
            LOG.trace("retrieveDocumentForSearchUrlWithPageNumber")

            val document = eshopParser.retrieveDocument(searchUrlWithPageNumber)
            document
        }, eshopExecutor)
    }

    private fun parseProductListURLs(eshopParser: EshopProductsParser, document: Document, currentPageNumber: Int): CompletableFuture<List<String>> {
//        LOG.trace(object : Any() {}.javaClass.enclosingMethod.name)

        return CompletableFuture.supplyAsync(
                Supplier {
                    LOG.trace("parseProductListURLs")
                    val parsePageForProductUrls = eshopParser.parsePageForProductUrls(document, currentPageNumber)
                    LOG.debug("page: $currentPageNumber, count of products: ${parsePageForProductUrls.size}")
                    parsePageForProductUrls
                },
                htmlParserExecutor)

    }

    data class SearchUrlWithKeywordData(val eshopUuid: EshopUuid,
                                        val searchKeyword: String,
                                        var searchUrl: String)

    data class DocumentData(val eshopUuid: EshopUuid,
                            val searchKeyword: String,
                            val searchUrl: String,
                            val document: Document)

    data class DocumentWithPageCountData(val eshopUuid: EshopUuid,
                                         val searchKeyword: String,
                                         val document: Document,
                                         val countOfPages: Int)

    data class DocumentDataForGivenPageNumber(val eshopUuid: EshopUuid,
                                              val searchKeyword: String,
                                              val searchUrl: String,
                                              val pageNumber: Int,
                                              val document: Document)
}

class InternalThreadFactory(prefix: String) : ThreadFactory {

    private val group: ThreadGroup
    private val threadNumber = AtomicInteger(1)
    private val namePrefix: String

    companion object {
        private val poolNumber = AtomicInteger(1)
    }

    init {
        val s = System.getSecurityManager()
        group = if (s != null) s.threadGroup else Thread.currentThread().threadGroup
        namePrefix = "$prefix-pool-" + poolNumber.getAndIncrement() + "-thread-"
    }

    override fun newThread(r: Runnable): Thread {
        val t = Thread(group,
                r,
                namePrefix + threadNumber.getAndIncrement(),
                0)

        if (t.isDaemon)
            t.isDaemon = false
        if (t.priority != Thread.NORM_PRIORITY)
            t.priority = Thread.NORM_PRIORITY
        return t
    }


}

