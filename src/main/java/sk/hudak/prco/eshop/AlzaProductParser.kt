package sk.hudak.prco.eshop

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.TextNode
import org.apache.commons.lang3.StringUtils
import org.jsoup.nodes.DataNode
import org.jsoup.nodes.Document
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.EshopUuid.ALZA
import sk.hudak.prco.api.ProductAction
import sk.hudak.prco.api.ProductAction.IN_ACTION
import sk.hudak.prco.api.ProductAction.NON_ACTION
import sk.hudak.prco.builder.SearchUrlBuilder
import sk.hudak.prco.parser.eshop.JSoupProductParser
import sk.hudak.prco.parser.unit.UnitParser
import sk.hudak.prco.utils.ConvertUtils
import sk.hudak.prco.utils.JsoupUtils.existElement
import sk.hudak.prco.utils.JsoupUtils.notExistElement
import sk.hudak.prco.utils.UserAgentDataHolder
import java.io.IOException
import java.math.BigDecimal
import java.util.*

@Component
class AlzaProductParser(unitParser: UnitParser,
                        userAgentDataHolder: UserAgentDataHolder,
                        searchUrlBuilder: SearchUrlBuilder)
    : JSoupProductParser(unitParser, userAgentDataHolder, searchUrlBuilder) {

    override val eshopUuid: EshopUuid = ALZA

    // koli pomalym odozvam davam na 10 sekund
    override val timeout: Int = TIMEOUT_10_SECOND

    override fun parseCountOfPages(documentList: Document): Int {
        // zaujimaju aj tak maximalne 3 page viac nie...
        val select = documentList.select("#pagertop").first() ?: return 1
        val children = select.children()
        // 2-hy predposledny
        val element = children[children.size - 3].child(0)
        return Integer.valueOf(element.text())
    }

    override fun parseUrlsOfProduct(documentList: Document, pageNumber: Int): List<String> {
        val select1 = documentList.select("a[class='name browsinglink']")
        val urls = ArrayList<String>(select1.size)
        for (element in select1) {
            var href = eshopUuid.productStartUrl + element.attr("href")
            // from
            // https://www.alza.sk/maxi/pampers-active-baby-dry-vel-4-maxi-152-ks-mesacne-balenie-d4842708.htm?o=8
            // urobit
            // https://www.alza.sk/maxi/pampers-active-baby-dry-vel-4-maxi-152-ks-mesacne-balenie-d4842708.htm
            if (href.contains("?")) {
                href = href.substring(0, href.lastIndexOf('?'))
            }
            urls.add(href)
        }
        return urls
    }

    override fun isProductUnavailable(documentDetailProduct: Document): Boolean {
        return notExistElement(documentDetailProduct, "a[class=cart-insert]") && notExistElement(documentDetailProduct, "a[class=btnx normal green buy]")
    }

    override fun parseProductPriceForPackage(documentDetailProduct: Document): Optional<BigDecimal> {
        //niekte je to tak
        var elements = documentDetailProduct.select("span[class=price_withVat]")
        if (elements.isEmpty()) {
            // inde tak
            elements = documentDetailProduct.select("span[class=bigPrice price_withVat]")
        }
        if (elements.isEmpty()) {
            elements = documentDetailProduct.select("#prices > tbody > tr.pricenormal > td.c2 > span")
        }

        if (elements.isEmpty()) {
            return Optional.empty()
        }
        val text = elements[0].text()
        return if (StringUtils.isBlank(text)) {
            Optional.empty()
        } else Optional.of(text)
                .map { value -> StringUtils.removeEnd(value, " €") }
                .map { ConvertUtils.convertToBigDecimal(it) }
    }

    override fun parseProductNameFromDetail(documentDetailProduct: Document): String? {
        val first = documentDetailProduct.select("script[type=application/ld+json]")
        if (first.size < 2) {
            return null
        }
        val element = first[1]
        val nodes = element.childNodes()
        val node = nodes[0] as DataNode
        val wholeDataJson = node.wholeData
        val actualObj: JsonNode
        try {
            val mapper = ObjectMapper()
            actualObj = mapper.readTree(wholeDataJson)
        } catch (e: IOException) {
            log.error("error while parsing json for url " + documentDetailProduct.location(), e)
            return null
        }

        val name = actualObj.get("name") as TextNode
        return name.textValue()
    }


    override fun parseProductPictureURL(documentDetailProduct: Document): String? {
        val select = documentDetailProduct.select("#imgMain")
        if (select.isEmpty()) {
            return null
        }
        val element = select[0]
        val fullUrl = element.attr("data-src")
        return Optional.of(fullUrl) .orElse(null)
    }

    override fun parseProductAction(documentDetailProduct: Document): Optional<ProductAction> {
        //TODO dorobit parsovanie zlavy v percentach
        return Optional.of(
                if (existElement(documentDetailProduct, "span[class=icon-percentage icon]") || existElement(documentDetailProduct, "span[class=quantityPercentDiscount icon-percentage icon]"))
                    IN_ACTION
                else
                    NON_ACTION)
    }

    override fun parseProductActionValidity(documentDetailProduct: Document): Optional<Date> {
        // not supported for this eshop
        return Optional.empty()
    }
}
