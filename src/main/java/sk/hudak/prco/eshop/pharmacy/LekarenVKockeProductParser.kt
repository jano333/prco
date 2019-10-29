package sk.hudak.prco.eshop.pharmacy

import org.apache.commons.lang3.StringUtils
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.EshopUuid.LEKAREN_V_KOCKE
import sk.hudak.prco.api.ProductAction
import sk.hudak.prco.builder.SearchUrlBuilder
import sk.hudak.prco.parser.eshop.JSoupProductParser
import sk.hudak.prco.parser.unit.UnitParser
import sk.hudak.prco.utils.ConvertUtils
import sk.hudak.prco.utils.JsoupUtils
import sk.hudak.prco.utils.UserAgentDataHolder
import sk.hudak.prco.utils.href
import java.math.BigDecimal
import java.util.*
import java.util.Optional.ofNullable
import kotlin.streams.toList

@Component
class LekarenVKockeProductParser(unitParser: UnitParser,
                                 userAgentDataHolder: UserAgentDataHolder,
                                 searchUrlBuilder: SearchUrlBuilder)
    : JSoupProductParser(unitParser, userAgentDataHolder, searchUrlBuilder) {

    override val eshopUuid: EshopUuid = LEKAREN_V_KOCKE

    override val timeout: Int = TIMEOUT_10_SECOND

    override fun parseCountOfPages(documentList: Document): Int {

        val first: Element? = documentList.select("div[class='content search'] > p").first()
        first?.let {
            val text = it.text()
            if (text.isNotBlank()) {
                val startIdx = text.indexOf("(")
                val endIdx = text.indexOf(")")
                if (startIdx != -1 && endIdx != -1) {
                    val countOfProductStr = text.substring(startIdx + 1, endIdx)
                    val valueOf = Integer.valueOf(countOfProductStr)
                    return JsoupUtils.calculateCountOfPages(valueOf, eshopUuid.maxCountOfProductOnPage)
                }
            }
        }
        return 1
    }

    override fun parsePageForProductUrls(documentList: Document, pageNumber: Int): List<String> {
        return documentList.select("div[class='product col-xs-6 col-xs-offset-0 col-s-6 col-s-offset-0 col-sm-3 col-sm-offset-0'] > a")
                .stream()
                .map { it.href() }
                .filter { StringUtils.isNotBlank(it) }
                .toList()
    }

    override fun parseProductNameFromDetail(documentDetailProduct: Document): Optional<String> {
        var first: Element? = documentDetailProduct.select("h1[class='product-detail-title title-xs']").first()
        if (first == null) {
            first = documentDetailProduct.select("h1[class='product-detail-title title-sm']").first()
        }

        return ofNullable(first)
                .map { it.text() }
                .filter { StringUtils.isNotBlank(it) }
    }

    override fun parseProductPictureURL(documentDetailProduct: Document): Optional<String> {
        return ofNullable(documentDetailProduct.select("img[class='img-responsive']").first())
                .map { it.attr("src") }
                .filter { StringUtils.isNotBlank(it) }
    }

    override fun isProductUnavailable(documentDetailProduct: Document): Boolean {
        var vypredane = false
        documentDetailProduct.select("td > strong > span[class='taxt-danger']")?.let {
            if ("Vypredané".equals(it.text())) {
                vypredane = true
            }
        }
        val available = !vypredane && documentDetailProduct.select("button[value='KOUPIT']").first() != null
        return !available
    }

    override fun parseProductPriceForPackage(documentDetailProduct: Document): Optional<BigDecimal> {
        return ofNullable(documentDetailProduct.select("strong[class='price-default'] span[itemprop='price']").first())
                .map { it.text() }
                .map { it.trim { it <= ' ' } }
                .filter { StringUtils.isNotBlank(it) }
                .map { ConvertUtils.convertToBigDecimal(it) }
    }

    override fun parseProductAction(documentDetailProduct: Document): Optional<ProductAction> {
        return Optional.empty()
    }

    override fun parseProductActionValidity(documentDetailProduct: Document): Optional<Date> {
        return Optional.empty()
    }
}
