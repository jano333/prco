package sk.hudak.prco.eshop;

import lombok.extern.slf4j.Slf4j;
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

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static sk.hudak.prco.api.EshopUuid.LEKAREN_BELLA;

@Slf4j
@Component
public class LekarenBellaProductParser extends JSoupProductParser {

    public LekarenBellaProductParser(UnitParser unitParser, UserAgentDataHolder userAgentDataHolder, SearchUrlBuilder searchUrlBuilder) {
        super(unitParser, userAgentDataHolder, searchUrlBuilder);
    }

    @Override
    public EshopUuid getEshopUuid() {
        return LEKAREN_BELLA;
    }

    @Override
    protected int parseCountOfPages(Document documentList) {
        int size = documentList.select("ul[class='control-products control-products-sm2'] > li").size();
        if (size < 1) {
            return 0;
        }
        return size / 2;
    }

    @Override
    protected List<String> parsePageForProductUrls(Document documentList, int pageNumber) {
        return documentList.select("div[class='product-items '] > div[class='row'] > div > a").stream()
                .map(element -> element.attr("href"))
                .collect(Collectors.toList());
    }

    @Override
    protected Optional<String> parseProductNameFromDetail(Document documentDetailProduct) {
        Element first = documentDetailProduct.select("h1[class='product-detail-title title-sm']").first();
        if (first == null) {
            first = documentDetailProduct.select("h1[class='product-detail-title title-xs']").first();
        }
        return ofNullable(first)
                .map(Element::text)
                .filter(StringUtils::isNotBlank);
    }

    @Override
    protected Optional<String> parseProductPictureURL(Document documentDetailProduct) {
        Elements select = documentDetailProduct.select("img[class='img-responsive']");
        return ofNullable(select)
                .map(elements -> elements.attr("src"))
                .filter(StringUtils::isNotBlank)
                .map(value -> value.substring(2));
    }

    @Override
    protected boolean isProductUnavailable(Document documentDetailProduct) {
        return null == documentDetailProduct.select("button[class='btn btn-primary btn-purchase']").first();
    }

    @Override
    protected Optional<BigDecimal> parseProductPriceForPackage(Document documentDetailProduct) {
        return ofNullable(documentDetailProduct.select("span[itemprop='price']"))
                .map(Elements::text)
                .filter(StringUtils::isNotBlank)
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
