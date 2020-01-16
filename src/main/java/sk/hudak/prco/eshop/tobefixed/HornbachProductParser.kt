package sk.hudak.prco.eshop.tobefixed

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.TextNode
import org.apache.http.HttpHeaders.USER_AGENT
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.BasicCookieStore
import org.apache.http.impl.client.BasicResponseHandler
import org.apache.http.impl.client.HttpClientBuilder
import org.apache.http.impl.cookie.BasicClientCookie
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.EshopUuid.HORNBACH
import sk.hudak.prco.api.ProductAction
import sk.hudak.prco.builder.SearchUrlBuilder
import sk.hudak.prco.exception.PrcoRuntimeException
import sk.hudak.prco.parser.WatchDogParser
import sk.hudak.prco.parser.eshop.JSoupProductParser
import sk.hudak.prco.parser.unit.UnitParser
import sk.hudak.prco.utils.ConvertUtils.convertToBigDecimal
import sk.hudak.prco.utils.UserAgentDataHolder
import java.math.BigDecimal
import java.util.*
import java.util.Collections.emptyList
import java.util.Optional.*

//@Component
class HornbachProductParser(unitParser: UnitParser, userAgentDataHolder: UserAgentDataHolder, searchUrlBuilder: SearchUrlBuilder)
    : JSoupProductParser(unitParser, userAgentDataHolder, searchUrlBuilder), WatchDogParser {

    override val eshopUuid: EshopUuid
        get() = HORNBACH

    override val timeout: Int
        get() = TIMEOUT_10_SECOND

    override fun retrieveDocument(productUrl: String): Document {
        val cookieStore = BasicCookieStore()
        // 746 -> Kosice predajna
        val cookie = BasicClientCookie("hbMarketCookie", "746")
        cookie.domain = "www.hornbach.sk"
        cookie.path = "/"
        cookieStore.addCookie(cookie)

        cookieStore.addCookie(BasicClientCookie(USER_AGENT, userAgent))

        val httpclient = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).build()

        try {
            return Jsoup.parse(httpclient.execute(HttpGet(productUrl), BasicResponseHandler()))

        } catch (e: Exception) {
            throw PrcoRuntimeException("error while downloading/parsing content for url $productUrl", e)
        }

    }

    override fun parseProductNameFromDetail(documentDetailProduct: Document): String? {
        val select = documentDetailProduct.select("#article-details > h1")
        if (select.isEmpty()) {
            return null
        }
        val first = select.first()
        val text = first.text()
        return text
    }

    override fun isProductUnavailable(documentDetailProduct: Document): Boolean {
        try {
            val jsonString = parseJson(documentDetailProduct)

            val mapper = ObjectMapper()
            val actualObj = mapper.readTree(jsonString)
            val jsonNode1 = actualObj.get("offers") as ArrayNode
            return jsonNode1.size() == 0

        } catch (e: Exception) {
            throw PrcoRuntimeException("error while parsing availability for product url: " + documentDetailProduct.location(), e)
        }

    }

    override fun parseProductPriceForPackage(documentDetailProduct: Document): Optional<BigDecimal> {
        try {
            val jsonString = parseJson(documentDetailProduct)

            val mapper = ObjectMapper()
            val actualObj = mapper.readTree(jsonString)
            val jsonNode1 = actualObj.get("offers") as ArrayNode
            val offerNode = jsonNode1.get(0)
            val priceNode = offerNode.get("price") as TextNode
            val price = priceNode.textValue()
            return ofNullable(convertToBigDecimal(price))

        } catch (e: Exception) {
            log.error("error while parsing price", e)
            return empty()
        }

    }

    private fun parseJson(document: Document): String {
        try {
            return document.select("script[type=application/ld+json]")[0].childNode(0).toString()

        } catch (e: Exception) {
            throw PrcoRuntimeException("json element not found for url " + document.location(), e)
        }

    }

    override fun parseCountOfPages(documentList: Document): Int {
        // TODO impl
        return 0
    }

    override fun parseUrlsOfProduct(documentList: Document, pageNumber: Int): List<String> {
        // TODO impl
        return emptyList()
    }

    override fun parseProductAction(documentDetailProduct: Document): Optional<ProductAction> {
        // TODO impl
        return empty()
    }

    override fun parseProductActionValidity(documentDetailProduct: Document): Optional<Date> {
        // TODO impl
        return empty()
    }

    override fun parseProductPictureURL(documentDetailProduct: Document): String? {
        try {
            val jsonString = parseJson(documentDetailProduct)

            val mapper = ObjectMapper()
            val actualObj = mapper.readTree(jsonString)
            val image = actualObj.get("image") as ArrayNode
            return if (image.size() == 0) {
               null
            } else of(image.get(0).get("url").textValue()) .orElse(null)

        } catch (e: Exception) {
            log.error("error while product picture url", e)
            return null
        }

    }
}
