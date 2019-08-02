package sk.hudak.prco.eshop;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.api.ProductAction;
import sk.hudak.prco.builder.SearchUrlBuilder;
import sk.hudak.prco.parser.UnitParser;
import sk.hudak.prco.parser.impl.JSoupProductParser;
import sk.hudak.prco.utils.ConvertUtils;
import sk.hudak.prco.utils.JsoupUtils;
import sk.hudak.prco.utils.UserAgentDataHolder;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static sk.hudak.prco.api.EshopUuid.PILULKA_24;

@Component
public class Pilulka24ProductParser extends JSoupProductParser {

    public Pilulka24ProductParser(UnitParser unitParser, UserAgentDataHolder userAgentDataHolder, SearchUrlBuilder searchUrlBuilder) {
        super(unitParser, userAgentDataHolder, searchUrlBuilder);
    }

    @Override
    public EshopUuid getEshopUuid() {
        return PILULKA_24;
    }

    @Override
    protected int parseCountOfPages(Document documentList) {
        return ofNullable(documentList.select("#js-product-list-content > p").first())
                .map(Element::text)
                .filter(StringUtils::isNotBlank)
                .map(text -> text.substring(0, text.indexOf(' ')))
                .filter(NumberUtils::isParsable)
                .map(Integer::valueOf)
                .map(countOfProducts -> JsoupUtils.calculateCountOfPages(countOfProducts, getEshopUuid().getMaxCountOfProductOnPage()))
                .orElse(SINGLE_PAGE_ONE);
    }

    @Override
    protected List<String> parsePageForProductUrls(Document documentList, int pageNumber) {
        return documentList.select("div[class='col-6 col-sm-4 col-lg-3 p-0 product-card__border'] > div:nth-child(1) > div > a").stream()
                .map(JsoupUtils::hrefAttribute)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toSet())// aby som zo 120 urobil 40 lebo je duplicita a neviem cez selector urobit
                .stream()
                .map(text -> text.substring(1))
                .map(text -> getEshopUuid().getProductStartUrl() + text)
                .collect(Collectors.toList());
    }

    @Override
    protected Optional<String> parseProductNameFromDetail(Document documentDetailProduct) {
        return ofNullable(documentDetailProduct.select("span[class='product-detail__header pr-3']").first())
                .map(Element::text);
    }

    @Override
    protected Optional<String> parseProductPictureURL(Document documentDetailProduct) {
        Element first = documentDetailProduct.select("div[class='product-detail__images'] > picture > a > img").first();
        if (first == null) {
            first = documentDetailProduct.select("div[class='product-detail__images w-100 js-carousel-item'] > picture > a > img").first();
        }
        return ofNullable(first)
                .map(JsoupUtils::dataSrcAttribute)
                .map(text -> text.substring(1))
                .map(text -> getEshopUuid().getProductStartUrl() + text);
    }

    @Override
    protected boolean isProductUnavailable(Document documentDetailProduct) {
        return documentDetailProduct.select("#js-add-to-cart-first").first() == null;
    }

    @Override
    protected Optional<BigDecimal> parseProductPriceForPackage(Document documentDetailProduct) {
        return ofNullable(documentDetailProduct.select("span[class='fs-28 mb-3 text-primary fwb']").first())
                .map(Element::text)
                .map(price -> StringUtils.removeEnd(price, " €"))
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
