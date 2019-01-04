package sk.hudak.prco.eshop;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.api.ProductAction;
import sk.hudak.prco.builder.SearchUrlBuilder;
import sk.hudak.prco.exception.PrcoRuntimeException;
import sk.hudak.prco.parser.UnitParser;
import sk.hudak.prco.parser.impl.JSoupProductParser;
import sk.hudak.prco.utils.UserAgentDataHolder;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static sk.hudak.prco.api.EshopUuid.FEEDO;
import static sk.hudak.prco.utils.ConvertUtils.convertToBigDecimal;
import static sk.hudak.prco.utils.JsoupUtils.existElement;
import static sk.hudak.prco.utils.JsoupUtils.getTextFromFirstElementByClass;
import static sk.hudak.prco.utils.JsoupUtils.notExistElement;

@Slf4j
@Component
public class FeedoProductParser extends JSoupProductParser {

    @Autowired
    public FeedoProductParser(UnitParser unitParser, UserAgentDataHolder userAgentDataHolder, SearchUrlBuilder searchUrlBuilder) {
        super(unitParser, userAgentDataHolder, searchUrlBuilder);
    }

    @Override
    public EshopUuid getEshopUuid() {
        return FEEDO;
    }

    @Override
    protected int getTimeout() {
        // koli pomalym odozvam davam na 15 sekund
        return 15000;
    }

    @Override
    protected int parseCountOfPages(Document documentList) {
        String countOfProductString = Optional.ofNullable(documentList.select("#content > div.clearfix.mb-2 > h1:nth-child(1) > span").first())
                .map(Element::text)
                .filter(text -> text.contains("("))
                .filter(text -> text.contains(")"))
                .map(text -> text.substring(text.indexOf('(') + 1, text.indexOf(')')))
                .orElseThrow(() -> new PrcoRuntimeException("None product count found for: " + documentList.location()));

        return calculateCountOfPages(Integer.valueOf(countOfProductString), getEshopUuid().getMaxCountOfProductOnPage());
    }

    //TODO move to utils...
    public static int calculateCountOfPages(int countOfProduct, int pagging) {
        int hh = countOfProduct % pagging;

        int result = countOfProduct / pagging;

        if (hh > 0) {
            return result + 1;
        } else {
            return result;
        }
    }

    @Override
    protected List<String> parsePageForProductUrls(Document documentList, int pageNumber) {
        return documentList.select("div[class=box-product__top]").stream()
                .map(element -> element.select("h1 a").first())
                .map(element -> element.attr("href"))
                .filter(StringUtils::isNotBlank)
                .map(href -> {
                    if (href.endsWith("/")) {
                        return href.substring(0, href.length() - 1);
                    } else {
                        return href;
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    protected boolean isProductUnavailable(Document documentDetailProduct) {
        return notExistElement(documentDetailProduct, "button[class=btn btn-danger btn-large cart]");
    }

    @Override
    protected Optional<String> parseProductNameFromDetail(Document documentDetailProduct) {
        return getTextFromFirstElementByClass(documentDetailProduct, "product-detail-heading hidden-xs hidden-sm");
    }

    @Override
    protected Optional<BigDecimal> parseProductPriceForPackage(Document documentDetailProduct) {
        // skusim -> premim cena
        Elements select = documentDetailProduct.select("div[class=price price-premium] span");
        if (select.isEmpty()) {
            // ak sa nenajde tak skusim -> akcna cena
            select = documentDetailProduct.select("div[class=price price-discount] span");
            if (select.isEmpty()) {
                // ak sa nenajde tak skusim -> normalna cena
                select = documentDetailProduct.select("div[class=price price-base] span");
                if (select.isEmpty()) {
                    return Optional.empty();
                }
            }
        }

        String html = select.get(0).html();
        if (StringUtils.isBlank(html)) {
            return Optional.empty();
        }
        int endIndex = html.indexOf("&nbsp;");
        if (-1 == endIndex) {
            return Optional.empty();
        }
        String cenaZaBalenie = html.substring(0, endIndex);
        return Optional.of(convertToBigDecimal(cenaZaBalenie));
    }

    @Override
    protected Optional<String> parseProductPictureURL(Document documentDetailProduct) {
        Elements select = documentDetailProduct.select("div[class=box-image]");
        if (select.isEmpty()) {
            return Optional.empty();
        }
        Element child = select.get(0).child(0);
        String href = child.attr("href");
        return Optional.ofNullable(href);
    }

    @Override
    protected Optional<ProductAction> parseProductAction(Document documentDetailProduct) {
        // premium cena
        if (existElement(documentDetailProduct, "div[class=price price-premium]")) {
            return Optional.of(ProductAction.IN_ACTION);
        }
        // akcna cena
        if (existElement(documentDetailProduct, "div[class=price price-discount]")) {
            return Optional.of(ProductAction.IN_ACTION);
        }
        return Optional.of(ProductAction.NON_ACTION);
    }

    @Override
    protected Optional<Date> parseProductActionValidity(Document documentDetailProduct) {
        // feedo nepodporuje
        return Optional.empty();
    }
}
