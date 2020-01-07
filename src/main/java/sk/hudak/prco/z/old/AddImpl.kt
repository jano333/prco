package sk.hudak.prco.z.old

import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.builder.SearchUrlBuilder
import sk.hudak.prco.dto.ProductNewData
import sk.hudak.prco.exception.EshopParserNotFoundException
import sk.hudak.prco.exception.PrcoRuntimeException
import sk.hudak.prco.parser.eshop.EshopProductsParser
import sk.hudak.prco.parser.html.HtmlParser
import sk.hudak.prco.service.InternalTxService
import sk.hudak.prco.utils.ThreadUtils
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.*
import java.util.concurrent.locks.ReentrantLock
import java.util.function.Consumer
import java.util.function.Function
import java.util.function.Supplier


@Deprecated("use diffrent version")
@Component
class AddImpl(private val internalTxService: InternalTxService,
              private val htmlParser: HtmlParser,
              private val productParsers: List<EshopProductsParser>,
              private var searchUrlBuilder: SearchUrlBuilder) {

    companion object {
        val LOG = LoggerFactory.getLogger(AddImpl::class.java)!!
    }

    private var internalExecutor: ExecutorService = Executors.newFixedThreadPool(20)
    private val executors = EnumMap<EshopUuid, ScheduledExecutorService>(EshopUuid::class.java)

    init {
        //TODO should down of executor
        EshopUuid.values().forEach {
            executors[it] = createExecutorServiceForEshop(it)
        }
    }

    private fun createExecutorServiceForEshop(eshopUuid: EshopUuid): ScheduledExecutorService {
        val threadFactory = ThreadFactory {
            val thread = Thread(it, "${eshopUuid.name}-thread")
            thread.uncaughtExceptionHandler = PrcoUncaughtExceptionHandler(eshopUuid)
            thread
        }
        return EshopScheduledExecutor(eshopUuid, threadFactory)
    }

    private fun getEshopExecutor(eshopUuid: EshopUuid): ScheduledExecutorService {
        return executors[eshopUuid]!!
    }


    fun addNewProductsByKeywordForAllEshops(eshopUuid: EshopUuid, searchKeyWordId: Long) {
        val eshopExecutor: ScheduledExecutorService = getEshopExecutor(eshopUuid)
        val eshopParser = findParserForEshop(eshopUuid)

        try {
            val keywordFuture: CompletableFuture<EshopKeywordData> = CompletableFuture.supplyAsync(
                    // 1. keyword id preklopim na string keyword
                    retrieveKeywordBaseOnKeywordId(EshopKeywordIdData(eshopUuid, searchKeyWordId)), internalExecutor)

            val buildSearchURLFuture: CompletableFuture<SearchUrlWithKeywordData> = keywordFuture.thenApplyAsync(
                    // 2. na zaklade keyword vyskladam search URL
                    buildSearchUrlForKeyword(), internalExecutor)

            val documentFuture: CompletableFuture<DocumentData> = buildSearchURLFuture.thenApplyAsync(
                    // 3. zavolam preklopenie URL na Document
                    retrieveDocumentForUrl(eshopParser), eshopExecutor)

            val documentWithPageCountFuture: CompletableFuture<DocumentWithPageCountData> = documentFuture.thenApplyAsync(
                    // 4. vyparsujem pocet stranok z Document-u
                    retrieveCountOfPages(eshopParser), internalExecutor)

            val resultOfAll = documentWithPageCountFuture.thenAcceptAsync(
                    //5. pustim paralelne tolko vlakien kolko je stranok
                    parseDocumentPages(eshopParser, eshopExecutor), internalExecutor)

        } catch (e: Exception) {
            LOG.error("error type ${e.javaClass.simpleName}")
            LOG.error("error", e)
            return
        }
    }

    private fun parseDocumentPages(eshopParser: EshopProductsParser, eshopExecutor: ScheduledExecutorService): Consumer<in DocumentWithPageCountData> {
        return Consumer { documentWithPageCountData ->
            LOG.debug("count of pages ${documentWithPageCountData.countOfPages}")

            //TODO pozor ak bude len 1 ako to funguje?
            for (currentPageNumber in 2..documentWithPageCountData.countOfPages) {

                val searchUrlForGivenPageNumberFuture: CompletableFuture<String> = CompletableFuture.supplyAsync(
                        // 1. vyskladam search url pre danu currentPageNumber
                        buildSearchUrlForGivenPageNumber(eshopParser, documentWithPageCountData, currentPageNumber), internalExecutor)


                val documentFuture: CompletableFuture<DocumentDataForGivenPageNumber> = searchUrlForGivenPageNumberFuture.thenApplyAsync(
                        // 2. zavolam preklopenie URL na Document
                        retrieveDocumentForUrlWithPageNumber(eshopParser, documentWithPageCountData.keyword, currentPageNumber), eshopExecutor)


                val productUrlListFuture: CompletableFuture<List<String>> = documentFuture.thenApplyAsync(
                        // 3. vyparsujem zoznam URL produktov
                        parseProductListURLs(eshopParser, currentPageNumber), internalExecutor
                )

                val resultOfAll = productUrlListFuture.thenAcceptAsync(
                        // 4. spracuj kazdu URL produktu
                        processPageUrls(eshopParser, eshopExecutor), internalExecutor)
            }
        }
    }

    private fun processPageUrls(eshopParser: EshopProductsParser, eshopExecutor: ScheduledExecutorService): Consumer<in List<String>> {
        return Consumer { productUrls ->

            productUrls.forEach { productUrl ->
                LOG.debug("processing product with URL $productUrl")

                // 1. vyskladam search url pre danu currentPageNumber
                val alreadyExistGivenProductFuture: CompletableFuture<Boolean> = CompletableFuture.supplyAsync(existProductWithGivenURL(productUrl), internalExecutor)

                alreadyExistGivenProductFuture.thenAcceptAsync(Consumer { exist ->
                    if (exist) {
                        LOG.debug("product $productUrl already existing")
                    } else {

                        // 2 na zaklade danej URL vyparsujem ProductNewData
                        val productNewDataFuture = CompletableFuture.supplyAsync(parseProductNewData(productUrl), eshopExecutor)
                        productNewDataFuture.handle { result, exception ->
                            if (exception != null) {
                                processExceptionDuringParsingProductNewData(exception)
                                null
                            } else {
                                processProductNewData(result)
                            }
                        }


                    }
                }, internalExecutor)


            }


        }
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun processProductNewData(result: ProductNewData): Nothing? {
        LOG.debug("processing new product ${result.name} a url ${result.url}")
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

    }

    private fun processExceptionDuringParsingProductNewData(exception: Throwable?) {
        LOG.error("processExceptionDuringParsingProductNewData", exception)
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

    }

    private fun parseProductNewData(productUrl: String): Supplier<ProductNewData> {
        return Supplier {
            htmlParser.parseProductNewData(productUrl)
        }
    }

    private fun existProductWithGivenURL(productURL: String): Supplier<Boolean> {
        return Supplier {
            internalTxService.existProductWithURL(productURL)
        }
    }

    private fun parseProductListURLs(eshopParser: EshopProductsParser,
                                     currentPageNumber: Int): Function<in DocumentDataForGivenPageNumber, List<String>> {

        return Function { documentDataForGivenPageNumber ->
            eshopParser.parseUrlsOfProduct(documentDataForGivenPageNumber.document, currentPageNumber)
        }
    }

    private fun retrieveDocumentForUrlWithPageNumber(eshopParser: EshopProductsParser, keyword: String, currentPageNumber: Int): Function<in String, out DocumentDataForGivenPageNumber> {
        return Function { searchUrl ->
            //TODO searchKeyWordId
            DocumentDataForGivenPageNumber(eshopParser.eshopUuid,
                    -1L,
                    keyword,
                    searchUrl,
                    currentPageNumber,
                    eshopParser.retrieveDocument(searchUrl)
            )
        }
    }


    private fun buildSearchUrlForGivenPageNumber(eshopParser: EshopProductsParser,
                                                 documentWithPageCountData: DocumentWithPageCountData,
                                                 currentPageNumber: Int): Supplier<String> {
        return Supplier {
            searchUrlBuilder.buildSearchUrl(eshopParser.eshopUuid, documentWithPageCountData.keyword, currentPageNumber)
        }
    }

    private fun findParserForEshop(eshopUuid: EshopUuid): EshopProductsParser {
        return productParsers.stream()
                .filter { it.eshopUuid == eshopUuid }
                .findFirst()
                .orElseThrow { EshopParserNotFoundException(eshopUuid) }
    }

    private fun retrieveKeywordBaseOnKeywordId(eshopKeywordIdData: EshopKeywordIdData): Supplier<EshopKeywordData> {
        return Supplier {
            val searchKeyword = internalTxService.getSearchKeywordById(eshopKeywordIdData.searchKeyWordId)
            //TODO log neaky?
            EshopKeywordData(eshopKeywordIdData, searchKeyword)
        }
    }


    private fun buildSearchUrlForKeyword(): Function<EshopKeywordData, SearchUrlWithKeywordData> {
        return Function { eshopKeywordData ->
            SearchUrlWithKeywordData(
                    eshopKeywordData,
                    searchUrlBuilder.buildSearchUrl(eshopKeywordData.eshopUuid, eshopKeywordData.searchKeyword)
            )
        }
    }

    private fun retrieveDocumentForUrl(eshopParser: EshopProductsParser): Function<SearchUrlWithKeywordData, DocumentData> {
        return Function { searchUrlWithKeywordData ->
            DocumentData(searchUrlWithKeywordData.eshopUuid,
                    searchUrlWithKeywordData.searchKeyWordId,
                    searchUrlWithKeywordData.searchKeyword,
                    searchUrlWithKeywordData.searchUrl,
                    eshopParser.retrieveDocument(searchUrlWithKeywordData.searchUrl)
            )
        }

    }

    private fun retrieveCountOfPages(eshopParser: EshopProductsParser): Function<DocumentData, DocumentWithPageCountData> {
        return Function { documentData ->
            DocumentWithPageCountData(
                    documentData.searchKeyword,
                    documentData.document,
                    eshopParser.eshopUuid,
                    eshopParser.parseCountOfPages(documentData.document))
        }
    }


}

@Deprecated("old")
class EshopScheduledExecutor(val eshopUuid: EshopUuid, threadFactory: ThreadFactory)
    : ScheduledThreadPoolExecutor(1, threadFactory) {

    companion object {
        val LOG = LoggerFactory.getLogger(EshopScheduledExecutor::class.java)!!
    }

    private val myLock = ReentrantLock()

    private var lastRunDate: Date? = null

    override fun execute(command: Runnable) {
        var countOfSecond: Long? = null

        LOG.debug("-> requesting lock for ${Thread.currentThread().name}")
        myLock.lock()
        LOG.debug("<- received lock for ${Thread.currentThread().name}")
        try {
            if (lastRunDate == null) {
                // spusti to hned
                LOG.info("scheduling command for $eshopUuid to be run now")
                // nastavim novy cas...
                lastRunDate = Date()
            } else {
                // vygenerum interval medzi <5,20> v sekundach
                val randomSecInterval = ThreadUtils.generateRandomSecondInInterval(5, 20).toLong()

                var dateTimeToRun = calculateDateTimeToRun(lastRunDate!!, randomSecInterval)
                countOfSecond = ChronoUnit.SECONDS.between(
                        Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime(),
                        dateTimeToRun.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime())

                LOG.info("scheduling command for $eshopUuid to be run after $countOfSecond at $dateTimeToRun")
                // nastavim novy cas...
                lastRunDate = dateTimeToRun
            }
        } finally {
            myLock.unlock()
            LOG.debug("received unlock for ${Thread.currentThread().name}")
        }

        if (countOfSecond != null) {
            LOG.trace("-> scheduling command")
            val schedule = super.schedule(command, countOfSecond, TimeUnit.SECONDS)
            LOG.trace("<- scheduling command")

        } else {
            LOG.trace("-> running command")
            super.execute(command)
            LOG.trace("<- running command")
        }
    }

    private fun calculateDateTimeToRun(lastRunDate: Date, countOfSecondToRun: Long): Date {
        val calendar = Calendar.getInstance()
        calendar.time = lastRunDate
        calendar.add(Calendar.SECOND, countOfSecondToRun.toInt())
        return calendar.time
    }

    override fun <T : Any?> submit(task: Callable<T>): Future<T> {
        throw PrcoRuntimeException("Not supported")
    }

    override fun <T : Any?> submit(task: Runnable, result: T): Future<T> {
        throw PrcoRuntimeException("Not supported")
    }

    override fun submit(task: Runnable): Future<*> {
        throw PrcoRuntimeException("Not suppoerted")
    }
}


open class EshopKeywordIdData(val eshopUuid: EshopUuid,
                              val searchKeyWordId: Long)

open class EshopKeywordData(eshopKeywordIdData: EshopKeywordIdData,
                            val searchKeyword: String)
    : EshopKeywordIdData(eshopKeywordIdData.eshopUuid, eshopKeywordIdData.searchKeyWordId)


open class SearchUrlWithKeywordData(eshopKeywordData: EshopKeywordData,
                                    var searchUrl: String)
    : EshopKeywordData(EshopKeywordIdData(eshopKeywordData.eshopUuid, eshopKeywordData.searchKeyWordId),
        eshopKeywordData.searchKeyword)

open class DocumentData(eshopUuid: EshopUuid,
                        searchKeyWordId: Long,
                        searchKeyword: String,
                        searchUrl: String,
                        val document: Document)
    : SearchUrlWithKeywordData(EshopKeywordData(EshopKeywordIdData(eshopUuid, searchKeyWordId), searchKeyword), searchUrl)


data class DocumentWithPageCountData(val keyword: String,
                                     val document: Document,
                                     val eshopUuid: EshopUuid,
                                     val countOfPages: Int)

open class DocumentDataForGivenPageNumber(eshopUuid: EshopUuid,
                                          searchKeyWordId: Long,
                                          searchKeyword: String,
                                          searchUrl: String,
                                          pageNumber: Int,
                                          val document: Document)

//            documentWithPageCountFuture.handle { result, exception ->
//                if (exception != null) {
//                    processRetrieveCountOfPagesException(exception)
//                    null
//                } else {
//                    processRetrieveCountOfPagesResult(result)
//                }
//            }

object ScheduledCompletable {

    fun <T> schedule(executor: ScheduledExecutorService, command: Supplier<T>, delay: Long, unit: TimeUnit): CompletableFuture<T> {
        val completableFuture = CompletableFuture<T>()
        executor.schedule({
            try {
                completableFuture.complete(command.get())

            } catch (t: Throwable) {
                completableFuture.completeExceptionally(t)
            }
        },
                delay,
                unit
        )
        return completableFuture
    }

    fun <T> scheduleAsync(executor: ScheduledExecutorService, command: Supplier<CompletableFuture<T>>, delay: Long, unit: TimeUnit): CompletableFuture<T> {
        val completableFuture = CompletableFuture<T>()
        executor.schedule(
                {
                    command.get()
                            .thenAccept { t ->
                                completableFuture.complete(t)
                            }
                            .exceptionally { t ->
                                completableFuture.completeExceptionally(t)
                                null
                            }
                },
                delay,
                unit
        )
        return completableFuture
    }
}