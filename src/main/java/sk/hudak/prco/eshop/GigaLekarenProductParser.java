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

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static sk.hudak.prco.api.EshopUuid.GIGA_LEKAREN;

@Component
public class GigaLekarenProductParser extends JSoupProductParser {


    public GigaLekarenProductParser(UnitParser unitParser, UserAgentDataHolder userAgentDataHolder, SearchUrlBuilder searchUrlBuilder) {
        super(unitParser, userAgentDataHolder, searchUrlBuilder);
    }

    @Override
    public EshopUuid getEshopUuid() {
        return GIGA_LEKAREN;
    }

    @Override
    protected int getTimeout() {
        return TIMEOUT_10_SECOND;
    }

    @Override
    protected int parseCountOfPages(Document documentList) {
        int size = documentList.select("div[class=paging_footer] a").size();
        int i = (size / 2) - 2;
        if (i < 1) {
            return 1;
        }
        return i;
    }

    @Override
    protected List<String> parsePageForProductUrls(Document documentList, int pageNumber) {
        return documentList.select("div[class=top_left] p[class=product_title] a").stream()
                .map(element -> element.attr("href"))
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
    }

    @Override
    protected Optional<String> parseProductNameFromDetail(Document documentDetailProduct) {
        return ofNullable(documentDetailProduct.select("#rw_det1 > table > tbody > tr:nth-child(1) > td:nth-child(2) > strong"))
                .map(Elements::text);
    }

    @Override
    protected Optional<String> parseProductPictureURL(Document documentDetailProduct) {
        return ofNullable(documentDetailProduct.select("#thephoto"))
                .map(elements -> elements.attr("src"))
                .filter(StringUtils::isNotBlank);
    }

    @Override
    protected boolean isProductUnavailable(Document documentDetailProduct) {
        return null == documentDetailProduct.select("#detail_block_form_cart").first();
    }

    @Override
    protected Optional<BigDecimal> parseProductPriceForPackage(Document documentDetailProduct) {
        return ofNullable(documentDetailProduct.select("td.color > strong"))
                .map(Elements::text)
                .filter(StringUtils::isNotBlank)
                .map(value -> StringUtils.removeEnd(value, " €"))
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
