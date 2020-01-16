package sk.hudak.prco.eshop.pharmacy

import org.apache.commons.lang3.StringUtils
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.EshopUuid.LEKAREN_BELLA
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
class LekarenBellaProductParser(unitParser: UnitParser, userAgentDataHolder: UserAgentDataHolder, searchUrlBuilder: SearchUrlBuilder)
    : JSoupProductParser(unitParser, userAgentDataHolder, searchUrlBuilder) {

    override val eshopUuid: EshopUuid
        get() = LEKAREN_BELLA

    override val timeout: Int
        get() = TIMEOUT_15_SECOND

    override fun parseCountOfPages(documentList: Document): Int {
        val size = documentList.select("ul[class='control-products control-products-sm2'] > li").size
        return if (size < 1) {
            0
        } else size / 2
    }

    override fun parseUrlsOfProduct(documentList: Document, pageNumber: Int): List<String> {
        return documentList.select("div[class='product-items '] > div[class='row'] > div > a")
                .stream()
                .map { it.attr("href") }
                .toList()
    }

    override fun parseProductNameFromDetail(documentDetailProduct: Document): String? {
        var first: Element? = documentDetailProduct.select("h1[class='product-detail-title']").first()
        if (first == null) {
            first = documentDetailProduct.select("h1[class='product-detail-title title-xs']").first()
        }
        if (first == null) {
            first = documentDetailProduct.select("h1[class='product-detail-title title-sm']").first()
        }
        return ofNullable(first)
                .map { it.text() }
                .filter { StringUtils.isNotBlank(it) }
                .orElse(null)
    }

    override fun parseProductPictureURL(documentDetailProduct: Document): Optional<String> {
        val select = documentDetailProduct.select("img[class='img-responsive']")
        return ofNullable(select)
                .map { it.attr("src") }
                .filter { StringUtils.isNotBlank(it) }
    }

    override fun isProductUnavailable(documentDetailProduct: Document): Boolean {
        return null == documentDetailProduct.select("button[class='btn btn-primary btn-purchase']").first()
    }

    override fun parseProductPriceForPackage(documentDetailProduct: Document): Optional<BigDecimal> {
        return ofNullable(documentDetailProduct.select("span[itemprop='price']"))
                .map { it.text() }
                .filter { StringUtils.isNotBlank(it) }
                .map { ConvertUtils.convertToBigDecimal(it) }
    }

}
