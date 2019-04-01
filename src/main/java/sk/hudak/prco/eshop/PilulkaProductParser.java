package sk.hudak.prco.eshop;

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
import sk.hudak.prco.utils.UserAgentDataHolder;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

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
        return EshopUuid.PILULKA;
    }

    @Override
    protected int getTimeout() {
        // koli pomalym odozvam davam na 10 sekund
        return 10000;
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
        if (first == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(first.text());
    }

    @Override
    protected boolean isProductUnavailable(Document documentDetailProduct) {
        return null == documentDetailProduct.select("input[value=Kúpiť]").first();
    }

    @Override
    protected Optional<BigDecimal> parseProductPriceForPackage(Document documentDetailProduct) {
        Element first = documentDetailProduct.select("strong[id=priceNew]").first();
        if (first == null) {
            return Optional.empty();
        }
        String substring = first.text().substring(0, first.text().length() - 2);
        return Optional.of(convertToBigDecimal(substring));
    }

    @Override
    protected Optional<String> parseProductPictureURL(Document documentDetailProduct) {
        Element first = documentDetailProduct.select("#pr-img-carousel > ul > li > a > img").first();
        if (first == null) {
            return Optional.empty();
        }
        return Optional.of(getEshopUuid().getProductStartUrl() + "/" + first.attr("src"));
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
