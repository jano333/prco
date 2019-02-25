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

import static java.util.Optional.ofNullable;
import static sk.hudak.prco.api.EshopUuid.DROGERIA_VMD;

@Slf4j
@Component
public class DrogeriaVmdProductParser extends JSoupProductParser {

    private static final Integer ZERO = Integer.valueOf(0);

    @Autowired
    public DrogeriaVmdProductParser(UnitParser unitParser, UserAgentDataHolder userAgentDataHolder, SearchUrlBuilder searchUrlBuilder) {
        super(unitParser, userAgentDataHolder, searchUrlBuilder);
    }

    @Override
    public EshopUuid getEshopUuid() {
        return DROGERIA_VMD;
    }

    @Override
    protected int getTimeout() {
        // koli pomalym odozvam davam na 15 sekund
        return 15000;
    }

    @Override
    protected int parseCountOfPages(Document documentList) {
        return ofNullable(documentList.select("span.pages a").last())
                .map(Element::text)
                .filter(StringUtils::isNotBlank)
                .map(Integer::valueOf)
                .orElse(ZERO);
    }

    @Override
    protected List<String> parsePageForProductUrls(Document documentList, int pageNumber) {
        return documentList.select("div.produkt > div.inner > a.name").stream()
                .map(element -> element.attr("href"))
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
    }

    @Override
    protected boolean isProductUnavailable(Document documentDetailProduct) {
        return !ofNullable(documentDetailProduct.select("button[class='.btn koupit_detail_gb']")).isPresent();
    }

    @Override
    protected Optional<String> parseProductNameFromDetail(Document documentDetailProduct) {
        return ofNullable(documentDetailProduct.select("#content > div.rightSide > div:nth-child(2) > h1"))
                .map(Elements::text);
    }

    @Override
    protected Optional<String> parseProductPictureURL(Document documentDetailProduct) {
        return ofNullable(documentDetailProduct.select("#detail_image"))
                .map(elements -> elements.attr("src"));
    }

    @Override
    protected Optional<BigDecimal> parseProductPriceForPackage(Document documentDetailProduct) {
        return ofNullable(documentDetailProduct.select("#detail_cenas"))
                .map(Elements::text)
                .filter(StringUtils::isNotBlank)
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
