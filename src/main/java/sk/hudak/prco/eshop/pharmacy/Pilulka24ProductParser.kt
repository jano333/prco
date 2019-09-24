package sk.hudak.prco.eshop.pharmacy

import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.math.NumberUtils
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.EshopUuid.PILULKA_24
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
import java.util.stream.Collectors

@Component
class Pilulka24ProductParser(unitParser: UnitParser,
                             userAgentDataHolder: UserAgentDataHolder,
                             searchUrlBuilder: SearchUrlBuilder)
    : JSoupProductParser(unitParser, userAgentDataHolder, searchUrlBuilder) {

    override val eshopUuid: EshopUuid = PILULKA_24

    // koli pomalym odozvam davam na 15 sekund
    override val timeout: Int = TIMEOUT_15_SECOND

    override fun parseCountOfPages(documentList: Document): Int {
        return ofNullable(documentList.select("#js-product-list-content > p").first())
                .map { it.text() }
                .filter{ StringUtils.isNotBlank(it) }
                .map { text -> text.substring(0, text.indexOf(' ')) }
                .filter{ NumberUtils.isParsable(it) }
                .map { Integer.valueOf(it) }
                .map { JsoupUtils.calculateCountOfPages(it, eshopUuid.maxCountOfProductOnPage) }
                .orElse(SINGLE_PAGE_ONE)
    }

    override fun parsePageForProductUrls(documentList: Document, pageNumber: Int): List<String>? {
        return documentList.select("div[class='col-6 col-sm-4 col-lg-3 p-0 product-card__border'] > div:nth-child(1) > div > a").stream()
                .map { it.href() }
                .filter{ StringUtils.isNotBlank(it) }
                .collect(Collectors.toSet())// aby som zo 120 urobil 40 lebo je duplicita a neviem cez selector urobit
                .stream()
                .filter{StringUtils.isNoneBlank(it)}
                .map { it!!.substring(1) }
                .map { eshopUuid.productStartUrl + it }
                .collect(Collectors.toList())
    }

    override fun parseProductNameFromDetail(documentDetailProduct: Document): Optional<String> {
        return ofNullable(documentDetailProduct.select("span[class='product-detail__header pr-3']").first())
                .map{ it.text() }
    }

    override fun parseProductPictureURL(documentDetailProduct: Document): Optional<String> {
        var first: Element? = documentDetailProduct.select("div[class='product-detail__images'] > picture > a > img").first()
        if (first == null) {
            first = documentDetailProduct.select("div[class='product-detail__images w-100 js-carousel-item'] > picture > a > img").first()
        }
        return ofNullable(first)
                .map { JsoupUtils.dataSrcAttribute(it) }
    }

    override fun isProductUnavailable(documentDetailProduct: Document): Boolean {
        return documentDetailProduct.select("#js-add-to-cart-first").first() == null
    }

    override fun parseProductPriceForPackage(documentDetailProduct: Document): Optional<BigDecimal> {
        return ofNullable(documentDetailProduct.select("span[class='fs-28 mb-3 text-primary fwb']").first())
                .map { it.text() }
                .map { StringUtils.removeEnd(it, " €") }
                .map { ConvertUtils.convertToBigDecimal(it) }
    }

    override fun parseProductAction(documentDetailProduct: Document): Optional<ProductAction> {
        return Optional.empty()
    }

    override fun parseProductActionValidity(documentDetailProduct: Document): Optional<Date> {
        return Optional.empty()
    }
}
