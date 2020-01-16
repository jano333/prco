package sk.hudak.prco.parser.eshop

import org.jsoup.HttpStatusException
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import sk.hudak.prco.api.ProductAction
import sk.hudak.prco.builder.SearchUrlBuilder
import sk.hudak.prco.dto.ProductNewData
import sk.hudak.prco.dto.ProductUpdateData
import sk.hudak.prco.dto.UnitTypeValueCount
import sk.hudak.prco.exception.*
import sk.hudak.prco.kotlin.warnYellow
import sk.hudak.prco.parser.unit.UnitParser
import sk.hudak.prco.utils.ThreadUtils.sleepRandomSafeBetween
import sk.hudak.prco.utils.UserAgentDataHolder
import java.math.BigDecimal
import java.net.SocketTimeoutException
import java.util.*

//FIXME refactor...
abstract class JSoupProductParser : EshopProductsParser {

    protected var unitParser: UnitParser
    protected var userAgentDataHolder: UserAgentDataHolder
    protected var searchUrlBuilder: SearchUrlBuilder

    constructor(unitParser: UnitParser,
                userAgentDataHolder: UserAgentDataHolder,
                searchUrlBuilder: SearchUrlBuilder) {
        this.unitParser = unitParser
        this.userAgentDataHolder = userAgentDataHolder
        this.searchUrlBuilder = searchUrlBuilder
    }

    protected val TIMEOUT_15_SECOND = 15000
    protected val TIMEOUT_10_SECOND = 10000
    protected val DEFAULT_TIMEOUT_3_SECOND = 3000
    protected val DATE_FORMAT_HH_MM_YYYY = "dd.MM.yyyy"
    val SINGLE_PAGE_ONE = 1

    companion object {
        val log = LoggerFactory.getLogger(JSoupProductParser::class.java)!!
    }

    protected val userAgent: String
        get() = userAgentDataHolder.getUserAgentForEshop(eshopUuid)

    protected open val timeout = DEFAULT_TIMEOUT_3_SECOND

    protected open val requestCookie: Map<String, String>
        get() = emptyMap()

    /**
     * Metoda vrati pocet stranok(pagging) na kolkych sa dane vyhladavane slovo vyskytuje.
     * <br></br>**Pozor:** Nie je to celkovy pocet produktov, ale pocet stranok, v zavislosti od strankovanie
     * daneho eshopu...
     * <br></br>Volana 1 v poradi.
     * <br></br> vynimky vyhadzovane touto metodu su odchytovane vyssie
     *
     * @param documentList
     * @return
     */
    abstract override fun parseCountOfPages(documentList: Document): Int

    /**
     * Volana 2 v poradi.<br></br>
     * vynimky vyhadzovane touto metodu su odchytovane vyssie
     *
     * @param documentList aktualne parsovany dokument
     * @param pageNumber   poradove cislo stranky(z pagingu)
     * @return zoznam URL produktov
     */
    abstract override fun parseUrlsOfProduct(documentList: Document, pageNumber: Int): List<String>

    /**
     * Volane pri aktualizacii udajov, overuje sa ci je product dostupny
     *
     * @param documentDetailProduct html document
     * @return true, ak je produkct nedostupny
     */
    protected abstract fun isProductUnavailable(documentDetailProduct: Document): Boolean

    /**
     * TODO
     *
     * @param documentDetailProduct
     * @return
     */
    protected abstract fun parseProductNameFromDetail(documentDetailProduct: Document): String?

    /**
     * TODO
     *
     * @param documentDetailProduct
     * @return
     */
    protected abstract fun parseProductPictureURL(documentDetailProduct: Document): String?

    /**
     * TODO
     *
     * @param documentDetailProduct
     * @return
     */
    protected abstract fun parseProductPriceForPackage(documentDetailProduct: Document): Optional<BigDecimal>

    //TODO nasledovne 2 metody spojit do jednej a urobit aj navratovy typ

    protected open fun parseProductAction(documentDetailProduct: Document): Optional<ProductAction> {
        return Optional.empty()
    }

    protected open fun parseProductActionValidity(documentDetailProduct: Document): Optional<Date> {
        return Optional.empty()
    }

