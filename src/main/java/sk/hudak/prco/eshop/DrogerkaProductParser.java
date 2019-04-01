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
import static sk.hudak.prco.api.EshopUuid.DROGERKA;

@Component
public class DrogerkaProductParser extends JSoupProductParser {

    public DrogerkaProductParser(UnitParser unitParser, UserAgentDataHolder userAgentDataHolder, SearchUrlBuilder searchUrlBuilder) {
        super(unitParser, userAgentDataHolder, searchUrlBuilder);
    }

    @Override
    public EshopUuid getEshopUuid() {
        return DROGERKA;
    }

    @Override
    protected int getTimeout() {
        // koli pomalym odozvam davam na 10 sekund
        return 10000;
    }

    @Override
    protected int parseCountOfPages(Document documentList) {
        int size = documentList.select("ul[class='pagination'] li").size();
        if (size == 0) {
            return 1;
        }
        return size - 2;
    }

    @Override
    protected List<String> parsePageForProductUrls(Document documentList, int pageNumber) {
        return documentList.select("div[class='col-xs-6 col-sm-4 col-md-3'] div div[class='desc'] a")
                .stream()
                .map(element -> element.attr("href"))
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
    }

    @Override
    protected Optional<String> parseProductNameFromDetail(Document documentDetailProduct) {
        return ofNullable(documentDetailProduct.select("#product > div.text > h1").first())
                .map(Element::text)
                .filter(StringUtils::isNotBlank);
    }

    @Override
    protected Optional<String> parseProductPictureURL(Document documentDetailProduct) {
        return ofNullable(documentDetailProduct.select("#product > div.img.thumbnails > a > img").first())
                .map(element -> element.attr("src"))
                .filter(StringUtils::isNotBlank);
    }

    @Override
    protected boolean isProductUnavailable(Document documentDetailProduct) {
        return documentDetailProduct.select("#button-cart").first() == null;
    }

    @Override
    protected Optional<BigDecimal> parseProductPriceForPackage(Document documentDetailProduct) {
        return ofNullable(documentDetailProduct.select("#product > div.text > div > div.left_side > span.main").first())
                .map(Element::text)
                .map(text -> StringUtils.removeStart(text, "Nová cena: "))
                .map(text -> StringUtils.removeEnd(text, "€"))
                .filter(StringUtils::isNotBlank)
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
