package sk.hudak.prco.eshop.drugstore

import org.apache.commons.lang3.StringUtils
import org.jsoup.nodes.Document
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.EshopUuid.DROGERKA
import sk.hudak.prco.api.ProductAction
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
class DrogerkaProductParser(unitParser: UnitParser, userAgentDataHolder: UserAgentDataHolder, searchUrlBuilder: SearchUrlBuilder)
    : JSoupProductParser(unitParser, userAgentDataHolder, searchUrlBuilder) {

    override val eshopUuid: EshopUuid = DROGERKA

    override val timeout: Int = TIMEOUT_10_SECOND

    override fun parseCountOfPages(documentList: Document): Int {
        val size = documentList.select("ul[class='pagination'] li").size
        return if (size == 0) {
            1
        } else size - 2
    }

    override fun parseUrlsOfProduct(documentList: Document, pageNumber: Int): List<String> {
        return documentList.select("div[class='col-xs-6 col-sm-4 col-md-3'] div div[class='desc'] a")
                .stream()
                .map { it.attr("href") }
                .filter { StringUtils.isNotBlank(it) }
                .collect(Collectors.toList())
    }

    override fun parseProductNameFromDetail(documentDetailProduct: Document): String? {
        return ofNullable(documentDetailProduct.select("#product > div.text > h1").first())
                .map { it.text() }
                .filter { StringUtils.isNotBlank(it) }
                .orElse(null)
    }

    override fun parseProductPictureURL(documentDetailProduct: Document): String? {
        return ofNullable(documentDetailProduct.select("#product > div.img.thumbnails > a > img").first())
                .map { it.attr("src") }
                .filter {
                    //it.isNotBlank()
                    StringUtils.isNotBlank(it)
                } .orElse(null)
    }

    override fun isProductUnavailable(documentDetailProduct: Document): Boolean {
        return documentDetailProduct.select("#button-cart").first() == null
    }

    override fun parseProductPriceForPackage(documentDetailProduct: Document): Optional<BigDecimal> {
        return ofNullable(documentDetailProduct.select("#product > div.text > div > div.left_side > span.main").first())
                .map { it.text() }
                .map { StringUtils.removeStart(it, "Nová cena: ") }
                .map { StringUtils.removeStart(it, "Aktuálna cena: ") }
                .map { StringUtils.removeEnd(it, "€") }
                .filter { StringUtils.isNotBlank(it) }
                .map { ConvertUtils.convertToBigDecimal(it) }
    }

    override fun parseProductAction(documentDetailProduct: Document): Optional<ProductAction> {
        return Optional.empty()
    }

    override fun parseProductActionValidity(documentDetailProduct: Document): Optional<Date> {
        return Optional.empty()
    }
}
