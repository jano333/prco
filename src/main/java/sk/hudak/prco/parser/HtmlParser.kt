package sk.hudak.prco.parser

import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dto.ProductNewData
import sk.hudak.prco.dto.ProductUpdateData

interface HtmlParser {

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