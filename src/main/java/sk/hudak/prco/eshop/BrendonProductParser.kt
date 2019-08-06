package sk.hudak.prco.eshop

import org.apache.commons.lang3.StringUtils
import org.jsoup.nodes.Document
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.EshopUuid.BRENDON
import sk.hudak.prco.api.ProductAction
import sk.hudak.prco.builder.SearchUrlBuilder
import sk.hudak.prco.parser.UnitParser
import sk.hudak.prco.parser.impl.JSoupProductParser
import sk.hudak.prco.utils.ConvertUtils
import sk.hudak.prco.utils.UserAgentDataHolder
import java.math.BigDecimal
import java.util.*
import java.util.Optional.ofNullable
import java.util.stream.Collectors

@Component
class BrendonProductParser(unitParser: UnitParser,
                           userAgentDataHolder: UserAgentDataHolder,
                           searchUrlBuilder: SearchUrlBuilder)
    : JSoupProductParser(unitParser, userAgentDataHolder, searchUrlBuilder) {

    override val eshopUuid: EshopUuid
        get() = BRENDON

    override val timeout: Int
        get() = TIMEOUT_15_SECOND

    override fun parseCountOfPages(documentList: Document): Int {
        return ofNullable(documentList.select("ul[class='pagermenu'] li[class='bluelink'] span").first())
                .map { it.text() }
                .map { text -> StringUtils.removeStart(text, "1 / ") }
                .map { Integer.valueOf(it) }
                .orElse(1)
    }

    override fun parsePageForProductUrls(documentList: Document, pageNumber: Int): List<String>? {
        return documentList.select("body > div.maincontent.clear > div > div.col700_container > div > div.middle-left_ > div > a")
                .stream()
                .map { element -> eshopUuid.productStartUrl + element.attr("href") }
                .collect(Collectors.toList())
    }

    override fun parseProductNameFromDetail(documentDetailProduct: Document): Optional<String> {
        return Optional.ofNullable(documentDetailProduct.select("div.product-name > h1").first())
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
                .map { text -> StringUtils.removeEnd(text, " €") }
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
