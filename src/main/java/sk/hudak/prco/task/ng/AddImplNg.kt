package sk.hudak.prco.task.ng

import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.builder.SearchUrlBuilder
import sk.hudak.prco.exception.EshopParserNotFoundException
import sk.hudak.prco.parser.eshop.EshopProductsParser
import sk.hudak.prco.parser.html.HtmlParser
import sk.hudak.prco.service.InternalTxService
import sk.hudak.prco.task.PrcoUncaughtExceptionHandler
import java.util.*
import java.util.concurrent.*
import java.util.concurrent.atomic.AtomicInteger
import java.util.function.Consumer
import java.util.function.Supplier


@Component
class AddImplNg(private val internalTxService: InternalTxService,
                private val htmlParser: HtmlParser,
                private val productParsers: List<EshopProductsParser>,
                private var searchUrlBuilder: SearchUrlBuilder) {

    companion object {
        val LOG = LoggerFactory.getLogger(AddImplNg::class.java)!!
    }

    private val internalDbServiceExecutor: ExecutorService = createInternalThreadExecutor("db-service", 20)
    private val internalSearchUrlBuilderExecutor: ExecutorService = createInternalThreadExecutor("search-url", 2)
    private val internalEshopParserExecutor: ExecutorService = createInternalThreadExecutor("eshop-parser", 10)
    private val eshopExecutor = EnumMap<EshopUuid, ScheduledExecutorService>(EshopUuid::class.java)

    init {
        //TODO should down of executor
        EshopUuid.values().forEach {
            eshopExecutor[it] = createEshopThreadExecutor(it)
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
        return eshopExecutor[eshopUuid]!!
    }


    fun addNewProductsByKeywordForAllEshops(eshopUuid: EshopUuid, searchKeyWordId: Long) {
        val eshopExecutor: ScheduledExecutorService = getEshopExecutor(eshopUuid)
        val eshopParser = findParserForEshop(eshopUuid)

        try {
            // 1. searchKeywordId -> searchKeyword
            val resultOfAll = retrieveKeywordBaseOnKeywordId(searchKeyWordId)
                    // 2. searchKeyword ->  searchKeywordRL
                    .thenComposeAsync { searchKeyword ->
                        buildSearchUrlForKeyword(eshopUuid, searchKeyword)
                    }
                    // 3. zavolam preklopenie URL na Document
                    .thenComposeAsync { searchUrlWithKeywordData ->
                        retrieveDocumentForSearchUrl(eshopParser, eshopExecutor, searchUrlWithKeywordData)
                    }
                    // 4. vyparsujem pocet stranok z Document-u
                    .thenComposeAsync { documentData ->
                        retrieveCountOfPages(eshopParser, documentData)
                    }
                    //5. pustim paralelne tolko vlakien kolko je stranok
                    .thenAcceptAsync(processEachDocumentWithPageNumber(eshopParser, eshopExecutor), internalDbServiceExecutor)


        } catch (e: Exception) {
            LOG.error("error type ${e.javaClass.simpleName}")
            LOG.error("error", e)
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
        }, internalDbServiceExecutor)
    }

    private fun buildSearchUrlForKeyword(eshopUuid: EshopUuid, searchKeyword: String): CompletableFuture<SearchUrlWithKeywordData> {
        return CompletableFuture.supplyAsync(Supplier {
            LOG.trace("buildSearchUrlForKeyword")

            val searchUrl = searchUrlBuilder.buildSearchUrl(eshopUuid, searchKeyword)
            LOG.debug("build url for keyword $searchKeyword : $searchUrl")
            SearchUrlWithKeywordData(eshopUuid, searchKeyword, searchUrl)
        }, internalSearchUrlBuilderExecutor)
    }

    private fun retrieveDocumentForSearchUrl(eshopParser: EshopProductsParser,
                                             eshopExecutor: ScheduledExecutorService,
                                             searchUrlWithKeywordData: SearchUrlWithKeywordData): CompletableFuture<DocumentData> {
        return CompletableFuture.supplyAsync(Supplier {
            LOG.trace("retrieveDocumentForUrl")

            DocumentData(searchUrlWithKeywordData.eshopUuid,
                    searchUrlWithKeywordData.searchKeyword,
                    searchUrlWithKeywordData.searchUrl,
                    eshopParser.retrieveDocument(searchUrlWithKeywordData.searchUrl))
        }, eshopExecutor)

    }

    private fun retrieveCountOfPages(eshopParser: EshopProductsParser, documentData: DocumentData): CompletionStage<DocumentWithPageCountData> {
        return CompletableFuture.supplyAsync(Supplier {
            LOG.trace("retrieveCountOfPages")

            val countOfPages = eshopParser.parseCountOfPages(documentData.document)
            LOG.debug("count of pages is $countOfPages for URL ${documentData.searchUrl} ")
            DocumentWithPageCountData(documentData.eshopUuid,
                    documentData.searchKeyword,
                    documentData.document,
                    countOfPages)
        }, internalEshopParserExecutor)
    }

    private fun processEachDocumentWithPageNumber(eshopParser: EshopProductsParser, eshopExecutor: ScheduledExecutorService)
            : Consumer<in DocumentWithPageCountData> {

        return Consumer { documentWithPageCountData ->
            LOG.trace("parseDocumentPages")

            //TODO pozor ak bude len 1 ako to funguje?
            for (currentPageNumber in 2..documentWithPageCountData.countOfPages) {
                processDocumentWithPageNumber(eshopParser, eshopExecutor, documentWithPageCountData, currentPageNumber)

            }
        }
    }

    private fun processDocumentWithPageNumber(eshopParser: EshopProductsParser,
                                              eshopExecutor: ScheduledExecutorService,
                                              documentWithPageCountData: DocumentWithPageCountData,
                                              currentPageNumber: Int) {
        LOG.debug("start processing page $currentPageNumber")

        // 1. currentPageNumber -> searchUrlWithPageNumber
        val resultOfAll = buildSearchUrlForGivenPageNumber(eshopParser, documentWithPageCountData.searchKeyword, currentPageNumber)
                // 2. searchUrlWithPageNumber -> DocumentForPageNumber
                .thenComposeAsync { searchUrlWithPageNumber ->
                    retrieveDocumentForSearchUrlWithPageNumber(eshopParser, eshopExecutor, searchUrlWithPageNumber)
                }
                // 3. DocumentForPageNumber -> productURLs[]
                .thenComposeAsync { document ->
                    parseProductListURLs(eshopParser, document, currentPageNumber)
                }
                // 4. spracuj kazdu URL produktu
                .thenAcceptAsync(processPageUrls(eshopParser, eshopExecutor), internalDbServiceExecutor)


    }

    private fun processPageUrls(eshopParser: EshopProductsParser, eshopExecutor: ScheduledExecutorService): Consumer<in List<String>> {
        return Consumer { productUrls ->

            productUrls.forEach { productUrl ->
                LOG.debug("processing product with URL $productUrl")

                // 1. vyskladam search url pre danu currentPageNumber
//                val alreadyExistGivenProductFuture: CompletableFuture<Boolean> = CompletableFuture.supplyAsync(
//                        existProductWithGivenURL(productUrl), internalExecutor)
//
//                alreadyExistGivenProductFuture.thenAcceptAsync(Consumer { exist ->
//                    if (exist) {
//                        LOG.debug("product $productUrl already existing")
//                    } else {
//
//                        // 2 na zaklade danej URL vyparsujem ProductNewData
//                        val productNewDataFuture = CompletableFuture.supplyAsync(parseProductNewData(productUrl), eshopExecutor)
//                        productNewDataFuture.handle { result, exception ->
//                            if (exception != null) {
//                                processExceptionDuringParsingProductNewData(exception)
//                                null
//                            } else {
//                                processProductNewData(result)
//                            }
//                        }
//
//
//                    }
//                }, internalExecutor)
//
//
//            }


            }
            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        }
    }

    private fun buildSearchUrlForGivenPageNumber(eshopParser: EshopProductsParser,
                                                 searchKeyword: String,
                                                 currentPageNumber: Int): CompletableFuture<String> {
        return CompletableFuture.supplyAsync(Supplier {
            LOG.trace("buildSearchUrlForGivenPageNumber")
            val buildSearchUrl = searchUrlBuilder.buildSearchUrl(eshopParser.eshopUuid, searchKeyword, currentPageNumber)
            LOG.debug("search URL for page $currentPageNumber : $buildSearchUrl")

            buildSearchUrl
        }, internalDbServiceExecutor)
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
        LOG.trace(object : Any() {}.javaClass.enclosingMethod.name)

        return CompletableFuture.supplyAsync(Supplier {
            LOG.trace("parseProductListURLs")
            val parsePageForProductUrls = eshopParser.parsePageForProductUrls(document, currentPageNumber)
            LOG.debug("page: $currentPageNumber, count of products: ${parsePageForProductUrls.size}")
            parsePageForProductUrls
        }, internalEshopParserExecutor)

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

