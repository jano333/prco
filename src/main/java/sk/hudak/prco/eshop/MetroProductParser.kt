package sk.hudak.prco.eshop

import org.jsoup.nodes.Document
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.EshopUuid.METRO
import sk.hudak.prco.api.ProductAction
import sk.hudak.prco.api.ProductAction.IN_ACTION
import sk.hudak.prco.api.ProductAction.NON_ACTION
import sk.hudak.prco.builder.SearchUrlBuilder
import sk.hudak.prco.parser.eshop.JSoupProductParser
import sk.hudak.prco.parser.unit.UnitParser
import sk.hudak.prco.utils.ConvertUtils.convertToBigDecimal
import sk.hudak.prco.utils.JsoupUtils.notExistElement
import sk.hudak.prco.utils.UserAgentDataHolder
import sk.hudak.prco.utils.href
import java.math.BigDecimal
import java.util.*
import kotlin.streams.toList

@Component
class MetroProductParser(unitParser: UnitParser,
                         userAgentDataHolder: UserAgentDataHolder,
                         searchUrlBuilder: SearchUrlBuilder)
    : JSoupProductParser(unitParser, userAgentDataHolder, searchUrlBuilder) {

    override val eshopUuid: EshopUuid = METRO
    override val timeout: Int = TIMEOUT_15_SECOND
    // 21 su Kosice pobocka
    override val requestCookie: Map<String, String> = Collections.singletonMap("storeId", "21")

    override fun parseCountOfPages(documentList: Document): Int {
        return if (documentList.select("p[class='mo-pagination-status']").first() != null) 1 else 0
    }

    override fun parsePageForProductUrls(documentList: Document, pageNumber: Int): List<String>? {
        return documentList.select("div.product-layer-content div.product-info-wrapper a.product-photo")
                .stream()
                .map { it.href() }
                .filter { it.isNotBlank() }
                .toList()
    }

    override fun isProductUnavailable(documentDetailProduct: Document): Boolean {
        //TODO
        return notExistElement(documentDetailProduct, "div.x-row.product-stock-detail")
    }

    override fun parseProductNameFromDetail(documentDetailProduct: Document): Optional<String> {
        //TODO
        val select = documentDetailProduct.select("div.product-detail.clearfix > h1").first() ?: return Optional.empty()
        return Optional.ofNullable(select.text())
    }

    override fun parseProductPriceForPackage(documentDetailProduct: Document): Optional<BigDecimal> {
        //TODO
        var text = documentDetailProduct.select("tr.price-package > td:nth-child(4)").text()
        text = text.substring(0, text.length - 2)
        return Optional.of(convertToBigDecimal(text))
    }

    override fun parseProductPictureURL(documentDetailProduct: Document): Optional<String> {
        //TODO
        return Optional.ofNullable(documentDetailProduct.select("div[class=img-center] img").first())
                .map { element -> element.attr("src") }
    }

    override fun parseProductAction(documentDetailProduct: Document): Optional<ProductAction> {
        //TODO
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
}
