package sk.hudak.prco.eshop.pharmacy

import org.apache.commons.lang3.StringUtils
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.EshopUuid.MOJA_LEKAREN
import sk.hudak.prco.builder.SearchUrlBuilder
import sk.hudak.prco.parser.eshop.JSoupProductParser
import sk.hudak.prco.parser.unit.UnitParser
import sk.hudak.prco.utils.ConvertUtils
import sk.hudak.prco.utils.JsoupUtils
import sk.hudak.prco.utils.UserAgentDataHolder
import java.math.BigDecimal
import java.util.*
import java.util.Optional.ofNullable

@Component
class MojaLekarenProductParser(unitParser: UnitParser,
                               userAgentDataHolder: UserAgentDataHolder,
                               searchUrlBuilder: SearchUrlBuilder)
    : JSoupProductParser(unitParser, userAgentDataHolder, searchUrlBuilder) {

    override val eshopUuid: EshopUuid = MOJA_LEKAREN

    // koli pomalym odozvam davam na 15 sekund
    override val timeout: Int = TIMEOUT_15_SECOND

    override fun parseCountOfPages(documentList: Document): Int {
        val select = documentList.select("p.pagination__part.pagination__part--page").first() ?: return SINGLE_PAGE_ONE
        return Integer.valueOf(select.children().last().previousElementSibling().previousElementSibling().text())
    }

    override fun parseUrlsOfProduct(documentList: Document, pageNumber: Int): List<String> {
        val urls = ArrayList<String>()
        for (element in documentList.select("li[class=product]")) {
            val aHref = element.child(0).child(0)
            val href = aHref.attr("href")
            urls.add(eshopUuid.productStartUrl + href)
        }
        return urls
    }

    override fun parseProductNameFromDetail(documentDetailProduct: Document): String? {
        var element: Element? = documentDetailProduct.select("div.detail-top.list > div > h1").first()
        if (element == null) {
            element = documentDetailProduct.select("div > article > h1").first()
        }
        if (element == null) {
            element = documentDetailProduct.select("div.detail-top.list > div > article > h1").first()
        }

        return ofNullable(element)
                .map { it.text() }
                .filter { StringUtils.isNotBlank(it) }
                .orElse(null)
    }

    override fun parseProductPictureURL(documentDetailProduct: Document): String? {
        val first = documentDetailProduct.select("div.product__img > a > picture > img").first()
                ?: return null
        return Optional.of(eshopUuid.productStartUrl + first.attr("data-srcset")) .orElse(null)
    }

    override fun isProductUnavailable(documentDetailProduct: Document): Boolean {
        return JsoupUtils.notExistElement(documentDetailProduct, "#frm-addMultipleToBasket-form > button > meta")
    }

    override fun parseProductPriceForPackage(documentDetailProduct: Document): Optional<BigDecimal> {
        var element = documentDetailProduct.select("span[itemprop=price]").first()
        if (element != null) {
            val attr = element.attr("content")
            if (attr.isNotBlank()) {
                return ofNullable(ConvertUtils.convertToBigDecimal(attr))
            }
        }
        return Optional.of(element)
                .filter { Objects.nonNull(it) }
                .map { it.text() }
                .filter { StringUtils.isNotBlank(it) }
                .map { ConvertUtils.convertToBigDecimal(it) }
    }

}
