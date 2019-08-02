package sk.hudak.prco.eshop

import org.apache.commons.lang3.StringUtils.removeEnd
import org.apache.commons.lang3.StringUtils.removeStart
import org.jsoup.nodes.Document
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.ProductAction
import sk.hudak.prco.builder.SearchUrlBuilder
import sk.hudak.prco.parser.UnitParser
import sk.hudak.prco.parser.impl.JSoupProductParser
import sk.hudak.prco.utils.ConvertUtils
import sk.hudak.prco.utils.UserAgentDataHolder
import java.math.BigDecimal
import java.util.*
import java.util.Optional.ofNullable
import kotlin.streams.toList

@Component
class FourKidsProductParser(unitParser: UnitParser, userAgentDataHolder: UserAgentDataHolder, searchUrlBuilder: SearchUrlBuilder) : JSoupProductParser(unitParser, userAgentDataHolder, searchUrlBuilder) {

    override val eshopUuid: EshopUuid
        get() = EshopUuid.FOUR_KIDS

    override val timeout: Int
        get() = TIMEOUT_10_SECOND

    override fun parseCountOfPages(documentList: Document): Int {
        val countOfProductsOpt = ofNullable(documentList.select("body > div.inner.relative > div > div > div:nth-child(5) > div.col-md-4.hidden-xs.hidden-sm > span").first())
                .map { it.text() }
                .filter { it.indexOf(" z ") != -1 }
                .map { removeEnd(it, "produktov") }
                .map { removeEnd(it, "produktů") }
                .map { removeStart(it, it.substring(0, it.indexOf(" z ") + 3)) }
                .map { it.trim { it <= ' ' } }
                .map { Integer.valueOf(it) }

        if (!countOfProductsOpt.isPresent) {
            return 1
        }

        val hh = countOfProductsOpt.get() % eshopUuid.maxCountOfProductOnPage

        return if (hh > 0) {
            countOfProductsOpt.get() + 1
        } else {
            countOfProductsOpt.get()
        }
    }

    override fun parsePageForProductUrls(documentList: Document, pageNumber: Int): List<String>? {
        return documentList.select("#products-list > div > a").stream()
                .map { element -> eshopUuid.productStartUrl + element.attr("href") }
                .toList()
    }

    override fun parseProductNameFromDetail(documentDetailProduct: Document): Optional<String> {
        return ofNullable(documentDetailProduct.select("div.product-detail > div.col-xs-12.col-md-7 > h1").first())
                .map { it.text() }
    }

    override fun parseProductPictureURL(documentDetailProduct: Document): Optional<String> {
        return ofNullable(documentDetailProduct.select("div.product-detail > div.col-xs-12.col-md-7 > div.img-detail.relative.text-center > a > img").first())
                .map { element -> element.attr("src") }
    }

    override fun isProductUnavailable(documentDetailProduct: Document): Boolean {
        return !ofNullable(documentDetailProduct.select("button[class='insert-cart cart']").first())
                .isPresent
    }

    override fun parseProductPriceForPackage(documentDetailProduct: Document): Optional<BigDecimal> {
        return Optional.ofNullable(documentDetailProduct.select("p[class='price']").first())
                .map { it.text() }
                .map { removeEnd(it, " €") }
                .map { ConvertUtils.convertToBigDecimal(it) }
    }

    override fun parseProductAction(documentDetailProduct: Document): Optional<ProductAction> {
        return Optional.empty()
    }

    override fun parseProductActionValidity(documentDetailProduct: Document): Optional<Date> {
        return Optional.empty()
    }
}
