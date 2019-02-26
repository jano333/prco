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

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static sk.hudak.prco.api.EshopUuid.MAGANO;

@Component
public class ManagoProductParser extends JSoupProductParser {

    public ManagoProductParser(UnitParser unitParser, UserAgentDataHolder userAgentDataHolder, SearchUrlBuilder searchUrlBuilder) {
        super(unitParser, userAgentDataHolder, searchUrlBuilder);
    }

    @Override
    public EshopUuid getEshopUuid() {
        return MAGANO;
    }

    @Override
    protected int parseCountOfPages(Document documentList) {
        int size = documentList.select("ul[class='pagination'] li").size();
        if (size == 0) {
            return 1;
        }
        return size - 1;
    }

    @Override
    protected List<String> parsePageForProductUrls(Document documentList, int pageNumber) {
        return documentList.select("div[class='product-card'] > div > a")
                .stream()
                .map(element -> element.attr("href"))
                .filter(StringUtils::isNotBlank)
                .map(href -> getEshopUuid().getProductStartUrl() + href)
                .collect(Collectors.toList());
    }

    @Override
    protected Optional<String> parseProductPictureURL(Document documentDetailProduct) {
        return ofNullable(documentDetailProduct.select("div[class='images single-image'] a img").first())
                .map(element -> element.attr("src"))
                .filter(StringUtils::isNotBlank)
                .map(src -> getEshopUuid().getProductStartUrl() + src);
    }

    @Override
    protected Optional<String> parseProductNameFromDetail(Document documentDetailProduct) {
        return ofNullable(documentDetailProduct.select("h1[itemprop='name']").first())
                .map(Element::text)
                .filter(StringUtils::isNotBlank);
    }

    @Override
    protected boolean isProductUnavailable(Document documentDetailProduct) {
        return null == documentDetailProduct.select("form > input[name='ok']").first();
    }

    @Override
    protected Optional<BigDecimal> parseProductPriceForPackage(Document documentDetailProduct) {
        return ofNullable(documentDetailProduct.select("p[class='price']").first())
                .map(Element::text)
                .map(String::trim)
                .filter(value -> value.indexOf("€") != -1)
                .map(value -> value.substring(0, value.indexOf("€") - 1))
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
