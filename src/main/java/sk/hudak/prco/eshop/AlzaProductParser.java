package sk.hudak.prco.eshop;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.api.ProductAction;
import sk.hudak.prco.parser.UnitParser;
import sk.hudak.prco.parser.impl.JSoupProductParser;
import sk.hudak.prco.utils.UserAgentDataHolder;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static sk.hudak.prco.api.ProductAction.IN_ACTION;
import static sk.hudak.prco.api.ProductAction.NON_ACTION;
import static sk.hudak.prco.utils.JsoupUtils.existElement;
import static sk.hudak.prco.utils.JsoupUtils.notExistElement;

//import static sk.hudak.prco.api.EshopUuid.ALZA;

public class AlzaProductParser extends JSoupProductParser {


    public AlzaProductParser(UnitParser unitParser, UserAgentDataHolder userAgentDataHolder) {
        super(unitParser, userAgentDataHolder);
    }

    @Override
    public EshopUuid getEshopUuid() {
//        return ALZA;
        return null;
    }

    @Override
    protected boolean isProductUnavailable(Document documentDetailProduct) {
        return notExistElement(documentDetailProduct, "a[class=cart-insert]")
                && notExistElement(documentDetailProduct, "a[class=btnx normal green buy]");
    }

    @Override
    protected Optional<BigDecimal> parseProductPriceForPackage(Document documentDetailProduct) {
        //niekte je to tak
        Elements elements = documentDetailProduct.select("span[class=price_withVat]");
        if (elements.isEmpty()) {
            // inde tak
            elements = documentDetailProduct.select("span[class=bigPrice price_withVat]");
        }
        if (elements.isEmpty()) {
            //TODO tu by mala byt vynimka ked bduem mat ze je nedostupne
            return null;
        }
        String text = elements.get(0).text();
        if (StringUtils.isBlank(text)) {
            return Optional.empty();
        }
        StringBuffer sb = new StringBuffer(text);
        sb = sb.deleteCharAt(0);
        return Optional.of(new BigDecimal(sb.toString().replace(",", ".")));
    }

    @Override
    protected Optional<String> parseProductNameFromList(Document documentList) {
        return parseProductNameFromDetail(documentList);
    }

    @Override
    protected Optional<String> parseProductNameFromDetail(Document documentDetailProduct) {
        //TODO
        return Optional.empty();
    }

    @Override
    protected int parseCountOfPages(Document documentList) {
        //TODO
        return 0;
    }

    @Override
    protected List<String> parsePageForProductUrls(Document documentList, int pageNumber) {
        //TODO
        return null;
    }

    @Override
    protected Optional<ProductAction> parseProductAction(Document documentDetailProduct) {
        //TODO dorobit parsovanie zlavy v percentach
        return Optional.of(
                (existElement(documentDetailProduct, "span[class=icon-percentage icon]")
                        || existElement(documentDetailProduct, "span[class=quantityPercentDiscount icon-percentage icon]")
                ) ? IN_ACTION : NON_ACTION);
    }

    @Override
    protected Optional<Date> parseProductActionValidity(Document documentDetailProduct) {
        //TODO
        return Optional.empty();
    }

    @Override
    protected Optional<String> parseProductPictureURL(Document documentDetailProduct) {
        //TODO
        return Optional.empty();
    }


}
