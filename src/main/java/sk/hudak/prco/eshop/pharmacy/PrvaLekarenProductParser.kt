package sk.hudak.prco.eshop.pharmacy

import org.apache.commons.lang3.StringUtils
import org.jsoup.nodes.Document
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.EshopUuid.PRVA_LEKAREN
import sk.hudak.prco.api.ProductAction
import sk.hudak.prco.builder.SearchUrlBuilder
import sk.hudak.prco.parser.eshop.JSoupProductParser
import sk.hudak.prco.parser.unit.UnitParser
import sk.hudak.prco.utils.ConvertUtils
import sk.hudak.prco.utils.UserAgentDataHolder
import java.math.BigDecimal
import java.util.*
import java.util.Optional.ofNullable
import javax.validation.constraints.NotNull
import kotlin.streams.toList

@Component
class PrvaLekarenProductParser(unitParser: UnitParser, userAgentDataHolder: UserAgentDataHolder, @NotNull searchUrlBuilder: SearchUrlBuilder) : JSoupProductParser(unitParser, userAgentDataHolder, searchUrlBuilder) {

    override val eshopUuid: EshopUuid
        get() = PRVA_LEKAREN

    override val timeout: Int
        get() = TIMEOUT_10_SECOND

    override fun parseCountOfPages(documentList: Document): Int {
        val countOfPages = documentList.select("div[class=pagination] a").size - 2
        return if (countOfPages < 1) {
            1
        } else countOfPages
    }

    override fun parsePageForProductUrls(documentList: Document, pageNumber: Int): List<String> {
        val href1 = documentList.select("div[class=productbox ] > a:nth-child(1)").stream()
                .map { it.attr("href") }
                .filter { StringUtils.isNotBlank(it) }
                .toList()
        val href2 = documentList.select("div[class=productbox last] > a:nth-child(1)").stream()
                .map { it.attr("href") }
                .filter { StringUtils.isNotBlank(it) }
                .toList()

        val hrefs = ArrayList(href1)
        hrefs.addAll(href2)
        return hrefs
    }

    override fun parseProductNameFromDetail(documentDetailProduct: Document): Optional<String> {
        return ofNullable(documentDetailProduct.select("div.detail > div.right > h1"))
                .map { it.text() }
    }

    override fun parseProductPictureURL(documentDetailProduct: Document): Optional<String> {
        return Optional.ofNullable(documentDetailProduct.select("div.detail > div.left > img"))
                .map { it.attr("src") }
                .filter { StringUtils.isNotBlank(it) }
    }

    override fun isProductUnavailable(documentDetailProduct: Document): Boolean {
        return documentDetailProduct.select("tr.koupit > td:nth-child(2) > input[type='submit']").first() == null
    }

    override fun parseProductPriceForPackage(documentDetailProduct: Document): Optional<BigDecimal> {
        return Optional.ofNullable(documentDetailProduct.select("td.cena"))
                .map { it.text() }
                .map { StringUtils.trim(it) }
                .filter { StringUtils.isNotBlank(it) }
                .map { StringUtils.removeEnd(it, " €") }
                .map { ConvertUtils.convertToBigDecimal(it) }
    }

    override fun parseProductAction(documentDetailProduct: Document): Optional<ProductAction> {


        // TODO
        return Optional.empty()

    }

    override fun parseProductActionValidity(documentDetailProduct: Document): Optional<Date> {
        // TODO
        return Optional.empty()
    }
}
