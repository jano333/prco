package sk.hudak.prco.eshop;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static sk.hudak.prco.utils.ConvertUtils.convertToBigDecimal;
import static sk.hudak.prco.utils.JsoupUtils.getFirstElementByClass;

@Component
public class PilulkaProductParser extends JSoupProductParser {

    @Autowired
    public PilulkaProductParser(UnitParser unitParser, UserAgentDataHolder userAgentDataHolder) {
        super(unitParser, userAgentDataHolder);
    }

    @Override
    public EshopUuid getEshopUuid() {
        return EshopUuid.PILULKA;
    }

    @Override
    protected int parseCountOfPages(Document documentList) {
        Optional<Element> firstElementByClass = getFirstElementByClass(documentList, "pager");
        if (!firstElementByClass.isPresent()) {
            return 1;
        }
        // hodnotu z posledneho a tagu pod tagom pager
        return Integer.valueOf(firstElementByClass.get().getElementsByTag("a").last().text());
    }

    @Override
    protected List<String> parsePageForProductUrls(Document documentList, int pageNumber) {
        Elements allElements = documentList.select("div[class=product-list]").first().children().first().children();
        List<String> resultUrls = new ArrayList<>();
        for (Element liEls : allElements) {
            Element a = liEls.select("div > h3 > a").first();
            resultUrls.add(getEshopUuid().getProductStartUrl() + a.attr("href"));
        }
        return resultUrls;
    }

    @Override
    protected Optional<String> parseProductNameFromDetail(Document documentDetailProduct) {
        Element first = documentDetailProduct.select("div[id=product-info] > form > div > h1 > span[itemprop=name]").first();
        if (first == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(first.text());
    }

    @Override
    protected boolean isProductUnavailable(Document documentDetailProduct) {
        return null == documentDetailProduct.select("input[value=Kúpiť]").first();
    }

    @Override
    protected Optional<BigDecimal> parseProductPriceForPackage(Document documentDetailProduct) {
        Element first = documentDetailProduct.select("strong[id=priceNew]").first();
        if (first == null) {
            return Optional.empty();
        }
        String substring = first.text().substring(0, first.text().length() - 2);
        return Optional.of(convertToBigDecimal(substring));
    }

    @Override
    protected Optional<String> parseProductPictureURL(Document documentDetailProduct) {
        Element first = documentDetailProduct.select("#pr-img-carousel > ul > li > a > img").first();
        if (first == null) {
            return Optional.empty();
        }
        return Optional.of(getEshopUuid().getProductStartUrl() + "/" + first.attr("src"));
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
