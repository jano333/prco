package sk.hudak.prco.eshop

import org.apache.commons.lang3.StringUtils
import org.jsoup.nodes.Document
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.EshopUuid.PERINBABA
import sk.hudak.prco.api.ProductAction
import sk.hudak.prco.builder.SearchUrlBuilder
import sk.hudak.prco.parser.eshop.JSoupProductParser
import sk.hudak.prco.parser.unit.UnitParser
import sk.hudak.prco.utils.ConvertUtils
import sk.hudak.prco.utils.UserAgentDataHolder
import java.math.BigDecimal
import java.util.*

@Component
class PerinbabaProductParser(unitParser: UnitParser, userAgentDataHolder: UserAgentDataHolder, searchUrlBuilder: SearchUrlBuilder)
    : JSoupProductParser(unitParser, userAgentDataHolder, searchUrlBuilder) {

    override val eshopUuid: EshopUuid
        get() = PERINBABA

    override val timeout: Int
        get() = TIMEOUT_10_SECOND

    override fun parseCountOfPages(documentList: Document): Int {
        val element = documentList.select("div[class=pages] ol").first() ?: return 1
        val size = element.children().size
        return if (size == 0) {
            1
        } else size - 1
    }

    override fun parsePageForProductUrls(documentList: Document, pageNumber: Int): List<String>? {
        val urls = ArrayList<String>()
        documentList.select("div[class=category-products] ul").stream()
                .filter { element -> Objects.nonNull(element.attr("products-grid")) }
                .forEach { element ->
                    val aElement = element.select("li[class=item first] h2 a").first()
                    if (aElement != null) {
                        val href = aElement.attr("href")
                        urls.add(href)
                    }
                }
        return urls
    }

    override fun parseProductNameFromDetail(documentDetailProduct: Document): Optional<String> {
        val first = documentDetailProduct.select("#product_addtocart_form > div.product-shop > div > div.product-name")
                .first() ?: return Optional.empty()
        val first1 = first.children().first() ?: return Optional.empty()
        return Optional.of(first1.text())
    }

    override fun parseProductPictureURL(documentDetailProduct: Document): Optional<String> {
        val first = documentDetailProduct.select("div.prolabel-wrapper > a > img").first() ?: return Optional.empty()
        return Optional.of(first.attr("src"))
    }

    override fun isProductUnavailable(documentDetailProduct: Document): Boolean {
        return documentDetailProduct.select("button[title=Kúpiť]").first() == null
    }

    override fun parseProductPriceForPackage(documentDetailProduct: Document): Optional<BigDecimal> {
        return Optional.ofNullable(documentDetailProduct.select("span[class=price]").first())
                .map { StringUtils.removeEnd(it.text(), " €") }
                .map { ConvertUtils.convertToBigDecimal(it) }
    }

    override fun parseProductAction(documentDetailProduct: Document): Optional<ProductAction> {
        //TODO impl
        return Optional.empty()
    }

    override fun parseProductActionValidity(documentDetailProduct: Document): Optional<Date> {
        //TODO impl
        return Optional.empty()
    }
}
