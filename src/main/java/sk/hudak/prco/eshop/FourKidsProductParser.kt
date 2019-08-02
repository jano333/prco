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
import sk.hudak.prco.utils.UserAgentDataHolder;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static org.apache.commons.lang3.StringUtils.removeEnd;
import static org.apache.commons.lang3.StringUtils.removeStart;

@Component
public class FourKidsProductParser extends JSoupProductParser {

    public FourKidsProductParser(UnitParser unitParser, UserAgentDataHolder userAgentDataHolder, SearchUrlBuilder searchUrlBuilder) {
        super(unitParser, userAgentDataHolder, searchUrlBuilder);
    }

    @Override
    public EshopUuid getEshopUuid() {
        return EshopUuid.FOUR_KIDS;
    }

    @Override
    protected int getTimeout() {
        return TIMEOUT_10_SECOND;
    }

    @Override
    protected int parseCountOfPages(Document documentList) {
        Optional<Integer> countOfProductsOpt = ofNullable(documentList.select("body > div.inner.relative > div > div > div:nth-child(5) > div.col-md-4.hidden-xs.hidden-sm > span").first())
                .map(Element::text)
                .filter(text -> text.indexOf(" z ") != -1)
                .map(text -> removeEnd(text, "produktov"))
                .map(text -> removeEnd(text, "produktů"))
                .map(text -> removeStart(text, text.substring(0, text.indexOf(" z ") + 3)))
                .map(String::trim)
                .map(Integer::valueOf);

        if (!countOfProductsOpt.isPresent()) {
            return 1;
        }

        int hh = countOfProductsOpt.get() % getEshopUuid().getMaxCountOfProductOnPage();

        if (hh > 0) {
            return countOfProductsOpt.get() + 1;
        } else {
            return countOfProductsOpt.get();
        }
    }

    @Override
    protected List<String> parsePageForProductUrls(Document documentList, int pageNumber) {
        return documentList.select("#products-list > div > a").stream()
                .map(element -> getEshopUuid().getProductStartUrl() + element.attr("href"))
                .collect(Collectors.toList());
    }

    @Override
    protected Optional<String> parseProductNameFromDetail(Document documentDetailProduct) {
        return ofNullable(documentDetailProduct.select("div.product-detail > div.col-xs-12.col-md-7 > h1").first())
                .map(Element::text);
    }

    @Override
    protected Optional<String> parseProductPictureURL(Document documentDetailProduct) {
        return ofNullable(documentDetailProduct.select("div.product-detail > div.col-xs-12.col-md-7 > div.img-detail.relative.text-center > a > img").first())
                .map(element -> element.attr("src"));
    }

    @Override
    protected boolean isProductUnavailable(Document documentDetailProduct) {
        return !ofNullable(documentDetailProduct.select("button[class='insert-cart cart']").first())
                .isPresent();
    }

    @Override
    protected Optional<BigDecimal> parseProductPriceForPackage(Document documentDetailProduct) {
        return Optional.ofNullable(documentDetailProduct.select("p[class='price']").first())
                .map(Element::text)
                .map(text -> StringUtils.removeEnd(text, " €"))
                .map(text -> ConvertUtils.convertToBigDecimal(text));
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
