package sk.hudak.prco.manager.addprocess

import org.apache.commons.lang3.exception.ExceptionUtils
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.ErrorType
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dto.ErrorCreateDto
import sk.hudak.prco.dto.product.NewProductCreateDto
import sk.hudak.prco.mapper.PrcoOrikaMapper
import sk.hudak.prco.parser.EshopProductsParser
import sk.hudak.prco.parser.EshopUuidParser
import sk.hudak.prco.parser.HtmlParser
import sk.hudak.prco.service.InternalTxService
import sk.hudak.prco.task.EshopTaskManager
import sk.hudak.prco.utils.ThreadUtils
import sk.hudak.prco.utils.Validate.notNullNotEmpty
import java.util.*
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

        private const val ERROR_WHILE_CREATING_NEW_PRODUCT = "error while creating new product"
    }

    override fun addNewProductsByKeywordsForAllEshops(vararg searchKeyWords: String) {
        searchKeyWords.forEach { addNewProductsByKeywordForAllEshops(it) }
    }

    override fun addNewProductsByKeywordForAllEshops(searchKeyWord: String) {
        notNullNotEmpty(searchKeyWord, "searchKeyWord")

        EshopUuid.values()
                .filter { existParserFor(it) }
                .forEach {
                    // spusti stahovanie pre dalsi
                    addNewProductsByKeywordForEshop(it, searchKeyWord)
                    // kazdy dalsi spusti s 3 sekundovym oneskorenim
                    ThreadUtils.sleepSafe(3)
                }
    }

    override fun addNewProductsByKeywordForEshop(eshopUuid: EshopUuid, searchKeyWord: String) {
        notNullNotEmpty(searchKeyWord, "searchKeyWord")

        log.debug(">> addNewProductsByKeywordForEshop eshop: {}, searchKeyWord: {}", eshopUuid, searchKeyWord)
        eshopTaskManager.submitTask(eshopUuid, Runnable {

            eshopTaskManager.markTaskAsRunning(eshopUuid)

            val urlList: List<String>
            try {
                // vyparsujem vsetky url-cky produktov, ktore sa najdu na strankach(prechadza aj pageovane stranky)
                urlList = htmlParser.searchProductUrls(eshopUuid, searchKeyWord)

            } catch (e: Exception) {
                // if error during parsing -> end
                log.error("error while parsing eshop products URLs", e)
                logErrorParsingProductUrls(eshopUuid, searchKeyWord, e)
                eshopTaskManager.markTaskAsFinished(eshopUuid, true)
                log.debug("<< addNewProductsByKeywordForEshop eshop $eshopUuid, searchKeyWord $searchKeyWord")
                return@Runnable
            }

            // if none url found -> end
            if(urlList.isEmpty()){
                log.debug("no url found for eshop $eshopUuid and searchKeyWord $searchKeyWord")
                eshopTaskManager.markTaskAsFinished(eshopUuid, false)
                log.debug("<< addNewProductsByKeywordForEshop eshop $eshopUuid, searchKeyWord $searchKeyWord")
                return@Runnable
            }


            var finishedWithError = false
            try {

                createNewProducts(eshopUuid, urlList as MutableList<String>)

            } catch (e: Exception) {
                log.error(ERROR_WHILE_CREATING_NEW_PRODUCT, e)
                logErrorParsingProductUrls(eshopUuid, searchKeyWord, e)
                finishedWithError = true

            } finally {
                eshopTaskManager.markTaskAsFinished(eshopUuid, finishedWithError)
            }
        })

        log.debug("<< addNewProductsByKeywordForEshop eshop {}, searchKeyWord {}", eshopUuid, searchKeyWord)
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
                eshopTaskManager.submitTask(eshopUuid, Runnable {

                    eshopTaskManager.markTaskAsRunning(eshopUuid)
                    var finishedWithError = false

                    try {
                        createNewProducts(eshopUuid, eshopUrls[eshopUuid]!!)

                    } catch (e: Exception) {
                        log.error(ERROR_WHILE_CREATING_NEW_PRODUCT, e)
                        finishedWithError = true

                    } finally {
                        eshopTaskManager.markTaskAsFinished(eshopUuid, finishedWithError)
                    }
                }
                )
            }
        }
        log.debug("<< addNewProductsByUrl count of URLs: {}", countOfUrls)
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
                ErrorType.PARSING_PRODUCT_URLS, null,
                e.message,
                ExceptionUtils.getStackTrace(e), null,
                searchKeyWord))
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


}
