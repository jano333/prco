package sk.hudak.prco.parser.eshop

import org.jsoup.nodes.Document
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dto.ProductNewData
import sk.hudak.prco.dto.ProductUpdateData
import sk.hudak.prco.exception.ProductNameNotFoundException

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
    fun parseUrlsOfProduct(searchKeyWord: String): List<String>

    /**
     * @param productUrl url konkretneho produktu
     * @return
     * @throws TODO
     */
    fun parseProductNewData(productUrl: String): ProductNewData

    /**
     * @param productUrl url konkretneho produktu
     * @return
     * @throws ProductNameNotFoundException, ProductPriceNotFoundException
     */
    fun parseProductUpdateData(productUrl: String): ProductUpdateData

    // --- nove API ---
    fun retrieveDocument(productUrl: String): Document

    //TODO toto nema byt htlm parser ???
    fun parseCountOfPages(document: Document): Int

    fun parsePageForProductUrls(document: Document, pageNumber: Int): List<String>

}