    override fun parseUrlsOfProduct(searchKeyWord: String): List<String> {
        val searchUrl = searchUrlBuilder.buildSearchUrl(eshopUuid, searchKeyWord)
        val firstPageDocument = retrieveDocument(searchUrl)

        // 1. krok - zistim paging(pocet stranok)
        var countOfAllPages = internalGetCountOfPages(firstPageDocument, searchUrl)
        log.debug("new pages count: {}", countOfAllPages)

        // checking for max size
        val maxCountOfNewPages = eshopUuid.maxCountOfNewPages
        if (countOfAllPages > maxCountOfNewPages) {
            countOfAllPages = maxCountOfNewPages
            log.debug("count of new pages changed to maxim: {}", countOfAllPages)
        }

        // 2. krok - parsujem prvu stranku
        val firstPageUrls = internalParsePageForProductUrls(firstPageDocument, searchUrl)
        if (firstPageUrls.isEmpty()) {
            log.info("none products URL found for keyword '{}'", searchKeyWord)
            return emptyList()
        }

        val resultUrls = ArrayList(firstPageUrls)

        // 3. krok - ak existuju dalsie stranky parsujem aj tie
        if (countOfAllPages > 1) {
            for (currentPageNumber in 2..countOfAllPages) {
                resultUrls.addAll(parseNextPage(searchKeyWord, currentPageNumber)!!)
            }
        }
        return resultUrls
    }

    override fun parseProductUpdateData(document: Document, productUrl: String): ProductUpdateData {
        // because there could be redirect
        val realProductUrl = document.location()
        val redirect = if (productUrl != realProductUrl) {
            log.warn("redirecting: ")
            log.warn("from $productUrl")
            log.warn("to $realProductUrl")
            true
        } else {
            false
        }

        // ak je produkt nedostupny tak nastavim len url a eshop uuid
        if (isProductUnavailable(document)) {
            //TODO pridat ako osobitnu chybu, pripadne do osobitnej tabulku na vyhodnotenie ak je napr.
            // 5 produktov po sebe oznacenych za nedostupnych tak overit ci je to naozaj tak
            // ale toto musi byt o uroven vyssie
            log.warnYellow("product is unavailable: $realProductUrl")
            return ProductUpdateData.createUnavailable(realProductUrl, eshopUuid, redirect)
        }

        // product name
        val productName = parseProductNameFromDetail(document)
        logWarningIfNullOrEmpty(Optional.ofNullable(productName), "productName", document.location())
        if (productName == null) {
            throw ProductNameNotFoundException(productUrl)
        }

        // product price for package
        val productPriceForPackageOpt = parseProductPriceForPackage(document)
        logWarningIfNullOrEmpty(productPriceForPackageOpt, "priceForPackage", document.location())
        val productPriceForPackage = productPriceForPackageOpt.orElseThrow { ProductPriceNotFoundException(productUrl) }

        // product picture
        val pictureUrl = internalParseProductPictureURL(document, productUrl)

        // product action
        val productAction = internalParseProductAction(document, productUrl)
        // validity of action
        var productActionValidity = Optional.empty<Date>()
        if (productAction.isPresent && productAction.get() == ProductAction.IN_ACTION) {
            productActionValidity = internalParseProductActionValidity(document, productUrl)
        }

        return ProductUpdateData.createAvailable(
                realProductUrl,
                eshopUuid,
                redirect,
                productName,
                pictureUrl,
                productPriceForPackage,
                // FIXME spojit do jedneho ohladne product action
                if (productAction.isPresent) productAction.get() else null,
                if (productActionValidity.isPresent) productActionValidity.get() else null)
    }

    override fun parseProductNewData(document: Document, productUrl: String): ProductNewData {

        val result = ProductNewData(eshopUuid, productUrl)

        val productNameOpt = parseProductNameFromDetail(document)
        logWarningIfNullOrEmpty(Optional.ofNullable(productNameOpt), "productName", document.location())
        if (productNameOpt == null) {
            throw  ProductNameNotFoundException(productUrl);
        }
        result.name = productNameOpt


        val productPictureUrl = internalParseProductPictureURL(document, productUrl)
        logWarningIfNullOrEmpty(productPictureUrl, "pictureUrl", document.location())
        if (productPictureUrl!=null && productPictureUrl.isNotBlank()) {
            result.pictureUrl = productPictureUrl
        }

        // ak nemame nazov produktu nemozeme pokracovat v parsovani 'unit'
        if (result.name == null) {
            return result
        }

        parseUnitValueCount(document, result.name!!)
                .ifPresent { (unit, value, packageCount) ->
                    result.unit = unit
                    result.unitValue = value
                    result.unitPackageCount = packageCount
                }

        return result
    }


//    override fun parseProductUpdateData(productUrl: String): ProductUpdateData {
//        val document = retrieveDocument(productUrl)
//        return parseProductUpdateData(document, productUrl)
//    }


