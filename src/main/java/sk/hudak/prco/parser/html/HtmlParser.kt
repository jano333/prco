package sk.hudak.prco.parser.html

import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dto.ProductNewData
import sk.hudak.prco.dto.ProductUpdateData
import sk.hudak.prco.exception.*

interface HtmlParser {

    // FIXME zmenit navratovu hodnotu z List<String> na Set<String>
    fun searchProductUrls(eshopUuid: EshopUuid, searchKeyWord: String): List<String>

    /**
     * Urobi connect na danu URL a vyparsuje html data o produkte.
     *
     * @param productUrl
     * @return
     * @throws [EshopNotFoundParserException]
     * @throws [EshopParserNotFoundException]
     * //TODO doplnit ostatne vynimky
     */
    fun parseProductNewData(productUrl: String): ProductNewData

    /**
     * @throws [EshopNotFoundParserException]
     * @throws [EshopParserNotFoundException]
     * @throws [ProductPageNotFoundHttpParserException]
     * @throws [HttpStatusParserException]
     * @throws [HttpSocketTimeoutParserException]
     * @throws [CoreParserException]
     */
    fun parseProductUpdateData(productUrl: String): ProductUpdateData
}