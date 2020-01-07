package sk.hudak.prco.eshop

import org.apache.commons.lang3.StringUtils
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.EshopUuid.MAGANO
import sk.hudak.prco.api.ProductAction
import sk.hudak.prco.builder.SearchUrlBuilder
import sk.hudak.prco.parser.eshop.JSoupProductParser
import sk.hudak.prco.parser.unit.UnitParser
import sk.hudak.prco.utils.ConvertUtils
import sk.hudak.prco.utils.UserAgentDataHolder
import java.math.BigDecimal
import java.util.*
import java.util.Optional.ofNullable
import kotlin.streams.toList

@Component
class ManagoProductParser(unitParser: UnitParser, userAgentDataHolder: UserAgentDataHolder, searchUrlBuilder: SearchUrlBuilder)
    : JSoupProductParser(unitParser, userAgentDataHolder, searchUrlBuilder) {

    override val eshopUuid: EshopUuid
        get() = MAGANO

    override val timeout: Int
        get() = TIMEOUT_10_SECOND

    override fun parseCountOfPages(documentList: Document): Int {
        val size = documentList.select("ul[class='pagination'] li").size
        return if (size == 0) {
            1
        } else size - 1
    }

    override fun parseUrlsOfProduct(documentList: Document, pageNumber: Int): List<String> {
        return documentList.select("div[class='product-card'] > div > a")
                .stream()
                .map { it.attr("href") }
                .filter { StringUtils.isNotBlank(it) }
                .map { href -> eshopUuid.productStartUrl + href }
                .toList()
    }

    override fun parseProductPictureURL(documentDetailProduct: Document): Optional<String> {
        var first: Element? = documentDetailProduct.select("div[class='images single-image'] a img").first()
        if (first == null) {
            first = documentDetailProduct.select("div[class='img-holder'] img").first()
        }
        return ofNullable(first)
                .map { it.attr("src") }
                .filter { StringUtils.isNotBlank(it) }
                .map { eshopUuid.productStartUrl + it }
    }

    override fun parseProductNameFromDetail(documentDetailProduct: Document): String? {
        return ofNullable(documentDetailProduct.select("h1[itemprop='name']").first())
                .map { it.text() }
                .filter { StringUtils.isNotBlank(it) }
                .orElse(null)
    }

    override fun isProductUnavailable(documentDetailProduct: Document): Boolean {
        return null == documentDetailProduct.select("form > input[name='ok']").first()
    }

    override fun parseProductPriceForPackage(documentDetailProduct: Document): Optional<BigDecimal> {
        return ofNullable(documentDetailProduct.select("p[class='price']").first())
                .map { it.text() }
                .map { it.trim({ it <= ' ' }) }
                .filter { it.indexOf("€") != -1 }
                .map { it.substring(0, it.indexOf("€") - 1) }
                .map { ConvertUtils.convertToBigDecimal(it) }
    }

    override fun parseProductAction(documentDetailProduct: Document): Optional<ProductAction> {
        //TODO impl
        return Optional.empty()
    }

    override fun parseProductActionValidity(documentDetailProduct: Document): Optional<Date> {
        //TODO impl
        return Optional.empty()
    }
}
