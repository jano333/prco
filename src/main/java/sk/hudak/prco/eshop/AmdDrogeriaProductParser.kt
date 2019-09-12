package sk.hudak.prco.eshop

import org.apache.commons.lang3.StringUtils
import org.jsoup.nodes.Document
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.EshopUuid.AMD_DROGERIA
import sk.hudak.prco.api.ProductAction
import sk.hudak.prco.builder.SearchUrlBuilder
import sk.hudak.prco.parser.eshop.JSoupProductParser
import sk.hudak.prco.parser.unit.UnitParser
import sk.hudak.prco.utils.ConvertUtils
import sk.hudak.prco.utils.UserAgentDataHolder
import java.math.BigDecimal
import java.util.*
import java.util.stream.Collectors

@Component
class AmdDrogeriaProductParser(unitParser: UnitParser,
                               userAgentDataHolder: UserAgentDataHolder,
                               searchUrlBuilder: SearchUrlBuilder)
    : JSoupProductParser(unitParser, userAgentDataHolder, searchUrlBuilder) {

    override val eshopUuid: EshopUuid = AMD_DROGERIA

    override val timeout: Int = TIMEOUT_10_SECOND

    override fun parseCountOfPages(documentList: Document): Int {
        val first = Optional.ofNullable(documentList.select("div.searching__toolbar-bottom > div > ul").first())
        if (!first.isPresent) {
            return 1
        }
        // li elements
        val children = first.get().children()
        if (children.size < 3) {
            return 1
        }
        val element = children[children.size - 3]
        val text = element.text()
        return Integer.valueOf(text)
    }

    override fun parsePageForProductUrls(documentList: Document, pageNumber: Int): List<String>? {
        return documentList.select("div.product-sm__name > h2 > a").stream()
                .map { element -> element.attr("href") }
                .filter { StringUtils.isNotBlank(it) }
                .map { href -> eshopUuid.productStartUrl + href }
                .collect(Collectors.toList())
    }

    override fun isProductUnavailable(documentDetailProduct: Document): Boolean {
        return documentDetailProduct.select("div.product__buy div.product__add-to-cart > a").isEmpty()
    }

    override fun parseProductNameFromDetail(documentDetailProduct: Document): Optional<String> {
        return Optional.ofNullable(documentDetailProduct.select("div.product__title > h1").first())
                .map { it.text() }
    }

    override fun parseProductPictureURL(documentDetailProduct: Document): Optional<String> {
        return Optional.ofNullable(documentDetailProduct.select("body > section.product > div > div > div.product__left-column.col-lg-6 > div > a > img").first())
                .map { element -> element.attr("src") }
                .filter { StringUtils.isNotBlank(it) }
                .map { src -> eshopUuid.productStartUrl + src }
    }

    override fun parseProductPriceForPackage(documentDetailProduct: Document): Optional<BigDecimal> {
        return Optional.ofNullable(documentDetailProduct.select("div.product__price > span").first())
                .map { it.text() }
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
