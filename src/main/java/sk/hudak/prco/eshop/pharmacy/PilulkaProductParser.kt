package sk.hudak.prco.eshop.pharmacy

import org.apache.commons.lang3.StringUtils
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.EshopUuid.PILULKA
import sk.hudak.prco.builder.SearchUrlBuilder
import sk.hudak.prco.parser.eshop.JSoupProductParser
import sk.hudak.prco.parser.unit.UnitParser
import sk.hudak.prco.utils.ConvertUtils.convertToBigDecimal
import sk.hudak.prco.utils.JsoupUtils
import sk.hudak.prco.utils.UserAgentDataHolder
import sk.hudak.prco.utils.href
import java.math.BigDecimal
import java.util.*
import java.util.Optional.of
import java.util.Optional.ofNullable

@Component
class PilulkaProductParser(unitParser: UnitParser,
                           userAgentDataHolder: UserAgentDataHolder,
                           searchUrlBuilder: SearchUrlBuilder)
    : JSoupProductParser(unitParser, userAgentDataHolder, searchUrlBuilder) {

    override val eshopUuid: EshopUuid = PILULKA

    // koli pomalym odozvam davam na 15 sekund
    override val timeout: Int = TIMEOUT_15_SECOND

    override fun parseCountOfPages(document: Document): Int {
        val text = document.select("#js-product-list-content > p").first()
                ?.text()
        if (text == null || text.isBlank()) {
            return 1
        }
        val pocetProduktov = StringUtils.remove(text, " produktov").toInt()
        return JsoupUtils.calculateCountOfPages(pocetProduktov, eshopUuid.maxCountOfProductOnPage)
    }

    override fun parseUrlsOfProduct(documentList: Document, pageNumber: Int): List<String> {
        val select = documentList.select("h2[class='p-0 my-2'] > a")
        return select
                .map { it.href() }
                .filter { it.isNotBlank() }
                .map { eshopUuid.productStartUrl + it }
                .toList()
    }

    override fun parseProductNameFromDetail(documentDetailProduct: Document): String? {
        val first = documentDetailProduct.select("div[id=product-info] > form > div > h1 > span[itemprop=name]").first()
        return if (first != null) {
            ofNullable(first.text()).orElse(null)
        } else ofNullable(documentDetailProduct.select("span[class='product-detail__header pr-3']").first())
                .map { it.text() }
                .orElse(null)
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
            return of(convertToBigDecimal(substring))
        }

        first = documentDetailProduct.select("span[class='fs-28 mb-3 text-primary fwb']").first()
        return ofNullable(first)
                .map { it.text() }
                .map { StringUtils.removeEnd(it, " €") }
                .map { convertToBigDecimal(it) }
    }

    override fun parseProductPictureURL(documentDetailProduct: Document): String? {
        // 1 scenar
        val result: Optional<String> = ofNullable(documentDetailProduct.select("div[class='product-detail__images'] > picture > a > img").first())
                .map { JsoupUtils.dataSrcAttribute(it) }
        if (result.isPresent) {
            return result.orElse(null)
        }

        // 2 scenar
        val let: String? = documentDetailProduct.select("#js-product-carousel > div:nth-child(1) > picture > a > img")
                .first()
                ?.let {
                    JsoupUtils.dataSrcAttribute(it)
                }
        if (let != null && let.isNotBlank()) {
            return of(let).orElse(null)
        }

        // ak ani jeden scenar
        return null

    }
}
