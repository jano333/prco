package sk.hudak.prco.eshop

import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.StringUtils.removeStart
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.EshopUuid.MAXIKOVY_HRACKY
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
class MaxikovyHrackyProductParser(unitParser: UnitParser, userAgentDataHolder: UserAgentDataHolder, searchUrlBuilder: SearchUrlBuilder)
    : JSoupProductParser(unitParser, userAgentDataHolder, searchUrlBuilder) {

    override val eshopUuid: EshopUuid
        get() = MAXIKOVY_HRACKY

    override// koli pomalym odozvam davam na 10 sekund
    val timeout: Int
        get() = TIMEOUT_10_SECOND

    override fun parseCountOfPages(documentList: Document): Int {
        //Optional[Zobrazuji 1-60 z 807 produktů]
        val countOfProductsOpt = ofNullable(documentList.select("div.line-sort > div > div.col-sm-4.text-right.top-12.font-12").first())
                .map { it.text() }
                .map { StringUtils.removeEnd(it, " produktů") }
                .map { removeStart(it, it.substring(0, it.indexOf(" z ") + 3)) }
                .map { it.trim({ it <= ' ' }) }
                .map { Integer.valueOf(it) }

        if (!countOfProductsOpt.isPresent) {
            return 1
        }
        //TODO podla mna to je ZLE pozri Feedo
        val hh = countOfProductsOpt.get() % eshopUuid.maxCountOfProductOnPage

        return if (hh > 0) {
            countOfProductsOpt.get() + 1
        } else {
            countOfProductsOpt.get()
        }
    }

    override fun parsePageForProductUrls(documentList: Document, pageNumber: Int): List<String>? {
        return documentList.select("#product-list-box > div[class='col-sm-4 col-lg-2-4']").stream()
                .map { it.select("div > div > a").first() }
                .map { eshopUuid.productStartUrl + it.attr("href") }
                .map { "$it?zmena_meny=EUR" }
                .toList()
    }

    override fun parseProductNameFromDetail(documentDetailProduct: Document): Optional<String> {
        return ofNullable(documentDetailProduct.select("#product-info > div.col-xs-12.col-md-5 > h1").first())
                .map { it.text() }
    }

    override fun parseProductPictureURL(documentDetailProduct: Document): Optional<String> {
        return ofNullable(documentDetailProduct.select("#product-info > div.col-xs-12.col-md-7 > div.main-image > a > img").first())
                .map { element -> element.attr("src") }
    }

    override fun isProductUnavailable(documentDetailProduct: Document): Boolean {
        return !ofNullable(documentDetailProduct.select("button[class='insert-cart btn btn-default']").first())
                .isPresent
    }

    override fun parseProductPriceForPackage(documentDetailProduct: Document): Optional<BigDecimal> {
        var first: Element? = documentDetailProduct.select("div[class='price text-red text-right']").first()
        if (first == null) {
            first = documentDetailProduct.select("div[class='price text-red text-right simple']").first()
        }
        return ofNullable(first)
                .map { it.text() }
                .map { StringUtils.removeEnd(it, " €") }
                .map { ConvertUtils.convertToBigDecimal(it) }
    }

    override fun parseProductAction(documentDetailProduct: Document): Optional<ProductAction> {
        //TODO
        return Optional.empty()
    }

    override fun parseProductActionValidity(documentDetailProduct: Document): Optional<Date> {
        //TODO
        return Optional.empty()
    }
}
