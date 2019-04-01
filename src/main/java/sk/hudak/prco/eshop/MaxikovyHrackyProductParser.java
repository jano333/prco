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
import static org.apache.commons.lang3.StringUtils.removeStart;
import static sk.hudak.prco.api.EshopUuid.MAXIKOVY_HRACKY;

@Component
public class MaxikovyHrackyProductParser extends JSoupProductParser {

    public MaxikovyHrackyProductParser(UnitParser unitParser, UserAgentDataHolder userAgentDataHolder, SearchUrlBuilder searchUrlBuilder) {
        super(unitParser, userAgentDataHolder, searchUrlBuilder);
    }

    @Override
    public EshopUuid getEshopUuid() {
        return MAXIKOVY_HRACKY;
    }

    @Override
    protected int getTimeout() {
        // koli pomalym odozvam davam na 10 sekund
        return 10000;
    }

    @Override
    protected int parseCountOfPages(Document documentList) {
        //Optional[Zobrazuji 1-60 z 807 produktů]
        Optional<Integer> countOfProductsOpt = ofNullable(documentList.select("div.line-sort > div > div.col-sm-4.text-right.top-12.font-12").first())
                .map(Element::text)
                .map(text -> StringUtils.removeEnd(text, " produktů"))
                .map(text -> removeStart(text, text.substring(0, text.indexOf(" z ") + 3)))
                .map(String::trim)
                .map(Integer::valueOf);

        if (!countOfProductsOpt.isPresent()) {
            return 1;
        }
        //TODO podla mna to je ZLE pozri Feedo
        int hh = countOfProductsOpt.get() % getEshopUuid().getMaxCountOfProductOnPage();

        if (hh > 0) {
            return countOfProductsOpt.get() + 1;
        } else {
            return countOfProductsOpt.get();
        }
    }

    @Override
    protected List<String> parsePageForProductUrls(Document documentList, int pageNumber) {
        return documentList.select("#product-list-box > div[class='col-sm-4 col-lg-2-4']").stream()
                .map(element -> element.select("div > div > a").first())
                .map(a -> getEshopUuid().getProductStartUrl() + a.attr("href"))
                .map(link -> link + "?zmena_meny=EUR")
                .collect(Collectors.toList());
    }

    @Override
    protected Optional<String> parseProductNameFromDetail(Document documentDetailProduct) {
        return ofNullable(documentDetailProduct.select("#product-info > div.col-xs-12.col-md-5 > h1").first())
                .map(Element::text);
    }

    @Override
    protected Optional<String> parseProductPictureURL(Document documentDetailProduct) {
        return ofNullable(documentDetailProduct.select("#product-info > div.col-xs-12.col-md-7 > div.main-image > a > img").first())
                .map(element -> element.attr("src"));
    }

    @Override
    protected boolean isProductUnavailable(Document documentDetailProduct) {
        return !ofNullable(documentDetailProduct.select("button[class='insert-cart btn btn-default']").first())
                .isPresent();
    }

    @Override
    protected Optional<BigDecimal> parseProductPriceForPackage(Document documentDetailProduct) {
        Element first = documentDetailProduct.select("div[class='price text-red text-right']").first();
        if(first == null){
            first = documentDetailProduct.select("div[class='price text-red text-right simple']").first();
        }
        return ofNullable(first)
                .map(Element::text)
                .map(text -> StringUtils.removeEnd(text, " €"))
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
