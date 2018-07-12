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
import sk.hudak.prco.parser.UnitParser;
import sk.hudak.prco.parser.impl.JSoupProductParser;
import sk.hudak.prco.utils.UserAgentDataHolder;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static sk.hudak.prco.utils.ConvertUtils.convertToBigDecimal;
import static sk.hudak.prco.utils.JsoupUtils.existElement;
import static sk.hudak.prco.utils.JsoupUtils.getTextFromFirstElementByClass;
import static sk.hudak.prco.utils.JsoupUtils.notExistElement;

@Slf4j
@Component
public class FeedoProductParser extends JSoupProductParser {

    @Autowired
    public FeedoProductParser(UnitParser unitParser, UserAgentDataHolder userAgentDataHolder) {
        super(unitParser, userAgentDataHolder);
    }

    @Override
    public EshopUuid getEshopUuid() {
        return EshopUuid.FEEDO;
    }

    @Override
    protected int getTimeout() {
        // koli pomalym odozvam davam na 15 sekund
        return 15000;
    }

    @Override
    protected boolean isProductUnavailable(Document documentDetailProduct) {
        return notExistElement(documentDetailProduct, "button[class=btn btn-danger btn-large cart]");
    }

    @Override
    protected Optional<String> parseProductNameFromDetail(Document documentDetailProduct) {
        return getTextFromFirstElementByClass(documentDetailProduct, "product-detail-heading hidden-xs hidden-sm");
    }

    @Override
    protected Optional<String> parseProductNameFromList(Document documentList) {
        return parseProductNameFromDetail(documentList);
    }

    @Override
    protected Optional<BigDecimal> parseProductPriceForPackage(Document documentDetailProduct) {
        // skusim -> premim cena
        Elements select = documentDetailProduct.select("div[class=price price-premium] span");
        if (select.isEmpty()) {
            // ak sa nenajde tak skusim -> akcna cena
            select = documentDetailProduct.select("div[class=price price-discount] span");
            if (select.isEmpty()) {
                // ak sa nenajde tak skusim -> normalna cena
                select = documentDetailProduct.select("div[class=price price-base] span");
                if (select.isEmpty()) {
                    return Optional.empty();
                }
            }
        }

        String html = select.get(0).html();
        if (StringUtils.isBlank(html)) {
            return Optional.empty();
        }
        int endIndex = html.indexOf("&nbsp;");
        if (-1 == endIndex) {
            return Optional.empty();
        }
        String cenaZaBalenie = html.substring(0, endIndex);
        return Optional.of(convertToBigDecimal(cenaZaBalenie));
    }

    @Override
    protected int parseCountOfPages(Document documentList) {
        Element select = documentList.select("div.product-list-pagination").first();
        if (select == null) {
            return 1;
        }
        Elements lis = select.children().first().children().first().children().first().select("li");
        return lis.size() - 2;
    }

    @Override
    protected List<String> parsePageForProductUrls(Document documentList, int pageNumber) {
        return documentList.select("article[class=box box-product]").stream()
                .map(t -> t.children().first().children().first().attr("href"))
                .collect(Collectors.toList());
    }

    @Override
    protected Optional<ProductAction> parseProductAction(Document documentDetailProduct) {
        // premium cena
        if (existElement(documentDetailProduct, "div[class=price price-premium]")) {
            return Optional.of(ProductAction.IN_ACTION);
        }
        // akcna cena
        if (existElement(documentDetailProduct, "div[class=price price-discount]")) {
            return Optional.of(ProductAction.IN_ACTION);
        }
        return Optional.of(ProductAction.NON_ACTION);
    }

    @Override
    protected Optional<Date> parseProductActionValidity(Document documentDetailProduct) {
        // feedo nepodporuje
        return Optional.empty();
    }

    @Override
    protected Optional<String> parseProductPictureURL(Document documentDetailProduct) {
        //TODO parsovanie obrazkov nefunguje(musim pridat testy na komplet to co urcite funguje..., vsetky eshopy v jednom vlaklne postupne )
        Elements select = documentDetailProduct.select("div[class=box-image]");
        if (select.isEmpty()) {
            return Optional.empty();
        }
        Element child = select.get(0).child(0);
        String href = child.attr("href");
        return Optional.ofNullable(href);
    }
}
