package sk.hudak.prco.eshop.pharmacy

import org.apache.commons.lang3.StringUtils
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.EshopUuid.INTERNETOVA_LEKAREN
import sk.hudak.prco.builder.SearchUrlBuilder
import sk.hudak.prco.parser.eshop.JSoupProductParser
import sk.hudak.prco.parser.unit.UnitParser
import sk.hudak.prco.utils.ConvertUtils
import sk.hudak.prco.utils.UserAgentDataHolder
import java.math.BigDecimal
import java.util.*
import java.util.Optional.ofNullable
import java.util.stream.Collectors

@Component
class InternetovaLekarenProductParser(unitParser: UnitParser, userAgentDataHolder: UserAgentDataHolder, searchUrlBuilder: SearchUrlBuilder)
    : JSoupProductParser(unitParser, userAgentDataHolder, searchUrlBuilder) {

    override val eshopUuid: EshopUuid
        get() = INTERNETOVA_LEKAREN

    override val timeout: Int
        get() = TIMEOUT_15_SECOND

    override fun parseCountOfPages(documentList: Document): Int {
        val i = documentList.select("div[class=indent] div[class='col col-pager text-right'] div[class=pager] > span").size - 3
        return if (i < 1) {
            1
        } else i
    }

    override fun parseUrlsOfProduct(documentList: Document, pageNumber: Int): List<String> {
        return documentList.select("#products > div > div > a").stream()
                .map { it.attr("href") }
                .filter { StringUtils.isNotBlank(it) }
                .map { eshopUuid.productStartUrl + it }
                .collect(Collectors.toList())
    }

    override fun parseProductNameFromDetail(documentDetailProduct: Document): String? {
        return ofNullable(documentDetailProduct.select("#product-detail > div > div.header.block-green.fs-large.bold.radius-top > h1").first())
                .map { it.text() }
                .orElse(null)
    }

    override fun parseProductPictureURL(documentDetailProduct: Document): Optional<String> {
        return ofNullable(documentDetailProduct.select("#productImg > img.image").first())
                .map { it.attr("src") }
                .filter { StringUtils.isNotBlank(it) }
                .map { eshopUuid.productStartUrl + it }
    }

    override fun isProductUnavailable(documentDetailProduct: Document): Boolean {
        return !Optional.ofNullable(documentDetailProduct.select("button[class='addToCartBtn btn btn-big radius plastic wood']").first())
                .isPresent
    }

    override fun parseProductPriceForPackage(documentDetailProduct: Document): Optional<BigDecimal> {
        var first: Element? = documentDetailProduct.select("strong[class=fs-xxlarge]").first()
        if (first == null) {
            first = documentDetailProduct.select("strong[class=fs-xxlarge red]").first()
        }
        return ofNullable(first)
                .map { it.text() }
                .map { StringUtils.removeEnd(it, "€") }
                .map { ConvertUtils.convertToBigDecimal(it) }
    }
}
