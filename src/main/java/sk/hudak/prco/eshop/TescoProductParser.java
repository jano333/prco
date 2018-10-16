package sk.hudak.prco.eshop;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.api.ProductAction;
import sk.hudak.prco.exception.PrcoRuntimeException;
import sk.hudak.prco.parser.UnitParser;
import sk.hudak.prco.parser.impl.JSoupProductParser;
import sk.hudak.prco.utils.UserAgentDataHolder;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static sk.hudak.prco.utils.ConvertUtils.convertToBigDecimal;
import static sk.hudak.prco.utils.JsoupUtils.getTextFromFirstElementByClass;
import static sk.hudak.prco.utils.JsoupUtils.notExistElement;

/**
 * Created by jan.hudak on 9/29/2017.
 */
@Component
public class TescoProductParser extends JSoupProductParser {

    private static final String DATE_FORMAT_HH_MM_YYYY = "dd.MM.yyyy";

    @Autowired
    public TescoProductParser(UnitParser unitParser, UserAgentDataHolder userAgentDataHolder) {
        super(unitParser, userAgentDataHolder);
    }

    @Override
    public EshopUuid getEshopUuid() {
        return EshopUuid.TESCO;
    }

    @Override
    protected int parseCountOfPages(Document documentList) {
        Element navEl = documentList.select("nav.pagination--page-selector-wrapper").first();
        if (navEl == null) {
            return 1;
        }
        Element ulEl = navEl.child(0);
        Elements liEls = ulEl.children();
        int liElCount = liEls.size();
        // prvy a posledny vynechavam
        return liElCount - 2;
    }

    @Override
    protected List<String> parsePageForProductUrls(Document documentList, int pageNumber) {
        Elements elementsByClass = documentList.getElementsByClass("product-list grid");
        if (elementsByClass.isEmpty()) {
            return Collections.emptyList();
        }
        Element ulElement = elementsByClass.get(0);
        Elements liElements = ulElement.children();
        if (liElements.isEmpty()) {
            return Collections.emptyList();
        }
        List<String> resultUrls = new ArrayList<>(liElements.size());
        // zoznam najdenych produktov
        for (Element liElement : liElements) {
            Element divTag = liElement.select("div > div > div > div").first();
            Element aTag = divTag.child(0);
            String endPartUrl = aTag.attr("href");
            resultUrls.add(getEshopUuid().getProductStartUrl() + endPartUrl);
        }
        return resultUrls;
    }

    @Override
    protected boolean isProductUnavailable(Document documentDetailProduct) {
        //TODO https://potravinydomov.itesco.sk/groceries/sk-SK/products/2002120131941
        return notExistElement(documentDetailProduct, "button[class=button small add-control button-secondary]");
    }

    @Override
    protected Optional<String> parseProductNameFromDetail(Document documentDetailProduct) {
        return getTextFromFirstElementByClass(documentDetailProduct, "product-details-tile__title");
    }

    @Override
    protected Optional<BigDecimal> parseProductPriceForPackage(Document documentDetailProduct) {
        Elements elements = documentDetailProduct.select("div[class=price-per-sellable-unit price-per-sellable-unit--price price-per-sellable-unit--price-per-item] div span span[class=value]");
        return Optional.ofNullable(convertToBigDecimal(elements.get(0).text()));
    }

    @Override
    protected Optional<ProductAction> parseProductAction(Document documentDetailProduct) {
        Elements select = documentDetailProduct.select("div.icon-offer-flash-group > div.yellow-square > span");
        if (select.isEmpty()) {
            return Optional.of(ProductAction.NON_ACTION);
        }
        return Optional.of("Akcia".equals(select.get(0).text()) ? ProductAction.IN_ACTION : ProductAction.NON_ACTION);
    }

    @Override
    protected Optional<Date> parseProductActionValidity(Document documentDetailProduct) {
        StringBuilder htmlTree = new StringBuilder()
                .append("ul[class=product-promotions]").append(" ")
                .append("li[class=product-promotion]").append(" ")
                .append("div").append(" ")
                .append("a").append(" ")
                .append("div[class=list-item-content promo-content-small]").append(" ")
                .append("span[class=dates]");

        StringBuilder sb = new StringBuilder(documentDetailProduct.select(htmlTree.toString()).get(0).text());
        sb = sb.delete(0, "Cena je platná pri dodaní do ".length());
        return Optional.ofNullable(parseDate(sb.toString(), DATE_FORMAT_HH_MM_YYYY));
    }

    @Override
    protected Optional<String> parseProductPictureURL(Document documentDetailProduct) {
        Element element = documentDetailProduct.select("img[class=product-image product-image-visible]").first();
        return Optional.ofNullable(element)
                .map(element1 -> element1.attr("data-src"));
    }

    //FIXME je to vo viacerych miestach urobit na to UTIL class pre datumy
    private Date parseDate(String strDate, String format) {
        if (strDate == null) {
            return null;
        }
        try {
            //FIXME
//            return DateTimeFormatter.ofPattern(format).parse(strDate).;
            return new SimpleDateFormat(format).parse(strDate);

        } catch (ParseException e) {
            throw new PrcoRuntimeException("error while parsing string to date", e);
        }
    }
}