    /**
     * TODO spisat ake vynimky moze vyhadzovat
     */
    override fun retrieveDocument(productUrl: String): Document {
        try {
            log.debug("request URL: $productUrl")
            log.trace("request userAgent: $userAgent")

            val connection = Jsoup.connect(productUrl)
                    .userAgent(userAgent)
                    .timeout(timeout)

            if (requestCookie.isNotEmpty()) {
                connection.cookies(requestCookie)
            }

            val response = connection.execute()

            log.trace("response cookies: ${response.cookies()}")
            // convert 'body' to 'document'
            return response.parse()


        } catch (e: Exception) {
            throw convertToParserException(productUrl, e)
        }
    }

    protected open fun convertToParserException(productUrl: String, e: Exception): PrcoRuntimeException {
        val errMsg = "error creating document for url '$productUrl': "
        return when (e) {
            is HttpStatusException -> {
                if (404 == e.statusCode) {
                    ProductPageNotFoundHttpParserException("$errMsg $e", e)
                } else {
                    HttpStatusParserException(e.statusCode, "$errMsg $e", e)
                }
            }
            is SocketTimeoutException -> {
                HttpSocketTimeoutParserException(e)
            }
            else -> {
                CoreParserException(errMsg, e)
            }
        }
    }


    protected open fun parseNextPage(searchKeyWord: String, currentPageNumber: Int): List<String>? {
        val searchUrl = searchUrlBuilder.buildSearchUrl(eshopUuid, searchKeyWord, currentPageNumber)
        // FIXME 5 a 20 dat nech nacita od konfiguracie pre konkretny eshop
        sleepRandomSafeBetween(5, 20)
        return parseUrlsOfProduct(retrieveDocument(searchUrl), currentPageNumber)
    }

    protected open fun parseUnitValueCount(document: Document, productName: String): Optional<UnitTypeValueCount> {
        return unitParser.parseUnitTypeValueCount(productName)
    }

    private fun internalParsePageForProductUrls(firstPageDocument: Document, searchUrl: String): List<String> {
        try {
            val urls = parseUrlsOfProduct(firstPageDocument, 1) ?: throw PrcoRuntimeException("urls is null")
            urls.stream()
                    .filter { value -> value == null || value.isEmpty() }
                    .findAny()
                    .ifPresent { throw PrcoRuntimeException("at least one url is null or empty") }

            return urls
        } catch (e: Exception) {
            throw PrcoRuntimeException("error while parsing pages of products, search URL: $searchUrl", e)
        }

    }

    private fun internalParseProductActionValidity(document: Document, searchUrl: String): Optional<Date> {
        try {
            return parseProductActionValidity(document)
        } catch (e: Exception) {
            throw PrcoRuntimeException("error while parsing product action validity, search URL: $searchUrl", e)
        }

    }

    private fun internalParseProductPictureURL(document: Document, productUrl: String): String? {
        try {
            return parseProductPictureURL(document)
        } catch (e: Exception) {
            throw PrcoRuntimeException("error while parsing product picture, URL: $productUrl", e)
        }

    }

    private fun internalParseProductAction(document: Document, productUrl: String): Optional<ProductAction> {
        try {
            return parseProductAction(document)
        } catch (e: Exception) {
            throw PrcoRuntimeException("error while parsing product action, URL: $productUrl", e)
        }
    }

    private fun internalGetCountOfPages(documentList: Document, searchUrl: String): Int {
        try {
            return parseCountOfPages(documentList)
        } catch (e: Exception) {
            throw PrcoRuntimeException("error while parsing count of page for products, search URL: $searchUrl", e)
        }
    }

    private fun logWarningIfNullOrEmpty(propertyValue: String?, propertyName: String, productUrl: String) {
        if (propertyValue == null) {
            log.warn("property {} is null for product url {}", propertyName, productUrl)
            return
        }

        if (propertyValue.isBlank()) {
            log.warn("property {} is blank for product url {}", propertyName, productUrl)
        }
    }

    private fun logWarningIfNullOrEmpty(propertyValue: Optional<*>, propertyName: String, productUrl: String) {
        if (!propertyValue.isPresent) {
            log.warn("property {} is null for product url {}", propertyName, productUrl)
            return
        }

        if (propertyValue.get() is String) {
            if ((propertyValue.get() as String).isBlank()) {
                log.warn("property {} is blank for product url {}", propertyName, productUrl)
            }
        }
    }

}
