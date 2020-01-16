package sk.hudak.prco.eshop.pharmacy

import org.apache.commons.lang3.StringUtils
import org.jsoup.nodes.Document
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.EshopUuid.GIGA_LEKAREN
import sk.hudak.prco.builder.SearchUrlBuilder
import sk.hudak.prco.parser.eshop.JSoupProductParser
import sk.hudak.prco.parser.unit.UnitParser
import sk.hudak.prco.utils.ConvertUtils
import sk.hudak.prco.utils.UserAgentDataHolder
import java.math.BigDecimal
import java.util.*
import java.util.Optional.ofNullable
import java.util.stream.Collectors

@Component
class GigaLekarenProductParser(unitParser: UnitParser,
                               userAgentDataHolder: UserAgentDataHolder,
                               searchUrlBuilder: SearchUrlBuilder)
    : JSoupProductParser(unitParser, userAgentDataHolder, searchUrlBuilder) {

    override val eshopUuid: EshopUuid = GIGA_LEKAREN

    override val timeout: Int = TIMEOUT_15_SECOND

    override fun parseCountOfPages(documentList: Document): Int {
        val size = documentList.select("div[class=paging_footer] a").size
        val i = size / 2 - 2
        return if (i < 1) {
            1
        } else i
    }

    override fun parseUrlsOfProduct(documentList: Document, pageNumber: Int): List<String> {
        return documentList.select("div[class=top_left] p[class=product_title] a").stream()
                .map { element -> element.attr("href") }
                .filter { StringUtils.isNotBlank(it) }
                .collect(Collectors.toList())
    }

    override fun parseProductNameFromDetail(documentDetailProduct: Document): String? {
        return ofNullable(documentDetailProduct.select("#rw_det1 > table > tbody > tr:nth-child(1) > td:nth-child(2) > strong"))
                .map { it.text() }
                .orElse(null)
    }

    override fun parseProductPictureURL(documentDetailProduct: Document): String? {
        return ofNullable(documentDetailProduct.select("#thephoto"))
                .map { it.attr("src") }
                .filter { StringUtils.isNotBlank(it) }
                .orElse(null)
    }

    override fun isProductUnavailable(documentDetailProduct: Document): Boolean {
        return null == documentDetailProduct.select("#detail_block_form_cart").first()
    }

    override fun parseProductPriceForPackage(documentDetailProduct: Document): Optional<BigDecimal> {
        return ofNullable(documentDetailProduct.select("td.color > strong"))
                .map { it.text() }
                .filter { StringUtils.isNotBlank(it) }
                .map { StringUtils.removeEnd(it, " €") }
                .map { ConvertUtils.convertToBigDecimal(it) }
    }
}
