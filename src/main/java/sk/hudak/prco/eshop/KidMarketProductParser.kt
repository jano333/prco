package sk.hudak.prco.eshop

import org.apache.commons.lang3.StringUtils
import org.jsoup.nodes.Document
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.EshopUuid.KID_MARKET
import sk.hudak.prco.api.ProductAction
import sk.hudak.prco.builder.SearchUrlBuilder
import sk.hudak.prco.parser.eshop.JSoupProductParser
import sk.hudak.prco.parser.unit.UnitParser
import sk.hudak.prco.utils.ConvertUtils
import sk.hudak.prco.utils.JsoupUtils
import sk.hudak.prco.utils.JsoupUtils.notExistElement
import sk.hudak.prco.utils.UserAgentDataHolder
import sk.hudak.prco.utils.src
import java.math.BigDecimal
import java.util.*
import java.util.Optional.ofNullable

@Component
class KidMarketProductParser(unitParser: UnitParser,
                             userAgentDataHolder: UserAgentDataHolder,
                             searchUrlBuilder: SearchUrlBuilder)
    : JSoupProductParser(unitParser, userAgentDataHolder, searchUrlBuilder) {

    override val eshopUuid: EshopUuid = KID_MARKET

    override val timeout: Int = TIMEOUT_15_SECOND

    override fun parseCountOfPages(documentList: Document): Int {
        val countOfProducts = ofNullable(documentList.select("span[class=heading-counter]").first())
                .map { it.text() }
                .map { Integer.valueOf(StringUtils.removeStart(it, "Nájdené výsledky: ")) }
                .orElse(0)
        if (countOfProducts == 0) {
            return 1
        }
        return JsoupUtils.calculateCountOfPages(countOfProducts, eshopUuid.maxCountOfProductOnPage)
    }

    override fun parsePageForProductUrls(documentList: Document, pageNumber: Int): List<String> {
        //FIXME prepisat cez strem
        val first = documentList.select("#product_list").first() ?: return emptyList()

        val urls = ArrayList<String>()
        first.children().forEach { element ->
            val href = element.select("h5 a").first().attr("href")
            urls.add(href.substring(0, href.lastIndexOf("?")))
        }
        return urls
    }

    override fun isProductUnavailable(documentDetailProduct: Document): Boolean {
        return notExistElement(documentDetailProduct, "div[class='box-info-product'] p[id=add_to_cart]")
    }

    override fun parseProductNameFromDetail(documentDetailProduct: Document): Optional<String> {
        return ofNullable(documentDetailProduct.select("h1[class=page-heading]").first())
                .map { it.text() }
    }

    override fun parseProductPictureURL(documentDetailProduct: Document): Optional<String> {
        return ofNullable(documentDetailProduct.select("#bigpic").first())
                .map { it.src() }
    }

    override fun parseProductPriceForPackage(documentDetailProduct: Document): Optional<BigDecimal> {
        return ofNullable(documentDetailProduct.select("#our_price_display").first())
                .map { it.text() }
                .map { StringUtils.removeEnd(it, "€") }
                .map { ConvertUtils.convertToBigDecimal(it) }
    }

    override fun parseProductAction(documentDetailProduct: Document): Optional<ProductAction> {
        //TODO
        return Optional.empty()
    }

    override fun parseProductActionValidity(documentDetailProduct: Document): Optional<Date> {
        //TODO
        return Optional.empty()
    }


}
