package sk.hudak.prco.eshop;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static sk.hudak.prco.api.EshopUuid.PILULKA;
import static sk.hudak.prco.utils.ConvertUtils.convertToBigDecimal;
import static sk.hudak.prco.utils.JsoupUtils.getFirstElementByClass;

@Component
public class PilulkaProductParser extends JSoupProductParser {

    @Autowired
    public PilulkaProductParser(UnitParser unitParser, UserAgentDataHolder userAgentDataHolder, SearchUrlBuilder searchUrlBuilder) {
        super(unitParser, userAgentDataHolder, searchUrlBuilder);
    }

    @Override
    public EshopUuid getEshopUuid() {
        return PILULKA;
    }

    @Override
    protected int getTimeout() {
        return TIMEOUT_10_SECOND;
    }

    @Override
    protected int parseCountOfPages(Document documentList) {
        Optional<Element> firstElementByClass = getFirstElementByClass(documentList, "pager");
        if (!firstElementByClass.isPresent()) {
            return 1;
        }
        // hodnotu z posledneho a tagu pod tagom pager
        return Integer.valueOf(firstElementByClass.get().getElementsByTag("a").last().text());
    }

    @Override
    protected List<String> parsePageForProductUrls(Document documentList, int pageNumber) {
        Optional<Elements> allElementsOpt = Optional.ofNullable(documentList.select("div[class=product-list]").first())
                .map(Element::children)
                .map(Elements::first)
                .map(Element::children)
                .map(elements -> elements.select("div > h3 > a"));
        if (!allElementsOpt.isPresent()) {
            return Collections.emptyList();
        }
        return allElementsOpt.get().stream()
                .map(element -> element.select("div > h3 > a").first())
                .filter(Objects::nonNull)
                .map(a -> getEshopUuid().getProductStartUrl() + a.attr("href"))
                .collect(Collectors.toList());
    }

    @Override
    protected Optional<String> parseProductNameFromDetail(Document documentDetailProduct) {
        Element first = documentDetailProduct.select("div[id=product-info] > form > div > h1 > span[itemprop=name]").first();
        if (first != null) {
            return Optional.ofNullable(first.text());
        }
        return ofNullable(documentDetailProduct.select("span[class='product-detail__header pr-3']").first())
                .map(Element::text);
    }

    @Override
    protected boolean isProductUnavailable(Document documentDetailProduct) {
        boolean isProductUnavailable = null == documentDetailProduct.select("input[value=Kúpiť]").first();
        if (isProductUnavailable) {
            isProductUnavailable = null == documentDetailProduct.select("#js-add-to-cart-first").first();
        }
        return isProductUnavailable;
    }

    @Override
    protected Optional<BigDecimal> parseProductPriceForPackage(Document documentDetailProduct) {
        Element first = documentDetailProduct.select("strong[id=priceNew]").first();
        if (first != null) {
            String substring = first.text().substring(0, first.text().length() - 2);
            return Optional.of(convertToBigDecimal(substring));
        }

        first = documentDetailProduct.select("span[class='fs-28 mb-3 text-primary fwb']").first();
        return Optional.ofNullable(first)
                .map(Element::text)
                .map(text -> StringUtils.removeEnd(text, " €"))
                .map(ConvertUtils::convertToBigDecimal);
    }

    @Override
    protected Optional<String> parseProductPictureURL(Document documentDetailProduct) {
        Element first = documentDetailProduct.select("#pr-img-carousel > ul > li > a > img").first();
        if (first != null) {
            return Optional.of(getEshopUuid().getProductStartUrl() + "/" + first.attr("src"));
        }
        first = documentDetailProduct.select("div[class='product-detail__images'] > picture > a > img").first();
        if (first == null) {
            first = documentDetailProduct.select("div[class='product-detail__images w-100 js-carousel-item'] > picture > a > img").first();
        }
        return ofNullable(first)
                .map(JsoupUtils::dataSrcAttribute)
                .map(text -> text.substring(1))
                .map(text -> getEshopUuid().getProductStartUrl() + "/"+ text);
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
