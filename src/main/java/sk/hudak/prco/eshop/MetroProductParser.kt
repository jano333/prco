package sk.hudak.prco.eshop

import org.apache.commons.lang3.StringUtils
import org.apache.commons.lang3.math.NumberUtils
import org.jsoup.nodes.Document
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.EshopUuid.METRO
import sk.hudak.prco.api.ProductAction
import sk.hudak.prco.api.ProductAction.IN_ACTION
import sk.hudak.prco.api.ProductAction.NON_ACTION
import sk.hudak.prco.builder.SearchUrlBuilder
import sk.hudak.prco.parser.eshop.JSoupProductParser
import sk.hudak.prco.parser.unit.UnitParser
import sk.hudak.prco.utils.ConvertUtils.convertToBigDecimal
import sk.hudak.prco.utils.UserAgentDataHolder
import sk.hudak.prco.utils.href
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*
import kotlin.streams.toList

@Component
class MetroProductParser(unitParser: UnitParser,
                         userAgentDataHolder: UserAgentDataHolder,
                         searchUrlBuilder: SearchUrlBuilder)
    : JSoupProductParser(unitParser, userAgentDataHolder, searchUrlBuilder) {

    override val eshopUuid: EshopUuid = METRO
    override val timeout: Int = TIMEOUT_15_SECOND

    // 21 su Kosice pobocka
    override val requestCookie: Map<String, String> = Collections.singletonMap("storeId", "21")

    override fun parseCountOfPages(documentList: Document): Int {
        return if (documentList.select("p[class='mo-pagination-status']").first() != null) 1 else 0
    }

    override fun parseUrlsOfProduct(documentList: Document, pageNumber: Int): List<String> {
        return documentList.select("div.product-layer-content div.product-info-wrapper a.product-photo")
                .stream()
                .map { it.href() }
                .filter { it.isNotBlank() }
                .toList()
    }

    override fun parseProductNameFromDetail(documentDetailProduct: Document): String? {
        return documentDetailProduct.select("h1.product-title").text()
    }

    override fun parseProductPictureURL(documentDetailProduct: Document): String? {
        val href = documentDetailProduct.select("a.product-photo").href()
        if (href.isBlank()) {
            return null
        }
        return Optional.of(href) .orElse(null)
    }

    override fun isProductUnavailable(documentDetailProduct: Document): Boolean {
        // https://sortiment.metro.sk/sk/pampers-abd-mb-s3-208ks/241758p/
        val select = documentDetailProduct.select("div.product-nav-row.product-nav-info span:nth-child(2)")
                .text()
        if (select.isBlank()) {
            return true
        }
        var remove = StringUtils.remove(select, "Na predajni: ")
        remove = StringUtils.remove(remove, " bal.")
        if (!NumberUtils.isDigits(remove)) {
            return true
        }
        return false
    }

    override fun parseProductPriceForPackage(documentDetailProduct: Document): Optional<BigDecimal> {
        // prepocet DPH lebo je to cena bez DPH
        val firstOrNull = documentDetailProduct.select("div.product-price-value > strong")
                .map { it.text() }
                .filter { it.isNotBlank() }
                .map { StringUtils.remove(it, " €") }
                .map { it.replace(",", ".") }
                .filter { NumberUtils.isParsable(it) }
                .map { convertToBigDecimal(it) }
                .firstOrNull()

        val let = firstOrNull?.let {
            var result = it.plus(it.multiply(BigDecimal(0.2)))
            result = result.setScale(2, RoundingMode.HALF_UP)
            result
        }

        return Optional.ofNullable(let)
    }

    override fun parseProductAction(documentDetailProduct: Document): Optional<ProductAction> {
        //TODO
        val select = documentDetailProduct.select("span[class=mic mic-action-sk is-32]")
        return Optional.of(if (select.isEmpty())
            NON_ACTION
        else
            IN_ACTION)
    }

    override fun parseProductActionValidity(documentDetailProduct: Document): Optional<Date> {
        //TODO impl action validity
        return Optional.empty()
    }
}
