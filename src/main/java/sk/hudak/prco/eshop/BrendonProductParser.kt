package sk.hudak.prco.eshop

import org.apache.commons.lang3.StringUtils
import org.jsoup.nodes.Document
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.EshopUuid.BRENDON
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

/**
 * problem lebo su 4 strany a mne dava stale akj pre 2 stranu produkty url z prvej strany teda mam 4 krat duplicitu a neveim preco
 */
@Component
class BrendonProductParser(unitParser: UnitParser,
                           userAgentDataHolder: UserAgentDataHolder,
                           searchUrlBuilder: SearchUrlBuilder)
    : JSoupProductParser(unitParser, userAgentDataHolder, searchUrlBuilder) {

    override val eshopUuid: EshopUuid = BRENDON

    override val timeout: Int = TIMEOUT_15_SECOND

    override fun parseCountOfPages(documentList: Document): Int {
        val first = documentList.select("span.allProductsReturned").first()
        return ofNullable(first)
                .map { it.text() }
                .map { StringUtils.removeStart(it, "1 / ") }
                .map { Integer.valueOf(it) }
                .map { JsoupUtils.calculateCountOfPages(it, eshopUuid.maxCountOfProductOnPage) }
                .orElse(1)
    }

    override fun parsePageForProductUrls(documentList: Document, pageNumber: Int): List<String> {
       log.debug("page number $pageNumber")
        log.debug(documentList.location())
        val toList = documentList.select("div.details > div.product-title.pb-2.flex-fill > h3 > a")
                .stream()
                .map { eshopUuid.productStartUrl + it.href() }
                .toList()
        log.debug(toList.toString())
        return toList
    }

    override fun parseProductNameFromDetail(documentDetailProduct: Document): Optional<String> {
        return ofNullable(documentDetailProduct.select("div.product-name > h1").first())
                .map { it.text() }
    }

    override fun parseProductPictureURL(documentDetailProduct: Document): Optional<String> {
        return ofNullable(documentDetailProduct.select("#picture-slider ul li img").first())
                .map { it.attr("data-src") }
    }

    override fun isProductUnavailable(documentDetailProduct: Document): Boolean {
        return !ofNullable(documentDetailProduct.select("div.add-to-cart-panel button[value='Pridať do košíka']").first())
                .isPresent
    }

    override fun parseProductPriceForPackage(documentDetailProduct: Document): Optional<BigDecimal> {
        val first = documentDetailProduct.select("div.product-price span").first()
        return ofNullable(first)
                .map { it.text() }
                .map { it.trim { it <= ' ' } }
                .filter { StringUtils.isNotBlank(it) }
                .map { StringUtils.removeEnd(it, " €") }
                .filter { StringUtils.isNotBlank(it) }
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
