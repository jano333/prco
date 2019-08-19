package sk.hudak.prco.manager.addprocess

import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component
import sk.hudak.prco.api.ErrorType
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dto.ErrorCreateDto
import sk.hudak.prco.dto.product.NewProductCreateDto
import sk.hudak.prco.mapper.PrcoOrikaMapper
import sk.hudak.prco.parser.eshop.EshopProductsParser
import sk.hudak.prco.parser.eshopuid.EshopUuidParser
import sk.hudak.prco.parser.html.HtmlParser
import sk.hudak.prco.service.InternalTxService
import sk.hudak.prco.task.EshopTaskManager
import sk.hudak.prco.task.ExceptionHandlingRunnable
import sk.hudak.prco.utils.ThreadUtils
import sk.hudak.prco.utils.ThreadUtils.sleepSafe
import sk.hudak.prco.utils.Validate.notEmpty
import sk.hudak.prco.utils.Validate.notNullNotEmpty
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException

@Primary
@Component
class NgAddingNewProductManagerImpl(private val internalTxService: InternalTxService,
                                    private val mapper: PrcoOrikaMapper,
                                    private val htmlParser: HtmlParser,
                                    private val eshopTaskManager: EshopTaskManager,
                                    private val eshopUuidParser: EshopUuidParser,
                                    private val productsParsers: List<EshopProductsParser>
) : AddingNewProductManager {

    companion object {
        val log = LoggerFactory.getLogger(NgAddingNewProductManagerImpl::class.java)!!
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
                    sleepSafe(it.countToWaitInSecond)
                }
    }

    override fun addNewProductsByKeywordForEshop(eshopUuid: EshopUuid, searchKeyWord: String) {
        notEmpty(searchKeyWord, "searchKeyWord")

        val notExistingProducts: List<String>?
        try {
            val urlList = eshopTaskManager.submitTask(eshopUuid, searchProductUrlsCallable(eshopUuid, searchKeyWord))
                    .get()

            if (urlList.isEmpty()) {
                log.debug("none url found for eshop $eshopUuid and keyword $searchKeyWord")
                // koncim
                return
            }
            notExistingProducts = eshopTaskManager.submitTask(eshopUuid, filterOnlyNonExistingCallable(eshopUuid, urlList, searchKeyWord))
                    .get()


        } catch (e: Exception) {
            handleAddNewProductsByKeywordForEshopException(e, eshopUuid)
            // koncim
            return
        }

        asyncCreateNewProductsForEshop(eshopUuid, notExistingProducts)
    }

    private fun asyncCreateNewProductsForEshop(eshopUuid: EshopUuid, urlList: List<String>) {
        eshopTaskManager.submitTask(eshopUuid, object : ExceptionHandlingRunnable() {

            override fun doInRunnable() {
                log.debug(">> asyncCreateNewProductsForEshop eshop $eshopUuid")
                eshopTaskManager.markTaskAsRunning(eshopUuid)

                createNewProductsErrorWrapper(eshopUuid, urlList)

                eshopTaskManager.markTaskAsFinished(eshopUuid, false)
            }

            override fun handleException(e: Exception) {
                handleAddNewProductsByKeywordForEshopException(e, eshopUuid)
            }

            override fun doInFinally() {
                log.debug("<< asyncCreateNewProductsForEshop eshop $eshopUuid")
            }
        }
        )
    }

    override fun addNewProductsByUrl(productsUrl: List<String>) {
        //FIXME
        notNullNotEmpty(productsUrl as Array<String>, "productsUrl")
        log.debug(">> addNewProductsByUrl count of URLs: ${productsUrl.size}")

        // filtrujem len tie ktore este neexistuju
        val notExistingProducts = filterOnlyNotExisting(productsUrl)
        if (notExistingProducts.isNotEmpty()) {
            // roztriedim URL podla typu eshopu
            val eshopUrls: EnumMap<EshopUuid, MutableList<String>> = convertToUrlsByEshop(notExistingProducts)
            // spustim parsovanie pre kazdy eshop
            eshopUrls.keys.forEach {
                asyncCreateNewProductsForEshop(it, eshopUrls[it]!!)
                // kazdy dalsi spusti s 1 sekundovym oneskorenim
                sleepSafe(it.countToWaitInSecond)
            }
        }
        log.debug("<< addNewProductsByUrl count of URLs: ${productsUrl.size}")
    }

    //TODO toto je zle
    private fun createNewProductsErrorWrapper(eshopUuid: EshopUuid, urlList: List<String>) {
        try {
            createNewProducts(eshopUuid, urlList)

        } catch (e: Exception) {
            throw CreateNewProductsForUrls(eshopUuid, e)
        }
    }

    private fun createNewProducts(eshopUuid: EshopUuid, urlList: List<String>) {
        loop@
        for (currentUrlIndex in 0 until urlList.size) {

            log.debug("starting {} of {}", currentUrlIndex + 1, urlList.size)
            val productUrl = urlList[currentUrlIndex]

            when (processNewProductUrl(eshopUuid, productUrl)) {
                ContinueStatus.STOP_PROCESSING_NEXT_ONE -> {
                    break@loop
                }
                ContinueStatus.CONTINUE_TO_NEXT_ONE -> {
                    ThreadUtils.sleepRandomSafe()
                }
            }
            // sleep pre dalsou iteraciou
            sleepSafe(eshopUuid.countToWaitInSecond)
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
            logErrorParsingProductNewData(eshopUuid, e)
            return ContinueStatus.CONTINUE_TO_NEXT_ONE
        }

        if (null == productNewData.name) {
            logErrorParsingProductNameForNewProduct(eshopUuid, productUrl)
            log.warn("new product not contains name, skipping to next product")
            return ContinueStatus.CONTINUE_TO_NEXT_ONE
        }

        // preklopim a pridavam do DB
        internalTxService.createNewProduct(mapper.map(productNewData, NewProductCreateDto::class.java))
        return ContinueStatus.CONTINUE_TO_NEXT_ONE
    }

    private enum class ContinueStatus {
        CONTINUE_TO_NEXT_ONE,
        STOP_PROCESSING_NEXT_ONE
    }

    /**
     * Find all URL for given keyword.
     */
    private fun searchProductUrlsCallable(eshopUuid: EshopUuid, searchKeyWord: String): Callable<List<String>> {
        return Callable {
            eshopTaskManager.markTaskAsRunning(eshopUuid)
            log.debug(">> searchProductUrlsCallable eshop $eshopUuid, searchKeyWord $searchKeyWord")

            // vyparsujem vsetky url-cky produktov, ktore sa najdu na strankach(prechadza aj pageovane stranky)
            val urlList: List<String> = searchProductUrlsWrapper(eshopUuid, searchKeyWord)

            eshopTaskManager.markTaskAsFinished(eshopUuid, false)
            log.debug("<< searchProductUrlsCallable eshop $eshopUuid, searchKeyWord $searchKeyWord")
            urlList
        }
    }

    private fun filterOnlyNonExistingCallable(eshopUuid: EshopUuid, urlList: List<String>, searchKeyWord: String): Callable<List<String>> {
        return Callable {
            eshopTaskManager.markTaskAsRunning(eshopUuid)
            log.debug(">> filterOnlyNonExistingCallable eshop $eshopUuid, searchKeyWord $searchKeyWord count of URLs ${urlList.size}")

            // filter only non existing
            val notExistingProducts = filterOnlyNotExisting(urlList)
            if (notExistingProducts.isEmpty()) {
                log.warn("count of non existing products URL is zero")
                throw NoneNonExistingProductUrlsFoundForKeyword(eshopUuid, searchKeyWord)
            }
            log.debug("<< filterOnlyNonExistingCallable eshop $eshopUuid, searchKeyWord $searchKeyWord count of non existing URLs ${notExistingProducts.size}")
            eshopTaskManager.markTaskAsFinished(eshopUuid, false)
            notExistingProducts
        }
    }

    private fun searchProductUrlsWrapper(eshopUuid: EshopUuid, searchKeyWord: String): List<String> {
        return try {
            // vyparsujem vsetky url-cky produktov, ktore sa najdu na strankach(prechadza aj pageovane stranky)
            htmlParser.searchProductUrls(eshopUuid, searchKeyWord)

        } catch (e: Exception) {
            throw SearchProductUrlsException(eshopUuid, searchKeyWord, e)
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

    private fun filterOnlyNotExisting(productsUrl: List<String>): List<String> {
        return productsUrl.filter {
            val exist = internalTxService.existProductWithURL(it)
            if (exist) {
                log.debug("product $it already existing")
            }
            !exist
        }
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

    private fun handleAddNewProductsByKeywordForEshopException(e: Exception, eshopUuid: EshopUuid) {
        when (e) {
            is ExecutionException -> {
                log.error("it is ExecutionException ${e.message} type ${e.cause?.javaClass?.simpleName}")
                handleAddNewProductsByKeywordForEshopException(e.cause as Exception, eshopUuid)
            }

            is SearchProductUrlsException -> {
                log.error(e.message, e)
                eshopTaskManager.markTaskAsFinished(e.eshopUuid, true)
                logErrorParsingProductUrls(e.eshopUuid, e.searchKeyWord, e)
            }

            is NoProductUrlsFoundFondForKeyword -> {
                log.info(e.message)
                eshopTaskManager.markTaskAsFinished(e.eshopUuid, false)
            }

            is NoneNonExistingProductUrlsFoundForKeyword -> {
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

    private fun logErrorParsingProductUrls(eshopUuid: EshopUuid, searchKeyWord: String, e: Exception) {
        internalTxService.createError(ErrorCreateDto(
                eshopUuid,
                ErrorType.PARSING_PRODUCT_NEW_DATA, null,
                e.message,
                ExceptionUtils.getStackTrace(e), null,
                searchKeyWord))
    }

    private fun logErrorParsingProductNameForNewProduct(eshopUuid: EshopUuid, productUrl: String) {
        internalTxService.createError(ErrorCreateDto(
                eshopUuid,
                ErrorType.PARSING_PRODUCT_NAME_FOR_NEW_PRODUCT, null, null, null,
                productUrl, null))
    }

    private fun logErrorParsingProductNewData(eshopUuid: EshopUuid, e: Exception) {
        internalTxService.createError(ErrorCreateDto(
                eshopUuid,
                ErrorType.PARSING_PRODUCT_URLS, null,
                e.message,
                ExceptionUtils.getStackTrace(e), null,
                null))
    }
}