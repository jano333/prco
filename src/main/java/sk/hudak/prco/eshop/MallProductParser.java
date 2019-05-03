package sk.hudak.prco.eshop;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.api.ProductAction;
import sk.hudak.prco.builder.SearchUrlBuilder;
import sk.hudak.prco.exception.PrcoRuntimeException;
import sk.hudak.prco.parser.UnitParser;
import sk.hudak.prco.parser.impl.JSoupProductParser;
import sk.hudak.prco.utils.UserAgentDataHolder;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static sk.hudak.prco.api.EshopUuid.MALL;
import static sk.hudak.prco.utils.ConvertUtils.convertToBigDecimal;
import static sk.hudak.prco.utils.JsoupUtils.notExistElement;

@Slf4j
@Component
public class MallProductParser extends JSoupProductParser {

    private static final int MAX_COUNT_OF_PRODUCT_PRE_PAGE = 36;

    @Autowired
    public MallProductParser(UnitParser unitParser, UserAgentDataHolder userAgentDataHolder, SearchUrlBuilder searchUrlBuilder) {
        super(unitParser, userAgentDataHolder, searchUrlBuilder);
    }

    @Override
    public EshopUuid getEshopUuid() {
        return MALL;
    }

    @Override
    protected int getTimeout() {
        return TIMEOUT_15_SECOND;
    }

    @Override
    protected int parseCountOfPages(Document documentList) {
        StringBuilder htmlTree = new StringBuilder()
                .append("span[data-sel=productCountNumber]");
        String pocetProduktov = documentList.select(htmlTree.toString()).first().text();
        double pocet = Double.parseDouble(pocetProduktov);
        double pocetStranNezaokruhleny = pocet / (double) MAX_COUNT_OF_PRODUCT_PRE_PAGE;
        BigDecimal bigDecimal = BigDecimal.valueOf(pocetStranNezaokruhleny);
        bigDecimal = bigDecimal.setScale(0, BigDecimal.ROUND_HALF_UP);
        int pocetStran = bigDecimal.intValue();
        return pocetStran;
    }

    @Override
    protected List<String> parsePageForProductUrls(Document documentList, int pageNumber) {
        List<String> result = new ArrayList<>(MAX_COUNT_OF_PRODUCT_PRE_PAGE);
        documentList.select("h3[class=lst-product-item-title]")
                .forEach(element -> result.add(getEshopUuid().getProductStartUrl() + element.child(0).attr("href")));
        return result;
    }

    @Override
    protected boolean isProductUnavailable(Document documentDetailProduct) {
        return notExistElement(documentDetailProduct, "button[id=add-to-cart]");
    }

    @Override
    protected Optional<String> parseProductNameFromDetail(Document documentDetailProduct) {
        Element element = documentDetailProduct.select("section[class=pro-column] h1").first();
        if (element == null) {
            return Optional.empty();
        }
        return Optional.of(element.text());
    }

    @Override
    protected Optional<BigDecimal> parseProductPriceForPackage(Document documentDetailProduct) {
        Elements elements2 = documentDetailProduct.select("b[class=pro-price con-emphasize font-primary--bold mr-5]");
        if (elements2.isEmpty()) {
            return Optional.empty();
        }
        String cenaZaBalenie = elements2.get(0).text().replace("€", "").trim();
        return Optional.of(convertToBigDecimal(cenaZaBalenie));
    }

    @Override
    protected Optional<ProductAction> parseProductAction(Document documentDetailProduct) {
        Element select = documentDetailProduct.select("p[class=m-0 pro-stickers]").first();
        if (select == null) {
            return Optional.of(ProductAction.NON_ACTION);
        }
        Elements children = select.children();
        for (Element child : children) {
            String aClass = child.attr("class");
            if ("label label--action".equals(aClass)) {
                return Optional.of(ProductAction.IN_ACTION);
            }
        }
        return Optional.of(ProductAction.NON_ACTION);
    }


    @Override
    protected Optional<Date> parseProductActionValidity(Document documentDetailProduct) {
        Elements select = documentDetailProduct.select("p[class=mb-5]");
        if (select.isEmpty()) {
            return Optional.empty();
        }
        Element element = select.get(0);
        StringBuilder sb = new StringBuilder(element.html());
        sb.delete(0, sb.indexOf("do") + 3);
        sb.delete(10, sb.length());
        return Optional.of(parseDate(sb.toString(), DATE_FORMAT_HH_MM_YYYY));
    }

    @Override
    protected Optional<String> parseProductPictureURL(Document documentDetailProduct) {
        Elements img = documentDetailProduct.select("img[class=gall-slide-img]");
        if (img.size() == 0) {
            return Optional.empty();
        }
        Element element1 = img.get(0);
        String src1 = element1.attr("src");
        return Optional.of(src1);
    }

    private Date parseDate(String strDate, String format) {
        if (strDate == null) {
            return null;
        }
        try {
            return new SimpleDateFormat(format).parse(strDate);

        } catch (ParseException e) {
            throw new PrcoRuntimeException("error while parsing string to date", e);
        }
    }
}
