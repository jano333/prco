package sk.hudak.prco.eshop.pharmacy

import org.apache.commons.lang3.StringUtils
import org.jsoup.nodes.Document
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.EshopUuid.DR_MAX
import sk.hudak.prco.api.ProductAction
import sk.hudak.prco.builder.SearchUrlBuilder
import sk.hudak.prco.dto.UnitTypeValueCount
import sk.hudak.prco.parser.eshop.JSoupProductParser
import sk.hudak.prco.parser.unit.UnitParser
import sk.hudak.prco.utils.ConvertUtils
import sk.hudak.prco.utils.UserAgentDataHolder
import java.math.BigDecimal
import java.util.*

@Component
class DrMaxProductParser
constructor(unitParser: UnitParser,
            userAgentDataHolder: UserAgentDataHolder,
            searchUrlBuilder: SearchUrlBuilder)
    : JSoupProductParser(unitParser, userAgentDataHolder, searchUrlBuilder) {

    override val eshopUuid: EshopUuid
        get() = DR_MAX

    override val timeout: Int
        get() = TIMEOUT_15_SECOND

    override fun parseCountOfPages(documentList: Document): Int {
        val select = documentList.select("h2[class=title-subcategory]").first() ?: return 1
        var text = select.text()
        text = StringUtils.remove(text, "Vyhľadávanie (")
        text = StringUtils.remove(text, ")")
        val allProductsSize = Integer.parseInt(text)
        val maxCountOfNewPages = eshopUuid.maxCountOfProductOnPage

        val cout = allProductsSize / maxCountOfNewPages
        val hh = allProductsSize % maxCountOfNewPages

        return if (hh > 0) {
            cout + 1
        } else {
            cout
        }
    }

    override fun parsePageForProductUrls(documentList: Document, pageNumber: Int): List<String>? {
        val urls = ArrayList<String>()
        for (child in documentList.select("#main > div.productList.productListTypeRow.row").first().children()) {
            val attr = child.children().first().attr("data-href") ?: continue
            urls.add(eshopUuid.productStartUrl + attr)
        }
        return urls
    }

    override fun parseProductNameFromDetail(documentDetailProduct: Document): Optional<String> {
        val select = documentDetailProduct.select("#product-detail > div.row > div > div > div.col.data > div.redesign_desktop > div.redesign-product-detail-title.bold")
        val first = select.first().children().first()
        val text = first.text().trim { it <= ' ' }
        return Optional.of(text)
    }

    override fun parseUnitValueCount(document: Document, productName: String): Optional<UnitTypeValueCount> {
        val select = document.select("div.redesign-product-detail-slogan")
        if (!select.isEmpty()) {
            val text = select[0].child(0).text()
            if (StringUtils.isNotBlank(text)) {
                return unitParser.parseUnitTypeValueCount(text)
            }
        }
        // ak nie je tak skusit z product name
        return super.parseUnitValueCount(document, productName)
    }

    override fun isProductUnavailable(documentDetailProduct: Document): Boolean {
        return documentDetailProduct.select("button[class=redesign-button addToCartBtn btn btn-big btn-pink ucase]").isEmpty()
    }

    override fun parseProductPriceForPackage(documentDetailProduct: Document): Optional<BigDecimal> {
        val select = documentDetailProduct.select("strong[itemprop=price]")
        if (select.isEmpty()) {
            return Optional.empty()
        }
        val text = select[0].text()
        val value = ConvertUtils.convertToBigDecimal(text)
        return Optional.ofNullable(value)
    }

    override fun parseProductPictureURL(documentDetailProduct: Document): Optional<String> {
        val select = documentDetailProduct.select("#productImg > img.image")
        val element = select[0]
        val src = element.attr("src")
        val s = eshopUuid.productStartUrl + src
        return Optional.of(s)
    }

    override fun parseProductAction(documentDetailProduct: Document): Optional<ProductAction> {
        //TODO impl
        return Optional.empty()
    }

    override fun parseProductActionValidity(documentDetailProduct: Document): Optional<Date> {
        //TODO impl
        return Optional.empty()
    }


}
