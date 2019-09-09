package sk.hudak.prco.manager.addprocess

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dto.product.NewProductCreateDto
import sk.hudak.prco.exception.*
import sk.hudak.prco.manager.error.ErrorLogManager
import sk.hudak.prco.mapper.PrcoOrikaMapper
import sk.hudak.prco.parser.eshop.EshopProductsParser
import sk.hudak.prco.parser.eshopuid.EshopUuidParser
import sk.hudak.prco.parser.html.HtmlParser
import sk.hudak.prco.service.InternalTxService
import sk.hudak.prco.task.EshopTaskManager
import sk.hudak.prco.task.ExceptionHandlingRunnable
import sk.hudak.prco.utils.ThreadUtils.sleepRandomSafe
import sk.hudak.prco.utils.ThreadUtils.sleepSafe
import sk.hudak.prco.utils.Validate.notEmpty
import java.util.*

@Component
class AddingNewProductManagerImpl(private val internalTxService: InternalTxService,
                                  private val mapper: PrcoOrikaMapper,
                                  private val htmlParser: HtmlParser,
                                  private val eshopTaskManager: EshopTaskManager,
                                  private val eshopUuidParser: EshopUuidParser,
                                  private val productsParsers: List<EshopProductsParser>,
                                  private val errorLogManager: ErrorLogManager)
    : AddingNewProductManager {

    companion object {
        val log = LoggerFactory.getLogger(AddingNewProductManagerImpl::class.java)!!
    }

    override fun addNewProductsByKeywordsForAllEshops(vararg searchKeyWords: String) {
        searchKeyWords.forEach {
            addNewProductsByKeywordForAllEshops(it)
        }
    }

    override fun addNewProductsByKeywordForAllEshops(searchKeyWord: String) {
        notEmpty(searchKeyWord, "searchKeyWord")

        EshopUuid.values()
                .filter {
                    existParserForEshop(it)
                }
                .forEach {
                    // spusti parsovanie 'eshop -> searchKeyWord'
                    addNewProductsByKeywordForEshop(it, searchKeyWord)
                    // pre kazdy dalsi eshop pockaj so spustenim 2 sekundy
                    sleepSafe(2)
                }
    }

    override fun addNewProductsByKeywordForEshop(eshopUuid: EshopUuid, searchKeyWord: String) {
        eshopTaskManager.submitTask(eshopUuid, object : ExceptionHandlingRunnable() {

            override fun doInRunnable() {
                log.debug(">> addNewProductsByKeywordForEshop eshop $eshopUuid, searchKeyWord $searchKeyWord")
                eshopTaskManager.markTaskAsRunning(eshopUuid)

                // vyparsujem vsetky url-cky produktov, ktore sa najdu na strankach(prechadza aj pageovane stranky)
                val urlList: List<String> = searchProductUrls(eshopUuid, searchKeyWord)
                log.debug("count of products URL ${urlList.size}")

                // filter only non existing
                val notExistingProducts = filterOnlyNotExistingWithException(urlList, eshopUuid)
                log.debug("count of non existing products URL  ${notExistingProducts.size}")
                log.debug("list of notexisting products URL $notExistingProducts")

                createNewProductsErrorWrapper(eshopUuid, notExistingProducts)

                eshopTaskManager.markTaskAsFinished(eshopUuid, false)
            }

            override fun handleException(e: Exception) {
                handleExceptionAddNewProducts(e, eshopUuid)
            }

            override fun doInFinally() {
                log.debug("<< addNewProductsByKeywordForEshop eshop $eshopUuid, searchKeyWord $searchKeyWord")
            }
        })
    }

    override fun addNewProductsByUrl(productsUrl: List<String>) {
        notEmpty(productsUrl, "productsUrl")
        log.debug(">> addNewProductsByUrl count of URLs: ${productsUrl.size}")

        // filtrujem len tie ktore este neexistuju
        val notExistingProducts = filterOnlyNotExisting(productsUrl)
        log.debug("count of non existing URLs ${notExistingProducts.size}")
        if (notExistingProducts.isNullOrEmpty()) {
            log.debug("<< addNewProductsByUrl count of URLs: ${productsUrl.size}")
            return
        }

        // roztriedim URL podla typu eshopu
        val eshopUrls: EnumMap<EshopUuid, MutableList<String>> = convertToUrlsByEshop(notExistingProducts)

        // spustim parsovanie pre kazdy eshop
        for ((eshopUuid, productUrlList) in eshopUrls) {

            eshopTaskManager.submitTask(eshopUuid, object : ExceptionHandlingRunnable() {

                override fun doInRunnable() {
                    log.debug(">> addNewProductsByUrlsForEshop eshop $eshopUuid")
                    eshopTaskManager.markTaskAsRunning(eshopUuid)

                    createNewProductsErrorWrapper(eshopUuid, productUrlList!!)

                    eshopTaskManager.markTaskAsFinished(eshopUuid, false)
                }

                override fun handleException(e: Exception) {
                    handleExceptionAddNewProducts(e, eshopUuid)
                }

                override fun doInFinally() {
                    log.debug("<< addNewProductsByUrlsForEshop eshop $eshopUuid")
                }

            })

            // kazdy dalsi spusti s 2 sekundovym oneskorenim
            sleepSafe(2)
        }



        log.debug("<< addNewProductsByUrl count of URLs: ${productsUrl.size}")
    }

    private fun createNewProductsErrorWrapper(eshopUuid: EshopUuid, urlList: List<String>) {
        try {
            createNewProducts(eshopUuid, urlList)

        } catch (e: Exception) {
            throw CreateNewProductsForUrlsException(eshopUuid, e)
        }
    }

    private enum class ContinueStatus {
        CONTINUE_TO_NEXT_ONE_OK,
        CONTINUE_TO_NEXT_ONE_ERROR,
        STOP_PROCESSING_NEXT_ONE
    }

    private fun createNewProducts(eshopUuid: EshopUuid, urlList: List<String>) {
        loop@
        for (currentUrlIndex in urlList.indices) {
            log.debug("starting {} of {}", currentUrlIndex + 1, urlList.size)

            val processNewProductUrl = processNewProductUrl(eshopUuid, urlList[currentUrlIndex])

            when (processNewProductUrl) {
                ContinueStatus.STOP_PROCESSING_NEXT_ONE -> {
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
            log.error(e.message, e)
            errorLogManager.logErrorParsingProductNewData(eshopUuid, e)
            return when (e) {
                is EshopNotFoundParserException,
                is EshopParserNotFoundException -> {
                    ContinueStatus.STOP_PROCESSING_NEXT_ONE
                }
                is ProductNotFoundHttpParserException,
                is HttpStatusParserException,
                is HttpSocketTimeoutParserException,
                is DefaultHttpParserException -> {
                    ContinueStatus.CONTINUE_TO_NEXT_ONE_ERROR
                }
                else -> {
                    ContinueStatus.CONTINUE_TO_NEXT_ONE_ERROR
                }
            }
        }

        if (null == productNewData.name) {
            errorLogManager.logErrorParsingProductNameForNewProduct(eshopUuid, productUrl)
            log.warn("new product not contains name, skipping to next product")
            return ContinueStatus.CONTINUE_TO_NEXT_ONE_ERROR
        }

        // preklopim a pridavam do DB
        internalTxService.createNewProduct(mapper.map(productNewData, NewProductCreateDto::class.java))
        return ContinueStatus.CONTINUE_TO_NEXT_ONE_OK
    }

    private fun searchProductUrls(eshopUuid: EshopUuid, searchKeyWord: String): List<String> {
        val urlList = try {
            // vyparsujem vsetky url-cky produktov, ktore sa najdu na strankach(prechadza aj pageovane stranky)
            htmlParser.searchProductUrls(eshopUuid, searchKeyWord)

        } catch (e: Exception) {
            throw SearchProductUrlsException(eshopUuid, searchKeyWord, e)
        }

        if (urlList.isEmpty()) {
            log.debug("none url found for eshop $eshopUuid and keyword $searchKeyWord")
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
        return internalFilterOnlyNonExistingUrls(productsUrl)
    }

    /**
     * @param productsUrl list of product URL for given eshop
     * @param eshopUuid eshop to which this URLs belongs to
     */
    private fun filterOnlyNotExistingWithException(productsUrl: List<String>, eshopUuid: EshopUuid): List<String> {
        val notExistingProducts = internalFilterOnlyNonExistingUrls(productsUrl)

        if (notExistingProducts.isEmpty()) {
            throw AllProductsWithGivenUrlsAlreadyExistingException(eshopUuid)
        }
        return notExistingProducts
    }

    private fun internalFilterOnlyNonExistingUrls(productsUrl: List<String>): List<String> {
        val notExistingProducts = productsUrl.filter {
            val exist = internalTxService.existProductWithURL(it)
            if (exist) {
                log.debug("product $it already existing")
            }
            !exist
        }

        if (notExistingProducts.isEmpty()) {
            log.debug("count of non existing products URL is zero")
        }
        return notExistingProducts
    }

    private fun existParserForEshop(eshopUuid: EshopUuid): Boolean {
        val parser = productsParsers.find {
            eshopUuid == it.eshopUuid
        }
        if (parser == null) {
            log.warn("for eshop $eshopUuid none parser found")
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
                log.error(e.message, e)
                eshopTaskManager.markTaskAsFinished(e.eshopUuid, true)
                errorLogManager.logErrorParsingProductUrls(e.eshopUuid, e.searchKeyWord, e)
            }

            is NoProductUrlsFoundFondForKeywordException -> {
                log.info(e.message)
                eshopTaskManager.markTaskAsFinished(e.eshopUuid, false)
            }

            is AllProductsWithGivenUrlsAlreadyExistingException -> {
                log.info(e.message)
                eshopTaskManager.markTaskAsFinished(e.eshopUuid, false)
            }

            is CreateNewProductsForUrlsException -> {
                log.error(e.message, e)
                eshopTaskManager.markTaskAsFinished(e.eshopUuid, true)
                errorLogManager.logErrorParsingProductNewData(e.eshopUuid, e)
            }

            else -> {
                log.error(e.message, e)
                eshopTaskManager.markTaskAsFinished(eshopUuid, true)
            }
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