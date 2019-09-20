package sk.hudak.prco.eshop

import org.apache.commons.lang3.StringUtils
import org.jsoup.nodes.Document
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.ProductAction
import sk.hudak.prco.builder.SearchUrlBuilder
import sk.hudak.prco.parser.eshop.JSoupProductParser
import sk.hudak.prco.parser.unit.UnitParser
import sk.hudak.prco.utils.ConvertUtils
import sk.hudak.prco.utils.UserAgentDataHolder
import sk.hudak.prco.utils.href
import sk.hudak.prco.utils.src
import java.math.BigDecimal
import java.util.*
import kotlin.streams.toList

@Component
class FarbyProductParser(unitParser: UnitParser,
                         userAgentDataHolder: UserAgentDataHolder,
                         searchUrlBuilder: SearchUrlBuilder)
    : JSoupProductParser(unitParser, userAgentDataHolder, searchUrlBuilder) {

    override val eshopUuid: EshopUuid = EshopUuid.FARBY

    override val timeout: Int = TIMEOUT_10_SECOND

    override fun parseCountOfPages(documentList: Document): Int {
        // nie je pagging
        return 1
    }

    override fun parsePageForProductUrls(documentList: Document, pageNumber: Int): List<String>? {
        return documentList.select("ol[class='product-items row'] li div div div strong a").stream()
                .map { it.href() }
                .filter { StringUtils.isNotEmpty(it) }
                .map { eshopUuid.productStartUrl + it }
                .toList()
    }

    override fun isProductUnavailable(documentDetailProduct: Document): Boolean {
        return documentDetailProduct.select("button[title='Vložiť do košíka']").first() == null
    }

    override fun parseProductNameFromDetail(documentDetailProduct: Document): Optional<String> {
        return Optional.ofNullable(documentDetailProduct.select("h1[class='page-title']").first())
                .map { it.text() }
    }

    override fun parseProductPictureURL(documentDetailProduct: Document): Optional<String> {
        val select = documentDetailProduct.select("img[id='img_zoom']")
        return Optional.ofNullable(select)
                .map { eshopUuid.productStartUrl + it.src() }
    }

    override fun parseProductPriceForPackage(documentDetailProduct: Document): Optional<BigDecimal> {
        return Optional.ofNullable(documentDetailProduct.select("span[itemprop='price']"))
                .map { ConvertUtils.convertToBigDecimal(it.text()) }
    }

    override fun parseProductAction(documentDetailProduct: Document): Optional<ProductAction> {
        return Optional.empty()
    }

    override fun parseProductActionValidity(documentDetailProduct: Document): Optional<Date> {
        return Optional.empty()
    }
}