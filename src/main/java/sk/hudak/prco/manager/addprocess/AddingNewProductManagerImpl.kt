package sk.hudak.prco.manager.addprocess

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.ErrorType
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dto.ErrorCreateDto
import sk.hudak.prco.dto.product.NewProductCreateDto
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.events.PrcoObserver
import sk.hudak.prco.exception.*
import sk.hudak.prco.manager.error.ErrorLogManager
import sk.hudak.prco.mapper.PrcoOrikaMapper
import sk.hudak.prco.parser.eshop.EshopProductsParser
import sk.hudak.prco.parser.eshopuid.EshopUuidParser
import sk.hudak.prco.parser.html.HtmlParser
import sk.hudak.prco.service.InternalTxService
import sk.hudak.prco.task.EshopTaskManager
import sk.hudak.prco.task.ExceptionHandlingRunnable
import sk.hudak.prco.task.SingleContext
import sk.hudak.prco.utils.ConsoleColor
import sk.hudak.prco.utils.ConsoleWithColor.wrapWithColor
import sk.hudak.prco.utils.ThreadUtils.sleepRandomSafe
import sk.hudak.prco.utils.ThreadUtils.sleepSafe
import sk.hudak.prco.utils.Validate.notEmpty
import java.util.*

interface AddingNewProductManager {

    /**
     * @param productsUrl list of new product URL's
     */
    fun addNewProductsByUrl(productsUrl: List<String>)

    /**
     * Vyhlada produkty s danym klucovym slovom pre konkretny eshop a ulozi ich do tabulky NEW_PRODUCT.
     *
     * @param eshopUuid     eshop identifikator
     * @param searchKeyWordId use SearchKeyWordId
     */
    fun addNewProductsByKeywordForEshop(eshopUuid: EshopUuid, searchKeyWordId: Long)

    /**
     * Vyhlada produkty s danym klucovym slovom(id) pre vsetky eshopy a ulozi ich do tabulky NEW_PRODUCT.
     */
    fun addNewProductsByKeywordForAllEshops(searchKeyWordId: Long)

    /**
     * @param searchKeyWords search key words
     */
    fun addNewProductsByKeywordsForAllEshops(vararg searchKeyWordIds: Long)

    fun addNewProductsByConfiguredKeywordsForAllEshops()
}

