package sk.hudak.prco.eshop

import org.apache.commons.lang3.StringUtils
import org.jsoup.nodes.Document
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.EshopUuid.TESCO
import sk.hudak.prco.api.ProductAction
import sk.hudak.prco.builder.SearchUrlBuilder
import sk.hudak.prco.exception.PrcoRuntimeException
import sk.hudak.prco.parser.eshop.JSoupProductParser
import sk.hudak.prco.parser.unit.UnitParser
import sk.hudak.prco.utils.ConvertUtils.convertToBigDecimal
import sk.hudak.prco.utils.JsoupUtils.getTextFromFirstElementByClass
import sk.hudak.prco.utils.JsoupUtils.notExistElement
import sk.hudak.prco.utils.UserAgentDataHolder
import java.math.BigDecimal
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.Optional.ofNullable
import java.util.stream.Collectors

/**
 * Created by jan.hudak on 9/29/2017.
 */
@Component
class TescoProductParser(unitParser: UnitParser, userAgentDataHolder: UserAgentDataHolder, searchUrlBuilder: SearchUrlBuilder)
    : JSoupProductParser(unitParser, userAgentDataHolder, searchUrlBuilder) {

    override val eshopUuid: EshopUuid
        get() = TESCO

    override val timeout: Int
        get() = TIMEOUT_10_SECOND

    override fun parseCountOfPages(documentList: Document): Int {
        val navEl = documentList.select("nav.pagination--page-selector-wrapper").first() ?: return 1
        val ulEl = navEl.child(0)
        val liEls = ulEl.children()
        val liElCount = liEls.size
        // prvy a posledny vynechavam
        return liElCount - 2
    }

    override fun parsePageForProductUrls(documentList: Document, pageNumber: Int): List<String>? {
        return documentList.select("div[class=product-details--content] a[class='product-tile--title product-tile--browsable']")
                .stream()
                .map { it.attr("href") }
                .filter { StringUtils.isNotBlank(it) }
                .map { eshopUuid.productStartUrl + it }
                .collect(Collectors.toList())
    }

    override fun isProductUnavailable(documentDetailProduct: Document): Boolean {
        //TODO https://potravinydomov.itesco.sk/groceries/sk-SK/products/2002120131941
        return notExistElement(documentDetailProduct, "button[class=button small add-control button-secondary]")
    }

    override fun parseProductNameFromDetail(documentDetailProduct: Document): Optional<String> {
        return getTextFromFirstElementByClass(documentDetailProduct, "product-details-tile__title")
    }

    override fun parseProductPictureURL(documentDetailProduct: Document): Optional<String> {
        val first = ofNullable(documentDetailProduct.select("img[class=product-image product-image-visible]").first())
        var pictureOpt = first.map { element1 -> element1.attr("data-src") }
        // niekedy je aj takto
        if (!pictureOpt.isPresent || pictureOpt.get().isEmpty()) {
            pictureOpt = first.map { element -> element.attr("src") }
        }
        return pictureOpt
    }

    override fun parseProductPriceForPackage(documentDetailProduct: Document): Optional<BigDecimal> {
        val elements = documentDetailProduct.select("div[class=price-per-sellable-unit price-per-sellable-unit--price price-per-sellable-unit--price-per-item] div span span[class=value]")
        return ofNullable(convertToBigDecimal(elements[0].text()))
    }

    override fun parseProductAction(documentDetailProduct: Document): Optional<ProductAction> {
        val select = documentDetailProduct.select("div.icon-offer-flash-group > div.yellow-square > span")
        return if (select.isEmpty()) {
            Optional.of(ProductAction.NON_ACTION)
        } else Optional.of(if ("Akcia" == select[0].text()) ProductAction.IN_ACTION else ProductAction.NON_ACTION)
    }

    override fun parseProductActionValidity(documentDetailProduct: Document): Optional<Date> {
        val htmlTree = StringBuilder()
                .append("ul[class=product-promotions]").append(" ")
                .append("li[class=product-promotion]").append(" ")
                .append("div").append(" ")
                .append("a").append(" ")
                .append("div[class=list-item-content promo-content-small]").append(" ")
                .append("span[class=dates]")

        var sb = StringBuilder(documentDetailProduct.select(htmlTree.toString())[0].text())
        sb = sb.delete(0, "Cena je platná pri dodaní do ".length)
        return ofNullable(parseDate(sb.toString(), DATE_FORMAT_HH_MM_YYYY))
    }

    //FIXME je to vo viacerych miestach urobit na to UTIL class pre datumy
    private fun parseDate(strDate: String?, format: String): Date? {
        if (strDate == null) {
            return null
        }
        try {
            //FIXME
            //            return DateTimeFormatter.ofPattern(format).parse(strDate).;
            return SimpleDateFormat(format).parse(strDate)

        } catch (e: ParseException) {
            throw PrcoRuntimeException("error while parsing string to date", e)
        }

    }
}
