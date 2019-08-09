package sk.hudak.prco.manager.addprocess

import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Primary
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
import sk.hudak.prco.utils.ThreadUtils
import sk.hudak.prco.utils.Validate.notEmpty
import sk.hudak.prco.utils.Validate.notNullNotEmpty
import java.util.*
import kotlin.collections.ArrayList

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
                    existParserFor(it)
                }
                .forEach {
                    // spusti stahovanie pre dalsi
                    addNewProductsByKeywordForEshop(it, searchKeyWord)
                    // kazdy dalsi spusti s 3 sekundovym oneskorenim
                    ThreadUtils.sleepSafe(3)
                }
    }

    override fun addNewProductsByKeywordForEshop(eshopUuid: EshopUuid, searchKeyWord: String) {
        notEmpty(searchKeyWord, "searchKeyWord")

        eshopTaskManager.submitTask(eshopUuid, Runnable {
            log.debug(">> addNewProductsByKeywordForEshop eshop $eshopUuid, searchKeyWord $searchKeyWord")

            eshopTaskManager.markTaskAsRunning(eshopUuid)

            try {
                // vyparsujem vsetky url-cky produktov, ktore sa najdu na strankach(prechadza aj pageovane stranky)
                val urlList: List<String> = searchProductUrlsWrapper(eshopUuid, searchKeyWord)

                // if none url found -> end
                if (urlList.isEmpty()) {
                    throw NoProductUrlsFoundFondForKeyword(eshopUuid, searchKeyWord)
                }

                createNewProductsWrapper(eshopUuid, urlList as MutableList<String>)

            } catch (e: Exception) {
                handleAddNewProductsByKeywordForEshopException(e)

            } finally {
                log.debug("<< addNewProductsByKeywordForEshop eshop $eshopUuid, searchKeyWord $searchKeyWord")
            }
        })

    }

    override fun addNewProductsByUrl(vararg productsUrl: String) {
        notNullNotEmpty(productsUrl as Array<String>, "productsUrl")

        val countOfUrls = productsUrl.size
        log.debug(">> addNewProductsByUrl count of URLs: $countOfUrls")

        // roztriedim URL podla typu eshopu
        val eshopUrls: EnumMap<EshopUuid, MutableList<String>> = EnumMap(EshopUuid::class.java)

        productsUrl.forEach {
            eshopUrls.computeIfAbsent(eshopUuidParser.parseEshopUuid(it)) {
                ArrayList()
            }.add(it)
        }

        eshopUrls.keys.forEach { eshopUuid ->
            run {
                eshopTaskManager.submitTask(eshopUuid,
                        Runnable {
                            log.debug(">> addNewProductsByKeywordForEshop eshop $eshopUuid")

                            eshopTaskManager.markTaskAsRunning(eshopUuid)

                            try {
                                createNewProductsWrapper(eshopUuid, eshopUrls[eshopUuid]!!)

                            } catch (e: Exception) {
                                handleAddNewProductsByKeywordForEshopException(e)

                            } finally {
                                log.debug("<< addNewProductsByKeywordForEshop eshop $eshopUuid")
                            }
                        }
                )
            }
        }
        log.debug("<< addNewProductsByUrl count of URLs: {}", countOfUrls)
    }

    private fun handleAddNewProductsByKeywordForEshopException(e: Exception) {
        when (e) {
            is SearchProductUrlsException -> {
                log.error(e.message, e)
                logErrorParsingProductUrls(e.eshopUuid, e.searchKeyWord, e)
                eshopTaskManager.markTaskAsFinished(e.eshopUuid, true)
            }

            is NoProductUrlsFoundFondForKeyword -> {
                log.info(e.message)
                eshopTaskManager.markTaskAsFinished(e.eshopUuid, false)
            }

            is CreateNewProductsForUrls -> {
                log.error(e.message, e)
                logErrorParsingProductNewData(e.eshopUuid, e)
                eshopTaskManager.markTaskAsFinished(e.eshopUuid, true)
            }

            else -> {
                //TODO
                log.error(e.message, e);
            }
        }
    }

    private fun createNewProductsWrapper(eshopUuid: EshopUuid, urlList: MutableList<String>) {
        try {
            createNewProducts(eshopUuid, urlList)

        } catch (e: Exception) {
            throw CreateNewProductsForUrls(eshopUuid, e)
        }
    }

    private fun createNewProducts(eshopUuid: EshopUuid, urlList: MutableList<String>) {
        val allUrlCount = urlList.size

        for (currentUrlIndex in 0 until allUrlCount) {

            if (eshopTaskManager.isTaskShouldStopped(eshopUuid)) {
                eshopTaskManager.markTaskAsStopped(eshopUuid)
                break
            }

            log.debug("starting {} of {}", currentUrlIndex + 1, allUrlCount)
            val productUrl = urlList[currentUrlIndex]

            // ak uz exituje, tak vynechavam
            log.debug("checking existence of product URL {}", productUrl)
            if (internalTxService.existProductWithURL(productUrl)) {
                log.debug("already added -> skipping")
                continue
            }

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
            throw SearchProductUrlsException(eshopUuid, searchKeyWord, e);
        }
    }

}

class SearchProductUrlsException(val eshopUuid: EshopUuid, val searchKeyWord: String, e: Exception) :
        PrcoRuntimeException("error while parsing eshop $eshopUuid products URLs for keyword $searchKeyWord", e)

class NoProductUrlsFoundFondForKeyword(val eshopUuid: EshopUuid, searchKeyWord: String) :
        PrcoRuntimeException("no url found for eshop $eshopUuid and searchKeyWord $searchKeyWord")

class CreateNewProductsForUrls(val eshopUuid: EshopUuid, e: Exception) :
        PrcoRuntimeException("error while creating new product from URL for eshop $eshopUuid", e)
