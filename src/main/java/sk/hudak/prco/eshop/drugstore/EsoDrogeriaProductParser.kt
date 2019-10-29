package sk.hudak.prco.eshop.drugstore

import org.apache.commons.lang3.StringUtils
import org.jsoup.nodes.Document
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.api.EshopUuid.ESO_DROGERIA
import sk.hudak.prco.api.ProductAction
import sk.hudak.prco.builder.SearchUrlBuilder
import sk.hudak.prco.parser.eshop.JSoupProductParser
import sk.hudak.prco.parser.unit.UnitParser
import sk.hudak.prco.utils.ConvertUtils
import sk.hudak.prco.utils.UserAgentDataHolder
import java.math.BigDecimal
import java.util.*
import java.util.Optional.ofNullable
import java.util.stream.Collectors

@Component
class EsoDrogeriaProductParser(unitParser: UnitParser,
                               userAgentDataHolder: UserAgentDataHolder,
                               searchUrlBuilder: SearchUrlBuilder)
    : JSoupProductParser(unitParser, userAgentDataHolder, searchUrlBuilder) {

    override val eshopUuid: EshopUuid
        get() = ESO_DROGERIA

    override val timeout: Int
        get() = TIMEOUT_10_SECOND

    override fun parseCountOfPages(documentList: Document): Int {
        val select = documentList.select("div[class='pagination'] a")
        if (select.size == 0) {
            return 1
        }
        val poslednyIndex = select.size - 2
        return if (poslednyIndex < 1) {
            1
        } else Optional.of(select[poslednyIndex])
                .map { it.text() }
                .filter { StringUtils.isNotBlank(it) }
                .map { Integer.valueOf(it) }
                .get()
    }

    override fun parseNextPage(searchKeyWord: String, currentPageNumber: Int): List<String>? {
        // pocet stranok v URL je od 0 preto to posuvam -1
        return super.parseNextPage(searchKeyWord, currentPageNumber - 1)
    }

    override fun parsePageForProductUrls(documentList: Document, pageNumber: Int): List<String> {
        return documentList.select("div[class='product tab_img160 image_first one-preview-image in-stock-y'] div[class='productBody'] div[class='img_box'] a")
                .stream()
                .map { element -> element.attr("href") }
                .filter { StringUtils.isNotBlank(it) }
                .map { href -> eshopUuid.productStartUrl + href }
                .collect(Collectors.toList())
    }

    override fun parseProductNameFromDetail(documentDetailProduct: Document): Optional<String> {
        return ofNullable(documentDetailProduct.select("#incenterpage2 > div.product-detail-container.in-stock-y > h1").first())
                .map { it.text() }
    }

    override fun parseProductPictureURL(documentDetailProduct: Document): Optional<String> {
        return ofNullable(documentDetailProduct.select("#detail_src_magnifying_small").first())
                .map { it.attr("src") }
                .filter { StringUtils.isNotBlank(it) }
                .map { eshopUuid.productStartUrl + it }
    }

    override fun isProductUnavailable(documentDetailProduct: Document): Boolean {
        return null == documentDetailProduct.select("#buy_btn").first()
    }

    override fun parseProductPriceForPackage(documentDetailProduct: Document): Optional<BigDecimal> {
        return ofNullable(documentDetailProduct.select("span[class='price-vat'] span[class='price-value def_color']").first())
                .map { it.text() }
                .map { it.trim { it <= ' ' } }
                .map { value -> StringUtils.removeEnd(value, "EUR").trim { it <= ' ' } }
                // posledny znak nie je medzera ale nieco co tak vyzera...
                .map { value -> value.substring(0, value.length - 2) }
                .map { ConvertUtils.convertToBigDecimal(it) }
    }

    override fun parseProductAction(documentDetailProduct: Document): Optional<ProductAction> {
        return Optional.empty()
    }

    override fun parseProductActionValidity(documentDetailProduct: Document): Optional<Date> {
        return Optional.empty()
    }
}
