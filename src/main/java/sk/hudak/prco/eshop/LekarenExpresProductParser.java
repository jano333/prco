package sk.hudak.prco.eshop;

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
import sk.hudak.prco.utils.JsoupUtils;
import sk.hudak.prco.utils.UserAgentDataHolder;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static sk.hudak.prco.api.EshopUuid.LEKAREN_EXPRES;
import static sk.hudak.prco.utils.JsoupUtils.notExistElement;

@Component
public class LekarenExpresProductParser extends JSoupProductParser {

    public LekarenExpresProductParser(UnitParser unitParser, UserAgentDataHolder userAgentDataHolder, @NotNull SearchUrlBuilder searchUrlBuilder) {
        super(unitParser, userAgentDataHolder, searchUrlBuilder);
    }

    @Override
    public EshopUuid getEshopUuid() {
        return LEKAREN_EXPRES;
    }

    @Override
    protected int parseCountOfPages(Document documentList) {
        int size = documentList.select("div[class='paging'] p > a").size();
        if (size == 0) {
            return 1;
        }
        size = size / 2;
        size = size - 1;
        if (size == 0) {
            return 1;
        }
        return size;
    }

    @Override
    protected List<String> parsePageForProductUrls(Document documentList, int pageNumber) {
        return documentList.select("div[class='product'] h3 a")
                .stream()
                .map(JsoupUtils::hrefAttribute)
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
    }

    @Override
    protected Optional<String> parseProductNameFromDetail(Document documentDetailProduct) {
        return ofNullable(documentDetailProduct.select("#meta > h1").first())
                .map(Element::text);
    }

    @Override
    protected Optional<String> parseProductPictureURL(Document documentDetailProduct) {
        return ofNullable(documentDetailProduct.select("#topImg > a").first())
                .map(JsoupUtils::hrefAttribute);
    }

    @Override
    protected boolean isProductUnavailable(Document documentDetailProduct) {
        return notExistElement(documentDetailProduct, "#meta > div.buy > div.button > a > span");
    }

    @Override
    protected Optional<BigDecimal> parseProductPriceForPackage(Document documentDetailProduct) {
        return ofNullable(documentDetailProduct.select("#meta > div.prices > span.price").first())
                .map(Element::text)
                .filter(StringUtils::isNotBlank)
                .map(text -> StringUtils.removeEnd(text, " €"))
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
