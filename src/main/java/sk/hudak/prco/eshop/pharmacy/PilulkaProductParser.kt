package sk.hudak.prco.eshop.pharmacy

import org.apache.commons.lang3.StringUtils
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.EshopUuid.PILULKA
import sk.hudak.prco.api.ProductAction
import sk.hudak.prco.builder.SearchUrlBuilder
import sk.hudak.prco.parser.eshop.JSoupProductParser
import sk.hudak.prco.parser.unit.UnitParser
import sk.hudak.prco.utils.ConvertUtils.convertToBigDecimal
import sk.hudak.prco.utils.JsoupUtils
import sk.hudak.prco.utils.JsoupUtils.getFirstElementByClass
import sk.hudak.prco.utils.UserAgentDataHolder
import java.math.BigDecimal
import java.util.*
import java.util.Optional.ofNullable
import kotlin.streams.toList

@Component
class PilulkaProductParser(unitParser: UnitParser, userAgentDataHolder: UserAgentDataHolder, searchUrlBuilder: SearchUrlBuilder)
    : JSoupProductParser(unitParser, userAgentDataHolder, searchUrlBuilder) {

    override val eshopUuid: EshopUuid
        get() = PILULKA

    override// koli pomalym odozvam davam na 15 sekund
    val timeout: Int
        get() = TIMEOUT_15_SECOND

    override fun parseCountOfPages(documentList: Document): Int {
        val firstElementByClass = getFirstElementByClass(documentList, "pager")
        return if (!firstElementByClass.isPresent) {
            1
        } else Integer.valueOf(firstElementByClass.get().getElementsByTag("a").last().text())
        // hodnotu z posledneho a tagu pod tagom pager
    }

    override fun parsePageForProductUrls(documentList: Document, pageNumber: Int): List<String>? {
        val allElementsOpt = Optional.ofNullable(documentList.select("div[class=product-list]").first())
                .map { it.children() }
                .map { it.first() }
                .map { it.children() }
                .map { it.select("div > h3 > a") }

        return if (!allElementsOpt.isPresent) {
            emptyList()
        } else allElementsOpt.get().stream()
                .map { it.select("div > h3 > a").first() }
                .filter { Objects.nonNull(it) }
                .map { a -> eshopUuid.productStartUrl + a.attr("href") }
                .toList()
    }

    override fun parseProductNameFromDetail(documentDetailProduct: Document): Optional<String> {
        val first = documentDetailProduct.select("div[id=product-info] > form > div > h1 > span[itemprop=name]").first()
        return if (first != null) {
            ofNullable(first.text())
        } else ofNullable(documentDetailProduct.select("span[class='product-detail__header pr-3']").first())
                .map { it.text() }
    }

    override fun isProductUnavailable(documentDetailProduct: Document): Boolean {
        var isProductUnavailable = null == documentDetailProduct.select("input[value=Kúpiť]").first()
        if (isProductUnavailable) {
            isProductUnavailable = null == documentDetailProduct.select("#js-add-to-cart-first").first()
        }
        return isProductUnavailable
    }

    override fun parseProductPriceForPackage(documentDetailProduct: Document): Optional<BigDecimal> {
        var first: Element? = documentDetailProduct.select("strong[id=priceNew]").first()
        if (first != null) {
            val substring = first.text().substring(0, first.text().length - 2)
            return Optional.of(convertToBigDecimal(substring))
        }

        first = documentDetailProduct.select("span[class='fs-28 mb-3 text-primary fwb']").first()
        return Optional.ofNullable(first)
                .map { it.text() }
                .map { StringUtils.removeEnd(it, " €") }
                .map { convertToBigDecimal(it) }
    }

    override fun parseProductPictureURL(documentDetailProduct: Document): Optional<String> {
        var first: Element? = documentDetailProduct.select("#pr-img-carousel > ul > li > a > img").first()
        if (first != null) {
            return Optional.of(eshopUuid.productStartUrl + "/" + first.attr("src"))
        }
        first = documentDetailProduct.select("div[class='product-detail__images'] > picture > a > img").first()
        if (first == null) {
            first = documentDetailProduct.select("div[class='product-detail__images w-100 js-carousel-item'] > picture > a > img").first()
        }
        return ofNullable(first)
                .map { JsoupUtils.dataSrcAttribute(it) }
                .map { it!!.substring(1) }
                .map {eshopUuid.productStartUrl + "/" + it }
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
