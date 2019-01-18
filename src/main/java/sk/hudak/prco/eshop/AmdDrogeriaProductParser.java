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
import sk.hudak.prco.parser.UnitParser;
import sk.hudak.prco.parser.impl.JSoupProductParser;
import sk.hudak.prco.utils.ConvertUtils;
import sk.hudak.prco.utils.UserAgentDataHolder;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static sk.hudak.prco.api.EshopUuid.AMD_DROGERIA;

@Slf4j
@Component
public class AmdDrogeriaProductParser extends JSoupProductParser {

    @Autowired
    public AmdDrogeriaProductParser(UnitParser unitParser, UserAgentDataHolder userAgentDataHolder, SearchUrlBuilder searchUrlBuilder) {
        super(unitParser, userAgentDataHolder, searchUrlBuilder);
    }

    @Override
    public EshopUuid getEshopUuid() {
        return AMD_DROGERIA;
    }

    @Override
    protected int getTimeout() {
        // koli pomalym odozvam davam na 10 sekund
        return 10000;
    }


    @Override
    protected int parseCountOfPages(Document documentList) {
        Optional<Element> first = Optional.ofNullable(documentList.select("div.searching__toolbar-bottom > div > ul").first());
        if (!first.isPresent()) {
            return 1;
        }
        // li elements
        Elements children = first.get().children();
        if (children.size() < 3) {
            return 1;
        }
        Element element = children.get(children.size() - 3);
        String text = element.text();
        return Integer.valueOf(text);
    }

    @Override
    protected List<String> parsePageForProductUrls(Document documentList, int pageNumber) {
        return documentList.select("div.product-sm__name > h2 > a").stream()
                .map(element -> element.attr("href"))
                .filter(StringUtils::isNotBlank)
                .map(href -> getEshopUuid().getProductStartUrl() + href)
                .collect(Collectors.toList());
    }

    @Override
    protected boolean isProductUnavailable(Document documentDetailProduct) {
        return documentDetailProduct.select("a[title='Vložiť do košíka']").isEmpty();
    }

    @Override
    protected Optional<String> parseProductNameFromDetail(Document documentDetailProduct) {
        return Optional.ofNullable(documentDetailProduct.select("div.product__title > h1").first())
                .map(Element::text);
    }

    @Override
    protected Optional<String> parseProductPictureURL(Document documentDetailProduct) {
        return Optional.ofNullable(documentDetailProduct.select("body > section.product > div > div > div.product__left-column.col-lg-6 > div > a > img").first())
                .map(element -> element.attr("src"))
                .filter(StringUtils::isNotBlank)
                .map(src -> getEshopUuid().getProductStartUrl() + src);
    }

    @Override
    protected Optional<BigDecimal> parseProductPriceForPackage(Document documentDetailProduct) {
        return Optional.ofNullable(documentDetailProduct.select("div.product__price > span").first())
                .map(Element::text)
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