@Component
class AddingNewProductManagerImpl(private val internalTxService: InternalTxService,
                                  private val mapper: PrcoOrikaMapper,
                                  private val htmlParser: HtmlParser,
                                  private val eshopTaskManager: EshopTaskManager,
                                  private val eshopUuidParser: EshopUuidParser,
                                  private val productsParsers: List<EshopProductsParser>,
                                  private val errorLogManager: ErrorLogManager,
                                  private val prcoObservable: PrcoObservable)
    : AddingNewProductManager, PrcoObserver {

    companion object {
        val LOG = LoggerFactory.getLogger(AddingNewProductManagerImpl::class.java)!!

        private const val CTX_ESHOP_UUID_KEY = "eshopUuid"
        private const val CTX_SEARCH_KEYWORD_KEY = "searchKeyWord"
        private const val CTX_COUNT_OF_PROCESSED = "countOfProcessed"
        private const val CTX_COUNT_OF_FOUND_PRODUCT_URLS_KEY = "countOfFoundProductUrls"
        private const val CTX_COUNT_OF_FOUND_PRODUCT_URLS_WITH_DUPLICITY_KEY = "countOfFoundProductUrlsWithDuplicity"
    }

    // registering itself as observer
    init {
        prcoObservable.addObserver(this)
    }

    // handling events produced by observable
    override fun update(source: Observable?, event: CoreEvent) {
        when (event) {
            is AddProductsToEshopByKeywordFinishedEvent -> {
                //TODO do osobitnej tabulky ukladat historicke informacie
            }
        }
    }

    override fun addNewProductsByKeywordsForAllEshops(vararg searchKeyWordIds: Long) {
        searchKeyWordIds.forEach {
            addNewProductsByKeywordForAllEshops(it)
        }
    }

    override fun addNewProductsByKeywordForAllEshops(searchKeyWordId: Long) {
        EshopUuid.values()
                .filter {
                    existParserForEshop(it)
                }
                .forEach {
                    // spusti parsovanie 'eshop -> searchKeyWordId'
                    addNewProductsByKeywordForEshop(it, searchKeyWordId)
                    // pre kazdy dalsi eshop pockaj so spustenim 2 sekundy
                    sleepSafe(2)
                }
    }

    override fun addNewProductsByConfiguredKeywordsForAllEshops() {
        EshopUuid.values()
                .filter { existParserForEshop(it) }
                .forEach {
                    val supportedSearchKeywordIds = it.config.supportedSearchKeywordIds
                    LOG.debug("eshop $it has configured ${supportedSearchKeywordIds.size} search keywords")
                    supportedSearchKeywordIds.forEach { searchKeywordId ->
                        // spusti parsovanie 'eshop -> searchKeyWordId'
                        addNewProductsByKeywordForEshop(it, searchKeywordId);
                    }
                    // pre kazdy dalsi eshop pockaj so spustenim 2 sekundy
                    sleepSafe(2)
                }
    }

    override fun addNewProductsByKeywordForEshop(eshopUuid: EshopUuid, searchKeyWordId: Long) {
        eshopTaskManager.submitTask(eshopUuid, object : ExceptionHandlingRunnable() {

            override fun doInRunnable(context: SingleContext) {
                LOG.debug(">> addNewProductsByKeywordForEshop eshop $eshopUuid, searchKeyWordId $searchKeyWordId")
                eshopTaskManager.markTaskAsRunning(eshopUuid)
                context.addValue(CTX_ESHOP_UUID_KEY, eshopUuid)

                // na zaklade id nacitam samotne klucove slovo
                val searchKeyWord: String = internalTxService.getSearchKeywordById(searchKeyWordId)
                LOG.debug("searchKeyWord $searchKeyWord")
                context.addValue(CTX_SEARCH_KEYWORD_KEY, searchKeyWord)

                // check ci je podporovane
                val config = eshopUuid.config
                if (config.supportedSearchKeywordIds.isEmpty()) {
                    LOG.warn(wrapWithColor("eshop config $eshopUuid has none supported keywords", ConsoleColor.RED))
                } else {
                    if (!config.supportedSearchKeywordIds.contains(searchKeyWordId)) {
                        LOG.warn("searchKeyWord $searchKeyWord with id $searchKeyWordId is not supported for eshop $eshopUuid")
                        //TODO refaktor metod doInRunnuble, prerobit nasledovne:
                        // ak dana metoda skonci a nenastavila do kontextu fiishedWithError true tak skoncila ok a
                        // ja mozem hore oznacit dany task za uspesne ukonceny... to je koli tomu aby tieto doInRunnable metody
                        // nemuseli mat na starosti oznacenie tasku ze bezi a potom aj jeho ukoncenie !!!
                        eshopTaskManager.markTaskAsFinished(eshopUuid, false)
                        return
                    }
                }

                // vyparsujem vsetky url-cky produktov, ktore sa najdu na strankach(prechadza aj pageovane stranky)
                var urlList: List<String> = searchProductUrls(context, eshopUuid, searchKeyWord)
                LOG.debug("count of products URL ${urlList.size}")

                urlList = filterDuplicity(context, eshopUuid, searchKeyWord, urlList)
                LOG.debug("count of products URL after duplicity check ${urlList.size}")

                // filter only non existing
                val notExistingProducts = filterOnlyNotExistingWithException(context, urlList, eshopUuid)
                LOG.debug("count of non existing products URL  ${notExistingProducts.size}")

                createNewProductsErrorWrapper(context, eshopUuid, notExistingProducts)

                eshopTaskManager.markTaskAsFinished(eshopUuid, false)
            }

            override fun handleException(context: SingleContext, e: Exception) {
                handleExceptionAddNewProducts(e, eshopUuid)
            }

            override fun doInFinally(context: SingleContext) {
                LOG.debug("<< addNewProductsByKeywordForEshop eshop $eshopUuid, searchKeyWordId $searchKeyWordId")
                notifyFinishProcessingKeywordForEshop(context)
            }
        })
    }

    override fun addNewProductsByUrl(productsUrl: List<String>) {
        notEmpty(productsUrl, "productsUrl")
        LOG.debug(">> addNewProductsByUrl count of URLs: ${productsUrl.size}")

        // filtrujem len tie ktore este neexistuju
        val notExistingProducts = filterOnlyNotExisting(productsUrl)
        if (notExistingProducts.isNullOrEmpty()) {
            LOG.debug("<< addNewProductsByUrl count of URLs: ${productsUrl.size}")
            return
        }

        // roztriedim URL podla typu eshopu
        for ((eshopUuid, productUrlList) in convertToUrlsByEshop(notExistingProducts)) {

            // spustim parsovanie pre kazdy eshop
            eshopTaskManager.submitTask(eshopUuid, object : ExceptionHandlingRunnable() {

                override fun doInRunnable(context: SingleContext) {
                    LOG.debug(">> addNewProductsByUrlsForEshop eshop $eshopUuid")
                    eshopTaskManager.markTaskAsRunning(eshopUuid)

                    createNewProductsErrorWrapper(context, eshopUuid, productUrlList!!)

                    eshopTaskManager.markTaskAsFinished(eshopUuid, false)
                }

                override fun handleException(context: SingleContext, e: Exception) {
                    handleExceptionAddNewProducts(e, eshopUuid)
                }

                override fun doInFinally(context: SingleContext) {
                    LOG.debug("<< addNewProductsByUrlsForEshop eshop $eshopUuid")
                    //TODO notify
                }
            })
            // kazdy dalsi spusti s 2 sekundovym oneskorenim
            sleepSafe(2)
        }
        LOG.debug("<< addNewProductsByUrl count of URLs: ${productsUrl.size}")
    }


    private fun filterDuplicity(context: SingleContext, eshopUuid: EshopUuid, searchKeyWord: String, urlList: List<String>): List<String> {
        // key: productUrl, value: count of duplicity
        var result = mutableMapOf<String, Int>()
        urlList.forEach {
            var entry = result[it]
            if (entry == null) {
                result[it] = 1
            } else {
                entry++
                result[it] = entry
            }
        }

        result.forEach { (key, value) ->
            if (value != 1) {
                LOG.error("product with URL $key is more than one, count: $value")
                errorLogManager.logErrorDuplicityDuringfindinUrlOfProducts(eshopUuid, searchKeyWord, key)
            }
        }

        val toList = result.keys.toList()

        context.addValue(CTX_COUNT_OF_FOUND_PRODUCT_URLS_WITH_DUPLICITY_KEY, (toList.size - urlList.size))
        return toList
    }

    private fun notifyFinishProcessingKeywordForEshop(context: SingleContext) {
        //TODO impl v osobitnom thread-e
        try {
            val countOfFound = context.values[CTX_COUNT_OF_FOUND_PRODUCT_URLS_KEY]
            val countOfProcessed = context.values[CTX_COUNT_OF_PROCESSED]
            prcoObservable.notify(AddProductsToEshopByKeywordFinishedEvent(eshopUuid = context.values[CTX_ESHOP_UUID_KEY] as EshopUuid,
                    keyword = context.values[CTX_SEARCH_KEYWORD_KEY] as String,
                    error = context.error,
                    errMsg = context.errMsg,
                    countOfFound = if (countOfFound is Int) countOfFound else 0,
                    countOfAdded = if (countOfProcessed is Int) countOfProcessed else 0))
        } catch (e: Exception) {
            LOG.error("error while notifying observers", e)
        }
    }

    private fun createNewProductsErrorWrapper(context: SingleContext, eshopUuid: EshopUuid, urlList: List<String>) {
        try {
            createNewProducts(context, eshopUuid, urlList)

        } catch (e: Exception) {
            throw CreateNewProductsForUrlsException(eshopUuid, e)
        }
    }

    private enum class ContinueStatus {
        CONTINUE_TO_NEXT_ONE_OK,
        CONTINUE_TO_NEXT_ONE_ERROR,
        STOP_PROCESSING_NEXT_ONE
    }

    private fun createNewProducts(context: SingleContext, eshopUuid: EshopUuid, urlList: List<String>) {
        loop@
        for (currentUrlIndex in urlList.indices) {
            LOG.debug("starting {} of {}", currentUrlIndex + 1, urlList.size)

            val processNewProductUrl = processNewProductUrl(eshopUuid, urlList[currentUrlIndex])

            when (processNewProductUrl) {
                ContinueStatus.STOP_PROCESSING_NEXT_ONE -> {
                    context.addValue(CTX_COUNT_OF_PROCESSED, currentUrlIndex + 1)
                    break@loop
                }
                ContinueStatus.CONTINUE_TO_NEXT_ONE_OK,
                ContinueStatus.CONTINUE_TO_NEXT_ONE_ERROR -> {
                    // sleep pre dalsou iteraciou, iba ak aktualne nie je zaroven posledny
                    if (currentUrlIndex + 1 != urlList.size) {
                        sleepRandomSafe()
                    }
                }
            }
        }

        if (!context.existValueForKey(CTX_COUNT_OF_PROCESSED)) {
            context.addValue(CTX_COUNT_OF_PROCESSED, urlList.size)
        }
    }

    private fun processNewProductUrl(eshopUuid: EshopUuid, productUrl: String): ContinueStatus {
        if (eshopTaskManager.isTaskShouldStopped(eshopUuid)) {
            eshopTaskManager.markTaskAsStopped(eshopUuid)
            return ContinueStatus.STOP_PROCESSING_NEXT_ONE
        }

        // parsujem
        val productNewData = try {
            htmlParser.parseProductNewData(productUrl)

        } catch (e: Exception) {
            LOG.error(e.message, e)
            errorLogManager.logErrorParsingProductNewData(eshopUuid, e)
            return when (e) {
                is EshopNotFoundParserException,
                is EshopParserNotFoundException -> {
                    ContinueStatus.STOP_PROCESSING_NEXT_ONE
                }
                is ProductPageNotFoundHttpParserException,
                is HttpStatusParserException,
                is HttpSocketTimeoutParserException,
                is CoreParserException -> {
                    ContinueStatus.CONTINUE_TO_NEXT_ONE_ERROR
                }
                else -> {
                    ContinueStatus.CONTINUE_TO_NEXT_ONE_ERROR
                }
            }
        }

        if (null == productNewData.name) {
            errorLogManager.logErrorParsingProductNameForNewProduct(eshopUuid, productUrl)
            LOG.warn("new product not contains name, skipping to next product")
            return ContinueStatus.CONTINUE_TO_NEXT_ONE_ERROR
        }

        // preklopim a pridavam do DB
        internalTxService.createNewProduct(mapper.map(productNewData, NewProductCreateDto::class.java))
        return ContinueStatus.CONTINUE_TO_NEXT_ONE_OK
    }

    private fun searchProductUrls(context: SingleContext, eshopUuid: EshopUuid, searchKeyWord: String): List<String> {
        val urlList = try {
            // vyparsujem vsetky url-cky produktov, ktore sa najdu na strankach(prechadza aj pageovane stranky)
            htmlParser.searchProductUrls(eshopUuid, searchKeyWord)

        } catch (e: Exception) {
            throw SearchProductUrlsException(eshopUuid, searchKeyWord, e)
        }
        context.addValue("countOfFoundProductUrls", urlList.size)
        if (urlList.isEmpty()) {
            LOG.debug("none url found for eshop $eshopUuid and keyword $searchKeyWord")
            throw NoProductUrlsFoundFondForKeywordException(eshopUuid, searchKeyWord)
        }
        return urlList
    }


    private fun convertToUrlsByEshop(productsUrl: List<String>): EnumMap<EshopUuid, MutableList<String>> {
        //FIXME zmenit MutableList na List navratovu hodnotu
        val eshopUrls: EnumMap<EshopUuid, MutableList<String>> = EnumMap(EshopUuid::class.java)

        productsUrl.forEach {
            eshopUrls.computeIfAbsent(eshopUuidParser.parseEshopUuid(it)) {
                ArrayList()
            }.add(it)
        }
        return eshopUrls
    }

    private fun filterOnlyNotExisting(productsUrl: List<String>): List<String> {
        val notExistingProducts = internalFilterOnlyNonExistingUrls(productsUrl)
        LOG.debug("count of non existing URLs ${notExistingProducts.size}")
        return notExistingProducts
    }

    /**
     * @param productsUrl list of product URL for given eshop
     * @param eshopUuid eshop to which this URLs belongs to
     */
    private fun filterOnlyNotExistingWithException(context: SingleContext, productsUrl: List<String>, eshopUuid: EshopUuid): List<String> {
        val notExistingProducts = internalFilterOnlyNonExistingUrls(productsUrl)

        context.addValue("countOfNonExistingProductUrls", notExistingProducts.size)
        if (notExistingProducts.isEmpty()) {
            throw AllProductsWithGivenUrlsAlreadyExistingException(eshopUuid)
        }
        return notExistingProducts
    }

    private fun internalFilterOnlyNonExistingUrls(productsUrl: List<String>): List<String> {
        val notExistingProducts = productsUrl.filter {
            val exist = internalTxService.existProductWithURL(it)
            if (exist) {
                LOG.debug("product $it already existing")
            }
            !exist
        }

        if (notExistingProducts.isEmpty()) {
            LOG.debug("count of non existing products URL is zero")
        }
        return notExistingProducts
    }

    private fun existParserForEshop(eshopUuid: EshopUuid): Boolean {
        val parser = productsParsers.find {
            eshopUuid == it.eshopUuid
        }
        if (parser == null) {
            LOG.warn("for eshop $eshopUuid none parser found")
            return false
        }
        return true
    }

    private fun handleExceptionAddNewProducts(e: Exception, eshopUuid: EshopUuid) {
        //TODO prejst realne ze ktore sa pouzivaju !!!!!
        when (e) {
//            is ExecutionException -> {
//                log.error("it is ExecutionException ${e.message} type ${e.cause?.javaClass?.simpleName}")
//                handleAddNewProductsByKeywordForEshopException(e.cause as Exception, eshopUuid, searchKeyWord)
//            }

            is SearchProductUrlsException -> {
                LOG.error(e.message, e)
                eshopTaskManager.markTaskAsFinished(e.eshopUuid, true)
                errorLogManager.logErrorParsingProductUrls(e.eshopUuid, e.searchKeyWord, e)
            }

            is NoProductUrlsFoundFondForKeywordException -> {
                LOG.info(e.message)
                eshopTaskManager.markTaskAsFinished(e.eshopUuid, false)
            }

            is AllProductsWithGivenUrlsAlreadyExistingException -> {
                LOG.info(e.message)
                eshopTaskManager.markTaskAsFinished(e.eshopUuid, false)
            }

            is CreateNewProductsForUrlsException -> {
                LOG.error(e.message, e)
                eshopTaskManager.markTaskAsFinished(e.eshopUuid, true)
                errorLogManager.logErrorParsingProductNewData(e.eshopUuid, e)
            }

            else -> {
                LOG.error(e.message, e)
                eshopTaskManager.markTaskAsFinished(eshopUuid, true)
            }
        }
    }

}

