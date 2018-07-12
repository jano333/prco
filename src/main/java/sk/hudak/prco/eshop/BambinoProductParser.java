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
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static sk.hudak.prco.api.EshopUuid.BAMBINO;
import static sk.hudak.prco.api.ProductAction.IN_ACTION;
import static sk.hudak.prco.api.ProductAction.NON_ACTION;
import static sk.hudak.prco.utils.ConvertUtils.convertToBigDecimal;
import static sk.hudak.prco.utils.JsoupUtils.existElement;
import static sk.hudak.prco.utils.JsoupUtils.getTextFromFirstElementByClass;
import static sk.hudak.prco.utils.JsoupUtils.notExistElement;

@Component
public class BambinoProductParser extends JSoupProductParser {

    private static final int PAGING = 24;

    @Autowired
    public BambinoProductParser(UnitParser unitParser, UserAgentDataHolder userAgentDataHolder) {
        super(unitParser, userAgentDataHolder);
    }

    @Override
    public EshopUuid getEshopUuid() {
        return BAMBINO;
    }

    @Override
    protected int parseCountOfPages(Document documentList) {
        Optional<String> text = getTextFromFirstElementByClass(documentList, "o-header-section__info");
        if (!text.isPresent()) {
            return 1;
        }
        String tmp = text.get();
        String count = tmp.replace("Celkom nájdených produktov: ", "").trim();
        BigDecimal result = new BigDecimal(count).divide(new BigDecimal(PAGING), RoundingMode.HALF_UP);
        return result.intValue();
    }

    @Override
    protected List<String> parsePageForProductUrls(Document documentList, int pageNumber) {
        Elements allElements = documentList.select("a[class=o-product-box__link]");
        List<String> result = new ArrayList<>(allElements.size());
        for (Element element : allElements) {
            String href = element.attr("href");
            // specialny fix, odsranujem vsetko co je za otaznikom lebo tam dava neaky token:
            // https://www.bambino.sk/jednorazove-plienky/premium-care-4-maxi-7-14kg-66-ks?searchToken=75bc6842-eecc-4269-a0fc-1e3af1c36c80
            int querySeparator = href.indexOf('?');
            if (querySeparator == -1) {
                result.add(href);
            } else {
                result.add(href.substring(0, querySeparator));
            }
        }
        return result;
    }

    @Override
    protected boolean isProductUnavailable(Document documentDetailProduct) {
        return notExistElement(documentDetailProduct, "span[class=o-add-to-cart__status]");
    }

    @Override
    protected Optional<String> parseProductNameFromList(Document documentList) {
        return parseProductNameFromDetail(documentList);
    }

    @Override
    protected Optional<String> parseProductNameFromDetail(Document documentDetailProduct) {
        Element first = documentDetailProduct.select("h1[class=o-product__name]").first();
        if (first == null) {
            return Optional.empty();
        }
        Elements children = first.children();
        StringBuilder sb = new StringBuilder();

        for (Element child : children) {
            String text = child.text();
            sb.append(text);
            sb.append(" ");
        }
        String result = sb.toString().trim();
        return Optional.ofNullable(result);
    }

    @Override
    protected Optional<BigDecimal> parseProductPriceForPackage(Document documentDetailProduct) {
        Element first = documentDetailProduct.select("b[class=m-quantity-price__price]").first();
        if (first == null) {
            return Optional.empty();
        }
        StringBuilder sb = new StringBuilder(first.text());
        // odmazem posledne 2 znaky
        sb = sb.deleteCharAt(sb.length() - 1);
        sb = sb.deleteCharAt(sb.length() - 1);
        String cenaZaBalenie = sb.toString();
        return Optional.of(convertToBigDecimal(cenaZaBalenie));
    }

    @Override
    protected Optional<ProductAction> parseProductAction(Document documentDetailProduct) {
        return existElement(documentDetailProduct, "span[class=m-badge m-badge--large bg-red o-product__labels__item]")
                ? Optional.of(IN_ACTION)
                : Optional.of(NON_ACTION);
    }

    @Override
    protected Optional<Date> parseProductActionValidity(Document documentDetailProduct) {
        // not supported for this eshop
        return Optional.empty();
    }

    @Override
    protected Optional<String> parseProductPictureURL(Document documentDetailProduct) {
        Element first = documentDetailProduct.select("img[itemprop=image]").first();
        if (first == null) {
            return Optional.empty();
        }
        String src = first.attr("src");
        return Optional.of(src);
    }
}
