package sk.hudak.prco.eshop.tobedeleted

import org.jsoup.nodes.Document
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.EshopUuid.BAMBINO
import sk.hudak.prco.api.ProductAction
import sk.hudak.prco.api.ProductAction.IN_ACTION
import sk.hudak.prco.api.ProductAction.NON_ACTION
import sk.hudak.prco.builder.SearchUrlBuilder
import sk.hudak.prco.parser.UnitParser
import sk.hudak.prco.parser.impl.JSoupProductParser
import sk.hudak.prco.utils.ConvertUtils.convertToBigDecimal
import sk.hudak.prco.utils.JsoupUtils.existElement
import sk.hudak.prco.utils.JsoupUtils.getTextFromFirstElementByClass
import sk.hudak.prco.utils.JsoupUtils.notExistElement
import sk.hudak.prco.utils.UserAgentDataHolder
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

//@Component
//TODO zrusit presli pod mall eshop
class BambinoProductParser(unitParser: UnitParser, userAgentDataHolder: UserAgentDataHolder, searchUrlBuilder: SearchUrlBuilder)
    : JSoupProductParser(unitParser, userAgentDataHolder, searchUrlBuilder) {

    companion object {
        private val PAGING = 24
    }

    override val eshopUuid: EshopUuid
        get() = BAMBINO

    override val timeout: Int
        get() = TIMEOUT_15_SECOND

    override fun parseCountOfPages(documentList: Document): Int {
        val text = getTextFromFirstElementByClass(documentList, "o-header-section__info")
        if (!text.isPresent) {
            return 1
        }
        val tmp = text.get()
        val count = tmp.replace("Celkom nájdených produktov: ", "").trim { it <= ' ' }
        val result = BigDecimal(count).divide(BigDecimal(PAGING), RoundingMode.HALF_UP)
        return result.toInt()
    }

    override fun parsePageForProductUrls(documentList: Document, pageNumber: Int): List<String>? {
        val allElements = documentList.select("a[class=o-product-box__link]")
        val result = ArrayList<String>(allElements.size)
        for (element in allElements) {
            val href = element.attr("href")
            // specialny fix, odsranujem vsetko co je za otaznikom lebo tam dava neaky token:
            // https://www.bambino.sk/jednorazove-plienky/premium-care-4-maxi-7-14kg-66-ks?searchToken=75bc6842-eecc-4269-a0fc-1e3af1c36c80
            val querySeparator = href.indexOf('?')
            if (querySeparator == -1) {
                result.add(href)
            } else {
                result.add(href.substring(0, querySeparator))
            }
        }
        return result
    }

    override fun isProductUnavailable(documentDetailProduct: Document): Boolean {
        return notExistElement(documentDetailProduct, "span[class=o-add-to-cart__status]")
    }


    override fun parseProductNameFromDetail(documentDetailProduct: Document): Optional<String> {
        val first = documentDetailProduct.select("h1[class=o-product__name]").first() ?: return Optional.empty()
        val children = first.children()
        val sb = StringBuilder()

        for (child in children) {
            val text = child.text()
            sb.append(text)
            sb.append(" ")
        }
        val result = sb.toString().trim { it <= ' ' }
        return Optional.ofNullable(result)
    }

    override fun parseProductPriceForPackage(documentDetailProduct: Document): Optional<BigDecimal> {
        val first = documentDetailProduct.select("b[class=m-quantity-price__price]").first() ?: return Optional.empty()
        var sb = StringBuilder(first.text())
        // odmazem posledne 2 znaky
        sb = sb.deleteCharAt(sb.length - 1)
        sb = sb.deleteCharAt(sb.length - 1)
        val cenaZaBalenie = sb.toString()
        return Optional.of(convertToBigDecimal(cenaZaBalenie))
    }

    override fun parseProductPictureURL(documentDetailProduct: Document): Optional<String> {
        val first = documentDetailProduct.select("img[itemprop=image]").first() ?: return Optional.empty()
        val src = first.attr("src")
        return Optional.of(src)
    }

    override fun parseProductAction(documentDetailProduct: Document): Optional<ProductAction> {
        return if (existElement(documentDetailProduct, "span[class=m-badge m-badge--large bg-red o-product__labels__item]"))
            Optional.of(IN_ACTION)
        else
            Optional.of(NON_ACTION)
    }

    override fun parseProductActionValidity(documentDetailProduct: Document): Optional<Date> {
        // not supported for this eshop
        return Optional.empty()
    }


}
