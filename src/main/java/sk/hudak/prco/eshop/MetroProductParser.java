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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static sk.hudak.prco.api.ProductAction.IN_ACTION;
import static sk.hudak.prco.api.ProductAction.NON_ACTION;
import static sk.hudak.prco.utils.ConvertUtils.convertToBigDecimal;
import static sk.hudak.prco.utils.JsoupUtils.notExistElement;

@Component
public class MetroProductParser extends JSoupProductParser {

    private static final int PAGING = 15;

    @Autowired
    public MetroProductParser(UnitParser unitParser, UserAgentDataHolder userAgentDataHolder) {
        super(unitParser, userAgentDataHolder);
    }

    @Override
    public EshopUuid getEshopUuid() {
        return EshopUuid.METRO;
    }

    @Override
    protected Map<String, String> getCookie() {
        // 21 su Kosice pobocka
        return Collections.singletonMap("storeId", "21");
    }

    @Override
    protected int parseCountOfPages(Document documentList) {
        Elements select = documentList.select("div[class=paging-show pull-left color-gray-dark] strong[class=color-gray-base]");
        if (select.size() != 2) {
            return 1;
        }
        //2 v poradi
        Element element = select.get(1);
        String count = element.text();
        BigDecimal result = new BigDecimal(count).divide(new BigDecimal(PAGING), RoundingMode.HALF_UP);
        return result.intValue();
    }

    @Override
    protected List<String> parsePageForProductUrls(Document documentList, int pageNumber) {
        Elements select = documentList.select("div.product.msg-parent");
        List<String> result = new ArrayList<>(select.size());
        for (Element element : select) {
            Element first = element.children().first().children().first().children().first().children().first();
            result.add(first.attr("href"));
        }
        return result;
    }

    @Override
    protected boolean isProductUnavailable(Document documentDetailProduct) {
        return notExistElement(documentDetailProduct, "div.x-row.product-stock-detail");
    }

    @Override
    protected Optional<String> parseProductNameFromDetail(Document documentDetailProduct) {
        Element select = documentDetailProduct.select("div.product-detail.clearfix > h1").first();
        if (select == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(select.text());
    }

    @Override
    protected Optional<BigDecimal> parseProductPriceForPackage(Document documentDetailProduct) {
        String text = documentDetailProduct.select("tr.price-package > td:nth-child(4)").text();
        text = text.substring(0, text.length() - 2);
        return Optional.of(convertToBigDecimal(text));
    }

    @Override
    protected Optional<ProductAction> parseProductAction(Document documentDetailProduct) {
        Elements select = documentDetailProduct.select("span[class=mic mic-action-sk is-32]");
        return Optional.of(select.isEmpty()
                ? NON_ACTION
                : IN_ACTION);
    }

    @Override
    protected Optional<Date> parseProductActionValidity(Document documentDetailProduct) {
        //TODO impl action validity
        return Optional.empty();
    }

    @Override
    protected Optional<String> parseProductPictureURL(Document documentDetailProduct) {
        //TODO impl picture url
        return Optional.empty();
    }
}
