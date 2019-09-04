package sk.hudak.prco.parser.html

import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dto.ProductNewData
import sk.hudak.prco.dto.ProductUpdateData

interface HtmlParser {

    // FIXME zmenit navratovu hodnotu z List<String> na Set<String>
    fun searchProductUrls(eshopUuid: EshopUuid, searchKeyWord: String): List<String>

    /**
     * Urobi connect na danu URL a vyparsuje html data o produkte.
     *
     * @param productUrl
     * @return
     */
    fun parseProductNewData(productUrl: String): ProductNewData

    fun parseProductUpdateData(productUrl: String): ProductUpdateData
}