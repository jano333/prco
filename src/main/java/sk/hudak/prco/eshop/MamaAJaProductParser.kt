package sk.hudak.prco.eshop

import lombok.extern.slf4j.Slf4j
import org.apache.commons.lang3.StringUtils
import org.jsoup.nodes.Document
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.EshopUuid.MAMA_A_JA
import sk.hudak.prco.api.ProductAction
import sk.hudak.prco.builder.SearchUrlBuilder
import sk.hudak.prco.dto.UnitTypeValueCount
import sk.hudak.prco.parser.UnitParser
import sk.hudak.prco.parser.impl.JSoupProductParser
import sk.hudak.prco.utils.ConvertUtils
import sk.hudak.prco.utils.UserAgentDataHolder
import java.math.BigDecimal
import java.util.*
import java.util.Optional.empty
import java.util.Optional.ofNullable

@Slf4j
//@Component
class MamaAJaProductParser(unitParser: UnitParser,
                           userAgentDataHolder: UserAgentDataHolder,
                           searchUrlBuilder: SearchUrlBuilder)
    : JSoupProductParser(unitParser, userAgentDataHolder, searchUrlBuilder) {

    override val eshopUuid: EshopUuid
        get() = MAMA_A_JA

    override val timeout: Int
        get() = TIMEOUT_10_SECOND

    override fun parseCountOfPages(documentList: Document): Int {
        val countOfProducts = ofNullable(documentList.select("#category-header > div:nth-child(1) > h1").first())
                .map { element -> element.text() }
                .filter { text -> text.contains("Vyhľadávanie (") }
                .map { text -> StringUtils.removeStart(text, "Vyhľadávanie (") }
                .map { text -> StringUtils.removeEnd(text, ")") }
                .map { text -> Integer.valueOf(text) }
                .orElse(0)

        if (countOfProducts!!.toInt() == 0) {
            return 0
        }
        var countOfPages = countOfProducts.toInt() / eshopUuid.maxCountOfProductOnPage

        if (countOfProducts.toInt() % eshopUuid.maxCountOfProductOnPage > 0) {
            countOfPages++
        }
        return countOfPages
    }

    override fun parsePageForProductUrls(documentList: Document, pageNumber: Int): List<String>? {
        val urls = ArrayList<String>()
        documentList.select("#main > div.productList.row")
                .select("div[class='col'] div[class='product indent text-center']")
                .stream()
                .forEach { element -> urls.add(eshopUuid.productStartUrl + element.attr("data-href")) }
        return urls
    }

    override fun parseProductNameFromDetail(documentDetailProduct: Document): Optional<String> {
        return ofNullable(documentDetailProduct
                .select("#product-detail > div.row > div > div > div.col.data > div > div:nth-child(1) > h1")
                .first())
                .map { it.text() }
    }

    override fun parseUnitValueCount(document: Document, productName: String): Optional<UnitTypeValueCount> {
        val text = ofNullable(document.select("div[class='slogan']").first())
                .map { it.text() }

        return if (!text.isPresent) {
            empty()
        } else unitParser.parseUnitTypeValueCount(text.get())
    }


    override fun parseProductPictureURL(documentDetailProduct: Document): Optional<String> {
        return ofNullable(documentDetailProduct.select("#productImg").first())
                .map { element -> element.attr("data-url") }
                .map { atr -> eshopUuid.productStartUrl + atr }
    }

    override fun isProductUnavailable(documentDetailProduct: Document): Boolean {
        return false == ofNullable(documentDetailProduct.select("span[class='ico-btn-cart']").first())
                .isPresent
    }

    override fun parseProductPriceForPackage(documentDetailProduct: Document): Optional<BigDecimal> {
        return ofNullable(documentDetailProduct.select("strong[itemprop='price']").first())
                .map { it.text() }
                .map { ConvertUtils.convertToBigDecimal(it) }
    }

    override fun parseProductAction(documentDetailProduct: Document): Optional<ProductAction> {
        return empty()
    }

    override fun parseProductActionValidity(documentDetailProduct: Document): Optional<Date> {
        return empty()
    }
}