abstract class AddProcessEvent(val eshopUuid: EshopUuid) : CoreEvent() {
    override fun toString(): String {
        return "AddProcessEvent(eshopUuid=$eshopUuid)"
    }
}

class AddProductsToEshopByKeywordFinishedEvent(eshopUuid: EshopUuid,
                                               val keyword: String,
                                               val error: Boolean,
                                               val errMsg: String?,
                                               val countOfFound: Int?,
                                               val countOfAdded: Int?)
    : AddProcessEvent(eshopUuid) {

    override fun toString(): String {
        return "EshopKeywordFinishEvent(" +
                "eshopUuid=$eshopUuid, " +
                "keyword='$keyword', " +
                "error=$error, " +
                "errMsg=$errMsg, " +
                "countOfFound=$countOfFound, " +
                "countOfAdded=$countOfAdded)"
    }
}

/**
 * Ulozi chybu ak pri parsovani novych produktov sa pre dany eshop a dane klucove slovo nenasiel ani jeden produkt
 */
@Component
class ErrorNoProductFoundByKeyWordObservable(prcoObservable: PrcoObservable,
                                             private val internalTxService: InternalTxService) : PrcoObserver {

    init {
        prcoObservable.addObserver(this)
    }

    override fun update(source: Observable?, event: CoreEvent) {
        when (event) {
            is AddProductsToEshopByKeywordFinishedEvent -> {
                processEshopKeywordFinishEvent(event)
            }
        }
    }

    private fun processEshopKeywordFinishEvent(event: AddProductsToEshopByKeywordFinishedEvent) {
        if (event.countOfFound == 0) {
            internalTxService.createError(ErrorCreateDto(
                    event.eshopUuid,
                    ErrorType.UNKNOWN, null,
                    "no url found for eshop ${event.eshopUuid} and searchKeyWord ${event.keyword}",
                    null, null,
                    event.keyword))
        }
    }
}

class SearchProductUrlsException(val eshopUuid: EshopUuid, val searchKeyWord: String, e: Exception) :
        PrcoRuntimeException("error while parsing eshop $eshopUuid products URLs for keyword $searchKeyWord", e)

class NoProductUrlsFoundFondForKeywordException(val eshopUuid: EshopUuid, searchKeyWord: String) :
        PrcoRuntimeException("no url found for eshop $eshopUuid and searchKeyWord $searchKeyWord")

class AllProductsWithGivenUrlsAlreadyExistingException(val eshopUuid: EshopUuid) :
        PrcoRuntimeException("none non existing url found for eshop $eshopUuid")

class CreateNewProductsForUrlsException(val eshopUuid: EshopUuid, e: Exception) :
        PrcoRuntimeException("error while creating new product from URL for eshop $eshopUuid", e)

