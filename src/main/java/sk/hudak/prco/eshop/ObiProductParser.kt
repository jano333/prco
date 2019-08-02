package sk.hudak.prco.eshop

import org.jsoup.nodes.Document
import org.jsoup.nodes.TextNode
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.EshopUuid.OBI
import sk.hudak.prco.api.ProductAction
import sk.hudak.prco.builder.SearchUrlBuilder
import sk.hudak.prco.exception.PrcoRuntimeException
import sk.hudak.prco.parser.UnitParser
import sk.hudak.prco.parser.WatchDogParser
import sk.hudak.prco.parser.impl.JSoupProductParser
import sk.hudak.prco.utils.ConvertUtils.convertToBigDecimal
import sk.hudak.prco.utils.UserAgentDataHolder
import java.math.BigDecimal
import java.util.*

// TODO doriesit aby bralo len kosicku pobocku? pozor su 2...
//@Component
class ObiProductParser (unitParser: UnitParser, userAgentDataHolder: UserAgentDataHolder, searchUrlBuilder: SearchUrlBuilder)
    : JSoupProductParser(unitParser, userAgentDataHolder, searchUrlBuilder), WatchDogParser {

    override val eshopUuid: EshopUuid
        get() = OBI

    override// koli pomalym odozvam davam na 15 sekund
    val timeout: Int
        get() = TIMEOUT_15_SECOND

    override fun parseProductNameFromDetail(documentDetailProduct: Document): Optional<String> {
        val select = documentDetailProduct.select("h1[class=h2 overview__heading]")
        return if (select.isEmpty()) {
            Optional.empty()
        } else Optional.of((select[0].childNode(0) as TextNode).wholeText)
    }

    override fun isProductUnavailable(documentDetailProduct: Document): Boolean {
        //TODO
        return false
    }

    override fun parseProductPriceForPackage(documentDetailProduct: Document): Optional<BigDecimal> {
        val select = documentDetailProduct.select("strong[itemprop=price]")
        val first = select.first()
        val cenaZaBalenie = first.text()
        return Optional.of(convertToBigDecimal(cenaZaBalenie))
    }

    override fun parseCountOfPages(documentList: Document): Int {
        //TODO
        throw PrcoRuntimeException("Not yet implemented")
    }

    override fun parsePageForProductUrls(documentList: Document, pageNumber: Int): List<String>? {
        //TODO
        throw PrcoRuntimeException("Not yet implemented")
    }

    override fun parseProductAction(documentDetailProduct: Document): Optional<ProductAction> {
        //TODO impl
        return Optional.empty()
    }

    override fun parseProductActionValidity(documentDetailProduct: Document): Optional<Date> {
        //TODO impl
        return Optional.empty()
    }

    override fun parseProductPictureURL(documentDetailProduct: Document): Optional<String> {
        //TODO impl
        return Optional.empty()
    }
}
