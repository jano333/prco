package sk.hudak.prco.eshop.pharmacy;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static sk.hudak.prco.api.EshopUuid.MOJA_LEKAREN;

@Slf4j
@Component
public class MojaLekarenProductParser extends JSoupProductParser {

    @Autowired
    public MojaLekarenProductParser(UnitParser unitParser, UserAgentDataHolder userAgentDataHolder, SearchUrlBuilder searchUrlBuilder) {
        super(unitParser, userAgentDataHolder, searchUrlBuilder);
    }

    @Override
    public EshopUuid getEshopUuid() {
        return MOJA_LEKAREN;
    }

    @Override
    protected int getTimeout() {
        // koli pomalym odozvam davam na 15 sekund
        return TIMEOUT_15_SECOND;
    }

    @Override
    protected int parseCountOfPages(Document documentList) {
        Element select = documentList.select("p.pagination__part.pagination__part--page").first();
        if (select == null) {
            return SINGLE_PAGE_ONE;
        }
        return Integer.valueOf(select.children().last().previousElementSibling().previousElementSibling().text());
    }

    @Override
    protected List<String> parsePageForProductUrls(Document documentList, int pageNumber) {
        List<String> urls = new ArrayList<>();
        for (Element element : documentList.select("li[class=product]")) {
            Element aHref = element.child(0).child(0);
            String href = aHref.attr("href");
            urls.add(getEshopUuid().getProductStartUrl() + href);
        }
        return urls;
    }

    @Override
    protected Optional<String> parseProductNameFromDetail(Document documentDetailProduct) {
        Element element = documentDetailProduct.select("div.detail-top.list > div > h1").first();
        if (element == null) {
            element = documentDetailProduct.select("div > article > h1").first();
        }

        return ofNullable(element)
                .map(Element::text)
                .filter(StringUtils::isNotBlank);
    }

    @Override
    protected Optional<String> parseProductPictureURL(Document documentDetailProduct) {
        Element first = documentDetailProduct.select("div.product__img > a > picture > img").first();
        if (first == null) {
            return Optional.empty();
        }
        return Optional.of(getEshopUuid().getProductStartUrl() + first.attr("data-srcset"));
    }

    @Override
    protected boolean isProductUnavailable(Document documentDetailProduct) {
        return JsoupUtils.notExistElement(documentDetailProduct, "#frm-addMultipleToBasket-form > button > meta");
    }

    @Override
    protected Optional<BigDecimal> parseProductPriceForPackage(Document documentDetailProduct) {
        return Optional.of(documentDetailProduct.select("span[itemprop=price]").first())
                .filter(Objects::nonNull)
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
