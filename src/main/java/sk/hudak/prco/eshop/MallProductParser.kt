package sk.hudak.prco.eshop

import org.apache.commons.lang3.math.NumberUtils
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.EshopUuid.MALL
import sk.hudak.prco.api.ProductAction
import sk.hudak.prco.builder.SearchUrlBuilder
import sk.hudak.prco.exception.PrcoRuntimeException
import sk.hudak.prco.parser.eshop.JSoupProductParser
import sk.hudak.prco.parser.unit.UnitParser
import sk.hudak.prco.utils.ConvertUtils.convertToBigDecimal
import sk.hudak.prco.utils.JsoupUtils
import sk.hudak.prco.utils.JsoupUtils.notExistElement
import sk.hudak.prco.utils.PatternUtils.NUMBER_AT_LEAST_ONE
import sk.hudak.prco.utils.PatternUtils.createMatcher
import sk.hudak.prco.utils.UserAgentDataHolder
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@Component
class MallProductParser(unitParser: UnitParser,
                        userAgentDataHolder: UserAgentDataHolder,
                        searchUrlBuilder: SearchUrlBuilder)
    : JSoupProductParser(unitParser, userAgentDataHolder, searchUrlBuilder) {

    companion object {
        private const val MAX_COUNT_OF_PRODUCT_PRE_PAGE = 48
    }

    override val eshopUuid: EshopUuid
        get() = MALL

    override val timeout: Int
        get() = TIMEOUT_15_SECOND

    override fun parseCountOfPages(documentList: Document): Int {
        val scriptContent = documentList.getElementsByTag("script").stream()
                .map { it.html() }
                .filter { it.contains("var CONFIGURATION") }
                .findFirst()

        if (!scriptContent.isPresent) {
            return 1
        }
        // "total":107,"products"
        val matcher = createMatcher(scriptContent.get(), "\"total\":", NUMBER_AT_LEAST_ONE, ",\"products\"")
        return if (matcher.find()) {
            BigDecimal(Optional.ofNullable(matcher.group(2))
                    .filter { NumberUtils.isParsable(it) }
                    .map { Integer.valueOf(it) }
                    .get())
                    .divide(BigDecimal(MAX_COUNT_OF_PRODUCT_PRE_PAGE), RoundingMode.CEILING)
                    .toInt()
        } else 1
    }

    override fun parsePageForProductUrls(documentList: Document, pageNumber: Int): List<String>? {
        val result = ArrayList<String>(MAX_COUNT_OF_PRODUCT_PRE_PAGE)
        documentList.select("h3[class=lst-product-item-title]")
                .forEach { result.add(eshopUuid.productStartUrl + it.child(0).attr("href")) }
        return result
    }

    override fun isProductUnavailable(documentDetailProduct: Document): Boolean {
        val b = notExistElement(documentDetailProduct, "button[id=add-to-cart]")
        if (!b) {
            return b
        }
        val s = Optional.ofNullable(documentDetailProduct.select("span[class='btn-inset lay-block']").first())
                .map { it.text() }
        return s.isPresent
    }

    override fun parseProductNameFromDetail(documentDetailProduct: Document): Optional<String> {
        val element = documentDetailProduct.select("section[class=pro-column] h1").first() ?: return Optional.empty()
        return Optional.of(element.text())
    }

    override fun parseProductPriceForPackage(documentDetailProduct: Document): Optional<BigDecimal> {
        val elements2 = documentDetailProduct.select("b[class=pro-price con-emphasize font-primary--bold mr-5]")
        if (elements2.isEmpty()) {
            return Optional.empty()
        }
        val cenaZaBalenie = elements2[0].text().replace("€", "").trim { it <= ' ' }
        return Optional.of(convertToBigDecimal(cenaZaBalenie))
    }

    override fun parseProductAction(documentDetailProduct: Document): Optional<ProductAction> {
        val select = documentDetailProduct.select("p[class=m-0 pro-stickers]").first()
                ?: return Optional.of(ProductAction.NON_ACTION)
        val children = select.children()
        for (child in children) {
            val aClass = child.attr("class")
            if ("label label--action" == aClass) {
                return Optional.of(ProductAction.IN_ACTION)
            }
        }
        return Optional.of(ProductAction.NON_ACTION)
    }


    override fun parseProductActionValidity(documentDetailProduct: Document): Optional<Date> {
        val select = documentDetailProduct.select("p[class=mb-5]")
        if (select.isEmpty()) {
            return Optional.empty()
        }
        val element = select[0]
        val sb = StringBuilder(element.html())
        sb.delete(0, sb.indexOf("do") + 3)
        sb.delete(10, sb.length)
        return Optional.of(parseDate(sb.toString(), DATE_FORMAT_HH_MM_YYYY)!!)
    }

    override fun parseProductPictureURL(documentDetailProduct: Document): Optional<String> {
        var img: Element? = documentDetailProduct.select("img[class=gall-slide-img]").first()
        if (img == null) {
            img = documentDetailProduct.select("img.gallery-magnifier__normal").first()
        }
        return Optional.ofNullable(img)
                .map { JsoupUtils.srcAttribute(it) }
    }

    private fun parseDate(strDate: String?, format: String): Date? {
        if (strDate == null) {
            return null
        }
        try {
            return SimpleDateFormat(format).parse(strDate)

        } catch (e: ParseException) {
            throw PrcoRuntimeException("error while parsing string to date", e)
        }

    }


}
