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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
public class PerinbabaProductParser extends JSoupProductParser {

    public PerinbabaProductParser(UnitParser unitParser, UserAgentDataHolder userAgentDataHolder, SearchUrlBuilder searchUrlBuilder) {
        super(unitParser, userAgentDataHolder, searchUrlBuilder);
    }

    @Override
    public EshopUuid getEshopUuid() {
        return EshopUuid.PERINBABA;
    }

    @Override
    protected int getTimeout() {
        // koli pomalym odozvam davam na 10 sekund
        return 10000;
    }

    @Override
    protected int parseCountOfPages(Document documentList) {
        Element element = documentList.select("div[class=pages] ol").first();
        if (element == null) {
            return 1;
        }
        int size = element.children().size();
        if (size == 0) {
            return 1;
        }
        return size - 1;
    }

    @Override
    protected List<String> parsePageForProductUrls(Document documentList, int pageNumber) {
        List<String> urls = new ArrayList<>();
        documentList.select("div[class=category-products] ul").stream()
                .filter(element -> Objects.nonNull(element.attr("products-grid")))
                .forEach(element -> {
                    Element aElement = element.select("li[class=item first] h2 a").first();
                    if (aElement != null) {
                        String href = aElement.attr("href");
                        urls.add(href);
                    }
                });
        return urls;
    }

    @Override
    protected Optional<String> parseProductNameFromDetail(Document documentDetailProduct) {
        Element first = documentDetailProduct.select("#product_addtocart_form > div.product-shop > div > div.product-name")
                .first();
        if (first == null) {
            return Optional.empty();
        }
        Element first1 = first.children().first();
        if (first1 == null) {
            return Optional.empty();
        }
        return Optional.of(first1.text());
    }

    @Override
    protected Optional<String> parseProductPictureURL(Document documentDetailProduct) {
        Element first = documentDetailProduct.select("div.prolabel-wrapper > a > img").first();
        if (first == null) {
            return Optional.empty();
        }
        return Optional.of(first.attr("src"));
    }

    @Override
    protected boolean isProductUnavailable(Document documentDetailProduct) {
        return documentDetailProduct.select("button[title=Kúpiť]").first() == null;
    }

    @Override
    protected Optional<BigDecimal> parseProductPriceForPackage(Document documentDetailProduct) {
        return Optional.ofNullable(documentDetailProduct.select("span[class=price]").first())
                .map(element -> StringUtils.removeEnd(element.text(), " €"))
                .map(text -> ConvertUtils.convertToBigDecimal(text));
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
