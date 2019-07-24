package sk.hudak.prco.parser

import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dto.internal.ProductNewData
import sk.hudak.prco.dto.internal.ProductUpdateData

interface HtmlParser {

    fun searchProductUrls(eshopUuid: EshopUuid, searchKeyWord: String): List<String>

    /**
     * Urobi connet na danu URL a vyparsuje html data o produkte.
     *
     * @param productUrl
     * @return
     */
    fun parseProductNewData(productUrl: String): ProductNewData

    fun parseProductUpdateData(productUrl: String): ProductUpdateData
}