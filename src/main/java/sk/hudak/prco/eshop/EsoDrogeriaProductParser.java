package sk.hudak.prco.eshop;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.api.ProductAction;
import sk.hudak.prco.builder.SearchUrlBuilder;
import sk.hudak.prco.parser.UnitParser;
import sk.hudak.prco.parser.impl.JSoupProductParser;
import sk.hudak.prco.utils.ConvertUtils;
import sk.hudak.prco.utils.UserAgentDataHolder;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static sk.hudak.prco.api.EshopUuid.ESO_DROGERIA;

@Component
public class EsoDrogeriaProductParser extends JSoupProductParser {

    public EsoDrogeriaProductParser(UnitParser unitParser, UserAgentDataHolder userAgentDataHolder, @NotNull SearchUrlBuilder searchUrlBuilder) {
        super(unitParser, userAgentDataHolder, searchUrlBuilder);
    }

    @Override
    public EshopUuid getEshopUuid() {
        return ESO_DROGERIA;
    }

    @Override
    protected int getTimeout() {
        // koli pomalym odozvam davam na 10 sekund
        return 10000;
    }

    @Override
    protected int parseCountOfPages(Document documentList) {
        Elements select = documentList.select("div[class='pagination'] a");
        if (select.size() == 0) {
            return 1;
        }
        int poslednyIndex = select.size() - 2;
        if (poslednyIndex < 1) {
            return 1;
        }
        return Optional.of(select.get(poslednyIndex))
                .map(Element::text)
                .filter(StringUtils::isNotBlank)
                .map(Integer::valueOf)
                .get();
    }

    @Override
    protected List<String> parseNextPage(String searchKeyWord, int currentPageNumber) {
        // pocet stranok v URL je od 0 preto to posuvam -1
        return super.parseNextPage(searchKeyWord, currentPageNumber - 1);
    }

    @Override
    protected List<String> parsePageForProductUrls(Document documentList, int pageNumber) {
        return documentList.select("div[class='product tab_img160 image_first one-preview-image in-stock-y'] div[class='productBody'] div[class='img_box'] a")
                .stream()
                .map(element -> element.attr("href"))
                .filter(StringUtils::isNotBlank)
                .map(href -> getEshopUuid().getProductStartUrl() + href)
                .collect(Collectors.toList());
    }

    @Override
    protected Optional<String> parseProductNameFromDetail(Document documentDetailProduct) {
        return ofNullable(documentDetailProduct.select("#incenterpage2 > div.product-detail-container.in-stock-y > h1").first())
                .map(Element::text);
    }

    @Override
    protected Optional<String> parseProductPictureURL(Document documentDetailProduct) {
        return ofNullable(documentDetailProduct.select("#detail_src_magnifying_small").first())
                .map(element -> element.attr("src"))
                .filter(StringUtils::isNotBlank)
                .map(src -> getEshopUuid().getProductStartUrl() + src);
    }

    @Override
    protected boolean isProductUnavailable(Document documentDetailProduct) {
        return null == documentDetailProduct.select("#buy_btn").first();
    }

    @Override
    protected Optional<BigDecimal> parseProductPriceForPackage(Document documentDetailProduct) {
        return ofNullable(documentDetailProduct.select("span[class='price-vat'] span[class='price-value def_color']").first())
                .map(Element::text)
                .map(String::trim)
                .map(value -> StringUtils.removeEnd(value, "EUR").trim())
                // posledny znak nie je medzera ale nieco co tak vyzera...
                .map(value -> value.substring(0, value.length() - 2))
                .map(ConvertUtils::convertToBigDecimal);
    }

    @Override
    protected Optional<ProductAction> parseProductAction(Document documentDetailProduct) {
        return Optional.empty();
    }

    @Override
    protected Optional<Date> parseProductActionValidity(Document documentDetailProduct) {
        return Optional.empty();
    }
}
