package sk.hudak.prco.parser.impl

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dto.ProductNewData
import sk.hudak.prco.dto.ProductUpdateData
import sk.hudak.prco.exception.EshopParserNotFoundPrcoException
import sk.hudak.prco.parser.EshopProductsParser
import sk.hudak.prco.parser.EshopUuidParser
import sk.hudak.prco.parser.HtmlParser

@Component
class JsoupHtmlParserImpl(
        private val eshopUuidParser: EshopUuidParser,
        private val productParsers: List<EshopProductsParser>) : HtmlParser {

    companion object {
        val log = LoggerFactory.getLogger(JsoupHtmlParserImpl::class.java)!!
    }

    override fun searchProductUrls(eshopUuid: EshopUuid, searchKeyWord: String): List<String> {
        log.debug("start searching for keyword '$searchKeyWord'")

        val result = findParserForEshop(eshopUuid).parseUrlsOfProduct(searchKeyWord)

        log.info("count of products found for keyword '$searchKeyWord': ${result.size}")
        return result
    }

    override fun parseProductNewData(productUrl: String): ProductNewData {
        return findParserForEshop(productUrl).parseProductNewData(productUrl)
    }

    override fun parseProductUpdateData(productUrl: String): ProductUpdateData {
        return findParserForEshop(productUrl).parseProductUpdateData(productUrl)
    }

    private fun findParserForEshop(productUrl: String): EshopProductsParser {
        // zistim typ eshopu na zaklade url
        val eshopUuid = eshopUuidParser.parseEshopUuid(productUrl)

        // vyhladam html parser implementaciu na zaklade eshop uuid
        return findParserForEshop(eshopUuid)
    }

    private fun findParserForEshop(eshopUuid: EshopUuid): EshopProductsParser {
        return productParsers.stream()
                .filter { it.eshopUuid == eshopUuid }
                .findFirst()
                .orElseThrow { EshopParserNotFoundPrcoException(eshopUuid) }
    }
}