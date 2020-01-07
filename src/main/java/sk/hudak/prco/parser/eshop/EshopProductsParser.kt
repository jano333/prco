package sk.hudak.prco.parser.eshop

import org.jsoup.nodes.Document
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dto.ProductNewData
import sk.hudak.prco.dto.ProductUpdateData

/**
 * Pozor dane rozhranie uz pouziva jsoup document .... teda je zavisle na implementacii...
 * TODO skusit urobit API na zaklade tychto dvoch metod a impl schovat
 * <code>
 * fun parseProductNewData(productUrl: String): ProductNewData {
 *  val eshopUuid = eshopUuidParser.parseEshopUuid(productUrl)
 *  val parserForEshop = getParserForEshop(eshopUuid)
 *  val document = parserForEshop.retrieveDocument(productUrl)
 *  return parserForEshop.parseProductNewData(document, productUrl)
 *  }
 *
 *fun parseProductUpdateData(productUrl: String): ProductUpdateData {
 *  val eshopUuid = eshopUuidParser.parseEshopUuid(productUrl)
 *  val parserForEshop = getParserForEshop(eshopUuid)
 *  val document = parserForEshop.retrieveDocument(productUrl)
 *  return parserForEshop.parseProductUpdateData(document, productUrl)
 *  }
 * </code>
 *
 */
interface EshopProductsParser {

    /**
     * Jednoznacky identifikator eshopu, pre ktory je urceny dany product parser.
     *
     * @return
     */
    val eshopUuid: EshopUuid

    /**
     * Vyhlada URL-cky vsetkych produktov v danom eshope, ktore vyhovuju vyhladavaciemu retazcu `searchKeyWord`
     *
     * @param searchKeyWord klucove slovo na zaklade ktoreho sa vyhladaju URL produktov pre eshop [.getEshopUuid]
     * @return zoznam URL produktov pre dane klucove slovo. V pride ak nic nenajde vrati prazny zoznam.
     */
    @Deprecated("use new API")
    fun parseUrlsOfProduct(searchKeyWord: String): List<String>

    // --- nove API ---
    fun retrieveDocument(productUrl: String): Document

    fun parseCountOfPages(document: Document): Int

    fun parseUrlsOfProduct(document: Document, pageNumber: Int): List<String>

    fun parseProductNewData(document: Document, productUrl: String): ProductNewData

    fun parseProductUpdateData(document: Document, productUrl: String): ProductUpdateData

}
