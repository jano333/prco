package sk.hudak.prco.eshop.drugstore

import org.apache.commons.lang3.StringUtils
import org.jsoup.nodes.Document
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.EshopUuid.DROGERIA_VMD
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
class DrogeriaVmdProductParser(unitParser: UnitParser,
                               userAgentDataHolder: UserAgentDataHolder,
                               searchUrlBuilder: SearchUrlBuilder)
    : JSoupProductParser(unitParser, userAgentDataHolder, searchUrlBuilder) {

    companion object {
        private val ZERO = Integer.valueOf(0)
    }

    override val eshopUuid: EshopUuid = DROGERIA_VMD

    override val timeout: Int = TIMEOUT_15_SECOND

    override fun parseCountOfPages(documentList: Document): Int {
        return ofNullable(documentList.select("span.pages a").last())
                .map { it.text() }
                .filter { StringUtils.isNotBlank(it) }
                .map { Integer.valueOf(it) }
                .orElse(ZERO)
    }

    override fun parseUrlsOfProduct(documentList: Document, pageNumber: Int): List<String> {
        return documentList.select("div.produkt > div.inner > a.name").stream()
                .map { element -> element.attr("href") }
                .filter { StringUtils.isNotBlank(it) }
                .collect(Collectors.toList())
    }

    override fun isProductUnavailable(documentDetailProduct: Document): Boolean {
        return !ofNullable(documentDetailProduct.select("button[class='.btn koupit_detail_gb']")).isPresent
    }

    override fun parseProductNameFromDetail(documentDetailProduct: Document): String? {
        return ofNullable(documentDetailProduct.select("#content > div.rightSide > div:nth-child(2) > h1"))
                .map { it.text() }
                .orElse(null)
    }

    override fun parseProductPictureURL(documentDetailProduct: Document): Optional<String> {
        return ofNullable(documentDetailProduct.select("#detail_image"))
                .map { elements -> elements.attr("src") }
    }

    override fun parseProductPriceForPackage(documentDetailProduct: Document): Optional<BigDecimal> {
        return ofNullable(documentDetailProduct.select("#detail_cenas"))
                .map { it.text() }
                .filter { StringUtils.isNotBlank(it) }
                .map { ConvertUtils.convertToBigDecimal(it) }
    }

}
