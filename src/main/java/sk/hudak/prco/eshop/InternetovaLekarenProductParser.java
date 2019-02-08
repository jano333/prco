package sk.hudak.prco.eshop;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
import static sk.hudak.prco.api.EshopUuid.INTERNETOVA_LEKAREN;

@Component
public class InternetovaLekarenProductParser extends JSoupProductParser {

    public InternetovaLekarenProductParser(UnitParser unitParser, UserAgentDataHolder userAgentDataHolder, @NotNull SearchUrlBuilder searchUrlBuilder) {
        super(unitParser, userAgentDataHolder, searchUrlBuilder);
    }

    @Override
    public EshopUuid getEshopUuid() {
        return INTERNETOVA_LEKAREN;
    }

    @Override
    protected int parseCountOfPages(Document documentList) {
        int i = documentList.select("div[class=indent] div[class='col col-pager text-right'] div[class=pager] > span").size() - 3;
        if (i < 1) {
            return 1;
        }
        return i;
    }

    @Override
    protected List<String> parsePageForProductUrls(Document documentList, int pageNumber) {
        return documentList.select("#products > div > div > a").stream()
                .map(element -> element.attr("href"))
                .filter(StringUtils::isNotBlank)
                .map(text -> getEshopUuid().getProductStartUrl() + text)
                .collect(Collectors.toList());
    }

    @Override
    protected Optional<String> parseProductNameFromDetail(Document documentDetailProduct) {
        return ofNullable(documentDetailProduct.select("#product-detail > div > div.header.block-green.fs-large.bold.radius-top > h1").first())
                .map(Element::text);
    }

    @Override
    protected Optional<String> parseProductPictureURL(Document documentDetailProduct) {
        return ofNullable(documentDetailProduct.select("#productImg > img.image").first())
                .map(element -> element.attr("src"))
                .filter(StringUtils::isNotBlank)
                .map(text -> getEshopUuid().getProductStartUrl() + text);
    }

    @Override
    protected boolean isProductUnavailable(Document documentDetailProduct) {
        return !Optional.ofNullable(documentDetailProduct.select("button[class='addToCartBtn btn btn-big radius plastic wood']").first())
                .isPresent();
    }

    @Override
    protected Optional<BigDecimal> parseProductPriceForPackage(Document documentDetailProduct) {
        return ofNullable(documentDetailProduct.select("strong[class=fs-xxlarge]").first())
                .map(Element::text)
                .map(value -> StringUtils.removeEnd(value, "€"))
                .map(ConvertUtils::convertToBigDecimal);
    }

    @Override
    protected Optional<ProductAction> parseProductAction(Document documentDetailProduct) {
        //TODO impl
        return Optional.empty();

    }

    @Override
    protected Optional<Date> parseProductActionValidity(Document documentDetailProduct) {
        //TODO impl
        return Optional.empty();
    }
}
