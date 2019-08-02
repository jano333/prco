package sk.hudak.prco.eshop

import org.jsoup.nodes.Document
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.EshopUuid.METRO
import sk.hudak.prco.api.ProductAction
import sk.hudak.prco.api.ProductAction.IN_ACTION
import sk.hudak.prco.api.ProductAction.NON_ACTION
import sk.hudak.prco.builder.SearchUrlBuilder
import sk.hudak.prco.parser.UnitParser
import sk.hudak.prco.parser.impl.JSoupProductParser
import sk.hudak.prco.utils.ConvertUtils.convertToBigDecimal
import sk.hudak.prco.utils.JsoupUtils.notExistElement
import sk.hudak.prco.utils.UserAgentDataHolder
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

@Component
class MetroProductParser(unitParser: UnitParser, userAgentDataHolder: UserAgentDataHolder, searchUrlBuilder: SearchUrlBuilder)
    : JSoupProductParser(unitParser, userAgentDataHolder, searchUrlBuilder) {

    override val eshopUuid: EshopUuid
        get() = METRO

    override val timeout: Int
        get() = TIMEOUT_15_SECOND

    override// 21 su Kosice pobocka
    val cookie: Map<String, String>
        get() = Collections.singletonMap("storeId", "21")

    override fun parseCountOfPages(documentList: Document): Int {
        val select = documentList.select("div[class=paging-show pull-left color-gray-dark] strong[class=color-gray-base]")
        if (select.size != 2) {
            return 1
        }
        //2 v poradi
        val element = select[1]
        val count = element.text()
        val result = BigDecimal(count).divide(BigDecimal(PAGING), RoundingMode.HALF_UP)
        return result.toInt()
    }

    override fun parsePageForProductUrls(documentList: Document, pageNumber: Int): List<String>? {
        val select = documentList.select("div.product.msg-parent")
        val result = ArrayList<String>(select.size)
        for (element in select) {
            val first = element.children().first().children().first().children().first().children().first()
            result.add(first.attr("href"))
        }
        return result
    }

    override fun isProductUnavailable(documentDetailProduct: Document): Boolean {
        return notExistElement(documentDetailProduct, "div.x-row.product-stock-detail")
    }

    override fun parseProductNameFromDetail(documentDetailProduct: Document): Optional<String> {
        val select = documentDetailProduct.select("div.product-detail.clearfix > h1").first() ?: return Optional.empty()
        return Optional.ofNullable(select.text())
    }

    override fun parseProductPriceForPackage(documentDetailProduct: Document): Optional<BigDecimal> {
        var text = documentDetailProduct.select("tr.price-package > td:nth-child(4)").text()
        text = text.substring(0, text.length - 2)
        return Optional.of(convertToBigDecimal(text))
    }

    override fun parseProductPictureURL(documentDetailProduct: Document): Optional<String> {
        return Optional.ofNullable(documentDetailProduct.select("div[class=img-center] img").first())
                .map { element -> element.attr("src") }
    }

    override fun parseProductAction(documentDetailProduct: Document): Optional<ProductAction> {
        val select = documentDetailProduct.select("span[class=mic mic-action-sk is-32]")
        return Optional.of(if (select.isEmpty())
            NON_ACTION
        else
            IN_ACTION)
    }

    override fun parseProductActionValidity(documentDetailProduct: Document): Optional<Date> {
        //TODO impl action validity
        return Optional.empty()
    }

    companion object {

        private val PAGING = 15
    }
}
