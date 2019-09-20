package sk.hudak.prco.eshop

import org.apache.commons.lang3.StringUtils
import org.jsoup.nodes.Document
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.EshopUuid.FEEDO
import sk.hudak.prco.api.ProductAction
import sk.hudak.prco.builder.SearchUrlBuilder
import sk.hudak.prco.exception.PrcoRuntimeException
import sk.hudak.prco.parser.eshop.JSoupProductParser
import sk.hudak.prco.parser.unit.UnitParser
import sk.hudak.prco.utils.ConvertUtils.convertToBigDecimal
import sk.hudak.prco.utils.JsoupUtils.calculateCountOfPages
import sk.hudak.prco.utils.JsoupUtils.existElement
import sk.hudak.prco.utils.JsoupUtils.getTextFromFirstElementByClass
import sk.hudak.prco.utils.JsoupUtils.notExistElement
import sk.hudak.prco.utils.UserAgentDataHolder
import java.math.BigDecimal
import java.util.*
import kotlin.streams.toList

@Component
class FeedoProductParser(unitParser: UnitParser,
                         userAgentDataHolder: UserAgentDataHolder,
                         searchUrlBuilder: SearchUrlBuilder)
    : JSoupProductParser(unitParser, userAgentDataHolder, searchUrlBuilder) {

    override val eshopUuid: EshopUuid = FEEDO

    override val timeout: Int = TIMEOUT_15_SECOND

    override fun parseCountOfPages(documentList: Document): Int {
        val countOfProductString = Optional.ofNullable(documentList.select("#content > div.clearfix.mb-2 > h1:nth-child(1) > span").first())
                .map { it.text() }
                .filter { it.contains("(") && it.contains(")") }
                .map { it.substring(it.indexOf('(') + 1, it.indexOf(')')) }
                .orElseThrow { PrcoRuntimeException("None product count found for: " + documentList.location()) }

        return calculateCountOfPages(Integer.valueOf(countOfProductString), eshopUuid.maxCountOfProductOnPage)
    }

    override fun parsePageForProductUrls(documentList: Document, pageNumber: Int): List<String>? {
        return documentList.select("div[class=box-product__top]").stream()
                .map { it.select("h1 a").first() }
                .map { it.attr("href") }
                .filter { StringUtils.isNotBlank(it) }
                .toList()
    }

    override fun isProductUnavailable(documentDetailProduct: Document): Boolean {
        return notExistElement(documentDetailProduct, "button[class=btn btn-danger btn-large cart]")
    }

    override fun parseProductNameFromDetail(documentDetailProduct: Document): Optional<String> {
        return getTextFromFirstElementByClass(documentDetailProduct, "product-detail-heading hidden-xs hidden-sm")
    }

    override fun parseProductPriceForPackage(documentDetailProduct: Document): Optional<BigDecimal> {
        // premim cena
        var select = documentDetailProduct.select("div[class=price price-premium] span")
        if (select.isEmpty()) {
            // akcna cena
            select = documentDetailProduct.select("div[class=price price-discount] span")
        }
        if (select.isEmpty()) {
            // normalna cena
            select = documentDetailProduct.select("div[class=price price-base] span")
        }
        if (select.isEmpty()) {
            // novinka
            select = documentDetailProduct.select("div[class=price] span[class=price-base]")
        }
        if (select.isEmpty()) {
            // dlhodobo zlacnená cena
            select = documentDetailProduct.select("span.price.price-discount")
        }

        if (select.isEmpty()) {
            return Optional.empty()
        }

        val html = select[0].html()
        if (StringUtils.isBlank(html)) {
            return Optional.empty()
        }

        val endIndex = html.indexOf("&nbsp;")
        if (-1 == endIndex) {
            return Optional.empty()
        }

        val cenaZaBalenie = html.substring(0, endIndex)
        return Optional.of(convertToBigDecimal(cenaZaBalenie))
    }

    override fun parseProductPictureURL(documentDetailProduct: Document): Optional<String> {
        val select = documentDetailProduct.select("div[class=box-image]")
        if (select.isEmpty()) {
            return Optional.empty()
        }
        val child = select[0].child(0)
        val href = child.attr("href")
        return Optional.ofNullable(href)
    }

    override fun parseProductAction(documentDetailProduct: Document): Optional<ProductAction> {
        // premium cena
        if (existElement(documentDetailProduct, "div[class=price price-premium]")) {
            return Optional.of(ProductAction.IN_ACTION)
        }
        // akcna cena
        return if (existElement(documentDetailProduct, "div[class=price price-discount]")) {
            Optional.of(ProductAction.IN_ACTION)
        } else Optional.of(ProductAction.NON_ACTION)
    }

    override fun parseProductActionValidity(documentDetailProduct: Document): Optional<Date> {
        // feedo nepodporuje
        return Optional.empty()
    }
}
