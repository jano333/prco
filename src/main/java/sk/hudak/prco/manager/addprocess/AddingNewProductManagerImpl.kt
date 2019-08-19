package sk.hudak.prco.manager.addprocess

import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.ErrorType
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dto.ErrorCreateDto
import sk.hudak.prco.dto.product.NewProductCreateDto
import sk.hudak.prco.exception.PrcoRuntimeException
import sk.hudak.prco.mapper.PrcoOrikaMapper
import sk.hudak.prco.parser.eshop.EshopProductsParser
import sk.hudak.prco.parser.eshopuid.EshopUuidParser
import sk.hudak.prco.parser.html.HtmlParser
import sk.hudak.prco.service.InternalTxService
import sk.hudak.prco.task.EshopTaskManager
import sk.hudak.prco.task.ExceptionHandlingRunnable
import sk.hudak.prco.utils.ThreadUtils
import sk.hudak.prco.utils.Validate.notEmpty
import sk.hudak.prco.utils.Validate.notNullNotEmpty
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier
import kotlin.collections.ArrayList


@Component
class AddingNewProductManagerImpl(private val internalTxService: InternalTxService,
                                  private val mapper: PrcoOrikaMapper,
                                  private val htmlParser: HtmlParser,
                                  private val eshopTaskManager: EshopTaskManager,
                                  private val eshopUuidParser: EshopUuidParser,
                                  private val productsParsers: List<EshopProductsParser>
) : AddingNewProductManager {

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
                    existParserFor(it)
                }
                .forEach {
                    // spusti parsovanie 'eshop -> searchKeyWord'
                    addNewProductsByKeywordForEshop(it, searchKeyWord)
                    // kazdy dalsi spusti s 1 sekundovym oneskorenim
                    // TODO z konfigu
                    ThreadUtils.sleepSafe(1)
                }
    }

    override fun addNewProductsByUrl(productsUrl: List<String>) {
        notNullNotEmpty(productsUrl as Array<String>, "productsUrl")

        log.debug(">> addNewProductsByUrl count of URLs: ${productsUrl.size}")

        // filtrujem len tie ktore este neexistuju
        val notExistingProducts = filterOnlyNotExisting(productsUrl)

        if (notExistingProducts.isNotEmpty()) {
            // roztriedim URL podla typu eshopu
            val eshopUrls: EnumMap<EshopUuid, MutableList<String>> = convertToUrlsByEshop(notExistingProducts)

            // spustim parsovanie pre kazdy eshop
            eshopUrls.keys.forEach {
                createNewProductsForEshop(it, eshopUrls[it]!!.toList())
            }
        }
        log.debug("<< addNewProductsByUrl count of URLs: ${productsUrl.size}")
    }

    override fun addNewProductsByKeywordForEshop(eshopUuid: EshopUuid, searchKeyWord: String) {
        notEmpty(searchKeyWord, "searchKeyWord")

        eshopTaskManager.submitTask(eshopUuid, object : ExceptionHandlingRunnable() {

            override fun doInRunnable() {
                log.debug(">> addNewProductsByKeywordForEshop eshop $eshopUuid, searchKeyWord $searchKeyWord")
                eshopTaskManager.markTaskAsRunning(eshopUuid)

                // vyparsujem vsetky url-cky produktov, ktore sa najdu na strankach(prechadza aj pageovane stranky)
                val urlList: List<String> = searchProductUrlsWrapper(eshopUuid, searchKeyWord)
                log.debug("count of products URL ${urlList.size}")
                // if none url found -> end
                if (urlList.isEmpty()) {
                    throw NoProductUrlsFoundFondForKeyword(eshopUuid, searchKeyWord)
                }
                // filter only non existing
                val notExistingProducts = filterOnlyNotExisting(urlList)
                log.debug("filter only to non existing count ${notExistingProducts.size}")
                if (notExistingProducts.isEmpty()) {
                    eshopTaskManager.markTaskAsFinished(eshopUuid, false)
                    return
                }

                createNewProductsErrorWrapper(eshopUuid, notExistingProducts)

                eshopTaskManager.markTaskAsFinished(eshopUuid, false)
            }

            override fun handleException(e: Exception) {
                handleAddNewProductsByKeywordForEshopException(e, eshopUuid)
            }

            override fun doInFinally() {
                log.debug("<< addNewProductsByKeywordForEshop eshop $eshopUuid, searchKeyWord $searchKeyWord")
            }
        })
    }

    private fun createNewProductsForEshop(eshopUuid: EshopUuid, urlList: List<String>) {

        eshopTaskManager.submitTask(eshopUuid, object : ExceptionHandlingRunnable() {

            override fun doInRunnable() {
                log.debug(">> addNewProductsByKeywordForEshop eshop $eshopUuid")
                eshopTaskManager.markTaskAsRunning(eshopUuid)

                createNewProductsErrorWrapper(eshopUuid, urlList)

                eshopTaskManager.markTaskAsFinished(eshopUuid, false)
            }

            override fun handleException(e: Exception) {
                handleAddNewProductsByKeywordForEshopException(e, eshopUuid)
            }

            override fun doInFinally() {
                log.debug("<< addNewProductsByKeywordForEshop eshop $eshopUuid")
            }
        }
        )
    }

    private fun createNewProductsErrorWrapper(eshopUuid: EshopUuid, urlList: List<String>) {
        try {
            createNewProducts(eshopUuid, urlList)

        } catch (e: Exception) {
            throw CreateNewProductsForUrls(eshopUuid, e)
        }
    }

    private fun createNewProducts(eshopUuid: EshopUuid, urlList: List<String>) {
        val allUrlCount = urlList.size


        for (currentUrlIndex in 0 until allUrlCount) {

            if (eshopTaskManager.isTaskShouldStopped(eshopUuid)) {
                eshopTaskManager.markTaskAsStopped(eshopUuid)
                break
            }

            log.debug("starting {} of {}", currentUrlIndex + 1, allUrlCount)
            val productUrl = urlList[currentUrlIndex]

            // parsujem
            val productNewData = htmlParser.parseProductNewData(productUrl)
            //TODO pridat kontrolu na dostupnost proudku, alza nebol dostupny preto nevrati mene.... a padne toto

            // je len tmp fix
            if (null == productNewData.name) {
                //TODO log do error logu? asi ano
                log.warn("new product not contains name, skipping to next product")
                continue
            }
            // rusim logovanie unit, lebo to moze byt produkt ktory to ani nema, teda ani ma nezaujima...
            //            if (productNewData.getUnit() == null) {
            //                logErrorParsingUnit(eshopUuid, productUrl, productNewData.getName().get());
            //            }

            // preklopim a pridavam do DB
            internalTxService.createNewProduct(mapper.map(productNewData, NewProductCreateDto::class.java))

            // sleep pre dalsou iteraciou
            //TODO fix na zaklade nastavenia daneho eshopu.... dave od to delay
            ThreadUtils.sleepRandomSafe()
        }
    }

    private fun convertToUrlsByEshop(productsUrl: List<String>): EnumMap<EshopUuid, MutableList<String>> {
        val eshopUrls: EnumMap<EshopUuid, MutableList<String>> = EnumMap(EshopUuid::class.java)

        productsUrl.forEach {
            eshopUrls.computeIfAbsent(eshopUuidParser.parseEshopUuid(it)) {
                ArrayList()
            }.add(it)
        }
        return eshopUrls
    }

    private fun handleAddNewProductsByKeywordForEshopException(e: Exception, eshopUuid: EshopUuid) {
        when (e) {
            is SearchProductUrlsException -> {
                log.error(e.message, e)
                eshopTaskManager.markTaskAsFinished(e.eshopUuid, true)
                logErrorParsingProductUrls(e.eshopUuid, e.searchKeyWord, e)
            }

            is NoProductUrlsFoundFondForKeyword -> {
                log.info(e.message)
                eshopTaskManager.markTaskAsFinished(e.eshopUuid, false)
            }

            is CreateNewProductsForUrls -> {
                log.error(e.message, e)
                eshopTaskManager.markTaskAsFinished(e.eshopUuid, true)
                logErrorParsingProductNewData(e.eshopUuid, e)
            }

            else -> {
                log.error(e.message, e)
                eshopTaskManager.markTaskAsFinished(eshopUuid, true)
            }
        }
    }

    fun addNewProductsByKeywordForEshopNG(eshopUuid: EshopUuid, searchKeyWord: String) {
        val task: Supplier<List<String>> = Supplier {
            val searchProductUrlsWrapper = searchProductUrlsWrapper(eshopUuid, searchKeyWord)
            searchProductUrlsWrapper
        }
        val completableFuture: CompletableFuture<List<String>> = CompletableFuture
                .supplyAsync(task, eshopTaskManager.dajmiho(eshopUuid))

//        completableFuture.handle(a -> {
//
//        })


        val future = completableFuture.thenApply {

        }
    }

    private fun logErrorParsingProductUrls(eshopUuid: EshopUuid, searchKeyWord: String, e: Exception) {
        internalTxService.createError(ErrorCreateDto(
                eshopUuid,
                ErrorType.PARSING_PRODUCT_NEW_DATA, null,
                e.message,
                ExceptionUtils.getStackTrace(e), null,
                searchKeyWord))
    }

    private fun logErrorParsingProductNewData(eshopUuid: EshopUuid, e: Exception) {
        internalTxService.createError(ErrorCreateDto(
                eshopUuid,
                ErrorType.PARSING_PRODUCT_URLS, null,
                e.message,
                ExceptionUtils.getStackTrace(e), null,
                null))
    }

    private fun logErrorParsingUnit(eshopUuid: EshopUuid, productUrl: String, productName: String) {
        internalTxService.createError(ErrorCreateDto(
                eshopUuid,
                ErrorType.PARSING_PRODUCT_UNIT_ERR, null, null, null,
                productUrl,
                productName))
    }

    //TODO premenovat metodu na existParserForEshop
    private fun existParserFor(eshopUuid: EshopUuid): Boolean {
        //FIXME cez lamba a findFirst
        for (productsParser in productsParsers) {
            if (eshopUuid == productsParser.eshopUuid) {
                return true
            }
        }
        log.warn("for eshop $eshopUuid none parser found")
        return false
    }

    private fun searchProductUrlsWrapper(eshopUuid: EshopUuid, searchKeyWord: String): List<String> {
        return try {
            // vyparsujem vsetky url-cky produktov, ktore sa najdu na strankach(prechadza aj pageovane stranky)
            htmlParser.searchProductUrls(eshopUuid, searchKeyWord)

        } catch (e: Exception) {
            throw SearchProductUrlsException(eshopUuid, searchKeyWord, e)
        }
    }

    private fun filterOnlyNotExisting(productsUrl: List<String>): List<String> {
        return productsUrl.filter {
            val exist = internalTxService.existProductWithURL(it)
            if (exist) {
                log.debug("product $it already existing")
            }
            !exist
        }
    }
}

class SearchProductUrlsException(val eshopUuid: EshopUuid, val searchKeyWord: String, e: Exception) :
        PrcoRuntimeException("error while parsing eshop $eshopUuid products URLs for keyword $searchKeyWord", e)

class NoProductUrlsFoundFondForKeyword(val eshopUuid: EshopUuid, searchKeyWord: String) :
        PrcoRuntimeException("no url found for eshop $eshopUuid and searchKeyWord $searchKeyWord")

class AllProductsWithGivenUrlsAlreadyExisting(val eshopUuid: EshopUuid) :
        PrcoRuntimeException("none non existing url found for eshop $eshopUuid")

class CreateNewProductsForUrls(val eshopUuid: EshopUuid, e: Exception) :
        PrcoRuntimeException("error while creating new product from URL for eshop $eshopUuid", e)
