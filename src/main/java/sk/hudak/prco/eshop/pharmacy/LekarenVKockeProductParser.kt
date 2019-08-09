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
import sk.hudak.prco.utils.UserAgentDataHolder
import java.math.BigDecimal
import java.util.*
import java.util.Optional.ofNullable
import kotlin.streams.toList

@Component
class LekarenVKockeProductParser(unitParser: UnitParser, userAgentDataHolder: UserAgentDataHolder, searchUrlBuilder: SearchUrlBuilder)
    : JSoupProductParser(unitParser, userAgentDataHolder, searchUrlBuilder) {

    override val eshopUuid: EshopUuid
        get() = LEKAREN_V_KOCKE

    override val timeout: Int
        get() = TIMEOUT_10_SECOND

    override fun parseCountOfPages(documentList: Document): Int {
        val size = documentList.select("nav > ul > li").size
        return if (size == 0) {
            1
        } else size / 2
    }

    override fun parsePageForProductUrls(documentList: Document, pageNumber: Int): List<String>? {
        return documentList.select("div[class='product col-xs-12 col-xs-offset-0 col-s-6 col-s-offset-0 col-sm-3 col-sm-offset-0'] > a")
                .stream()
                .map { it.attr("href") }
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
        return !ofNullable(documentDetailProduct.select("button[value='KOUPIT']").first())
                .isPresent
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
