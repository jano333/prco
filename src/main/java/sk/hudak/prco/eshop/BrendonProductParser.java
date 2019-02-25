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

import static java.util.Optional.empty;
import static java.util.Optional.of;
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
        // koli pomalym odozvam davam na 15 sekund
        return 15000;
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
        Element select = documentDetailProduct.select("div[class='col400']").first();
        if (select == null) {
            return empty();
        }
        Elements children = select.children();
        StringBuilder str = new StringBuilder();
        for (Element child : children) {
            String tagName = child.tag().getName();
            if ("a".equals(tagName)) {
                str.append(child.children().first().text());
                str.append(" ");
            }
            if ("span".equals(tagName)) {
                str.append(child.text());
            }
        }
        String result = str.toString();
        if (StringUtils.isBlank(result)) {
            return empty();
        }
        return of(result);
    }

    @Override
    protected Optional<String> parseProductPictureURL(Document documentDetailProduct) {
        return ofNullable(documentDetailProduct.select("#MainPicture").first())
                .map(element -> element.attr("src"));
    }

    @Override
    protected boolean isProductUnavailable(Document documentDetailProduct) {
        Elements select = documentDetailProduct.select("div[class='float-left']");
        for (Element element : select) {
            if ("Do koša".equals(element.text())) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected Optional<BigDecimal> parseProductPriceForPackage(Document documentDetailProduct) {
        return Optional.ofNullable(documentDetailProduct.select("span[class='margin-right40']").first())
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
