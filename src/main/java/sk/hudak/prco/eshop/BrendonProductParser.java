package sk.hudak.prco.eshop;

import lombok.extern.slf4j.Slf4j;
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
import static sk.hudak.prco.api.EshopUuid.BRENDON;

@Slf4j
@Component
public class BrendonProductParser extends JSoupProductParser {

    public BrendonProductParser(UnitParser unitParser,
                                UserAgentDataHolder userAgentDataHolder,
                                SearchUrlBuilder searchUrlBuilder) {

        super(unitParser, userAgentDataHolder, searchUrlBuilder);
    }

    @Override
    public EshopUuid getEshopUuid() {
        return BRENDON;
    }

    @Override
    protected int getTimeout() {
        return TIMEOUT_15_SECOND;
    }

    @Override
    protected int parseCountOfPages(Document documentList) {
        return ofNullable(documentList.select("ul[class='pagermenu'] li[class='bluelink'] span").first())
                .map(Element::text)
                .map(text -> StringUtils.removeStart(text, "1 / "))
                .map(Integer::valueOf)
                .orElse(1);
    }

    @Override
    protected List<String> parsePageForProductUrls(Document documentList, int pageNumber) {
        return documentList.select("body > div.maincontent.clear > div > div.col700_container > div > div.middle-left_ > div > a")
                .stream()
                .map(element -> getEshopUuid().getProductStartUrl() + element.attr("href"))
                .collect(Collectors.toList());
    }

    @Override
    protected Optional<String> parseProductNameFromDetail(Document documentDetailProduct) {
        return Optional.ofNullable(documentDetailProduct.select("div.product-name > h1").first())
                .map(Element::text);
    }

    @Override
    protected Optional<String> parseProductPictureURL(Document documentDetailProduct) {
        return ofNullable(documentDetailProduct.select("#picture-slider ul li img").first())
                .map(element -> element.attr("data-src"));
    }

    @Override
    protected boolean isProductUnavailable(Document documentDetailProduct) {
        return !ofNullable(documentDetailProduct.select("div.add-to-cart-panel button[value='Pridať do košíka']").first())
                .isPresent();
    }

    @Override
    protected Optional<BigDecimal> parseProductPriceForPackage(Document documentDetailProduct) {
        Element first = documentDetailProduct.select("div.product-price span").first();
        return ofNullable(first)
                .map(Element::text)
                .map(String::trim)
                .filter(StringUtils::isNotBlank)
                .map(text -> StringUtils.removeEnd(text, " €"))
                .filter(StringUtils::isNotBlank)
                .map(ConvertUtils::convertToBigDecimal);
    }

    @Override
    protected Optional<ProductAction> parseProductAction(Document documentDetailProduct) {
        //TODO
        return Optional.empty();
    }

    @Override
    protected Optional<Date> parseProductActionValidity(Document documentDetailProduct) {
        //TODO
        return Optional.empty();
    }
}
