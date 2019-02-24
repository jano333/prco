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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;
import static sk.hudak.prco.api.EshopUuid.KID_MARKET;
import static sk.hudak.prco.utils.JsoupUtils.notExistElement;

@Slf4j
@Component
public class KidMarketProductParser extends JSoupProductParser {

    public KidMarketProductParser(UnitParser unitParser, UserAgentDataHolder userAgentDataHolder, SearchUrlBuilder searchUrlBuilder) {
        super(unitParser, userAgentDataHolder, searchUrlBuilder);
    }

    @Override
    public EshopUuid getEshopUuid() {
        return KID_MARKET;
    }

    @Override
    protected int parseCountOfPages(Document documentList) {
        return ofNullable(documentList.select("span[class=heading-counter]").first())
                .map(Element::text)
                .map(textValue -> Integer.valueOf(StringUtils.removeStart(textValue, "Nájdené výsledky: ")))
                .orElse(0);
    }

    @Override
    protected List<String> parsePageForProductUrls(Document documentList, int pageNumber) {
        //FIXME prepisat cez strem
        Element first = documentList.select("#product_list").first();
        if (first == null) {
            return Collections.emptyList();
        }

        List<String> urls = new ArrayList<>();
        first.children().forEach(element -> {
                    String href = element.select("h5 a").first().attr("href");
                    urls.add(href.substring(0, href.lastIndexOf("?")));
                }
        );
        return urls;
    }

    @Override
    protected boolean isProductUnavailable(Document documentDetailProduct) {
        return notExistElement(documentDetailProduct, "div[class='box-info-product'] p[id=add_to_cart]");
    }

    @Override
    protected Optional<String> parseProductNameFromDetail(Document documentDetailProduct) {
        return ofNullable(documentDetailProduct.select("h1[class=page-heading]").first())
                .map(Element::text);
    }

    @Override
    protected Optional<String> parseProductPictureURL(Document documentDetailProduct) {
        return ofNullable(documentDetailProduct.select("#bigpic").first())
                .map(element -> element.attr("src"));
    }

    @Override
    protected Optional<BigDecimal> parseProductPriceForPackage(Document documentDetailProduct) {
        return ofNullable(documentDetailProduct.select("#our_price_display").first())
                .map(Element::text)
                .map(text -> StringUtils.removeEnd(text, "€"))
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
