package sk.hudak.prco.eshop;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static sk.hudak.prco.api.EshopUuid.PRVA_LEKAREN;

@Component
public class PrvaLekarenProductParser extends JSoupProductParser {


    public PrvaLekarenProductParser(UnitParser unitParser, UserAgentDataHolder userAgentDataHolder, @NotNull SearchUrlBuilder searchUrlBuilder) {
        super(unitParser, userAgentDataHolder, searchUrlBuilder);
    }

    @Override
    public EshopUuid getEshopUuid() {
        return PRVA_LEKAREN;
    }

    @Override
    protected int getTimeout() {
        // koli pomalym odozvam davam na 10 sekund
        return 10000;
    }

    @Override
    protected int parseCountOfPages(Document documentList) {
        int countOfPages = documentList.select("div[class=pagination] a").size() - 2;
        if (countOfPages < 1) {
            return 1;
        }
        return countOfPages;
    }

    @Override
    protected List<String> parsePageForProductUrls(Document documentList, int pageNumber) {
        List<String> href1 = documentList.select("div[class=productbox ] > a:nth-child(1)").stream()
                .map(element -> element.attr("href"))
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
        List<String> href2 = documentList.select("div[class=productbox last] > a:nth-child(1)").stream()
                .map(element -> element.attr("href"))
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());

        List<String> hrefs = new ArrayList<>(href1);
        hrefs.addAll(href2);
        return hrefs;
    }

    @Override
    protected Optional<String> parseProductNameFromDetail(Document documentDetailProduct) {
        return ofNullable(documentDetailProduct.select("div.detail > div.right > h1"))
                .map(Elements::text);
    }

    @Override
    protected Optional<String> parseProductPictureURL(Document documentDetailProduct) {
        return Optional.ofNullable(documentDetailProduct.select("div.detail > div.left > img"))
                .map(elements -> elements.attr("src"))
                .filter(StringUtils::isNotBlank);
    }

    @Override
    protected boolean isProductUnavailable(Document documentDetailProduct) {
        return documentDetailProduct.select("tr.koupit > td:nth-child(2) > input[type='submit']").first() == null;
    }

    @Override
    protected Optional<BigDecimal> parseProductPriceForPackage(Document documentDetailProduct) {
        return Optional.ofNullable(documentDetailProduct.select("td.cena"))
                .map(Elements::text)
                .map(StringUtils::trim)
                .filter(StringUtils::isNotBlank)
                .map(text -> StringUtils.removeEnd(text, " €"))
                .map(ConvertUtils::convertToBigDecimal);
    }

    @Override
    protected Optional<ProductAction> parseProductAction(Document documentDetailProduct) {


        // TODO
        return Optional.empty();

    }

    @Override
    protected Optional<Date> parseProductActionValidity(Document documentDetailProduct) {
        // TODO
        return Optional.empty();
    }
}
