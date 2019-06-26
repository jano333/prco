package sk.hudak.prco.eshop;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
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
import sk.hudak.prco.utils.JsoupUtils;
import sk.hudak.prco.utils.UserAgentDataHolder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;

import static sk.hudak.prco.api.EshopUuid.MALL;
import static sk.hudak.prco.utils.ConvertUtils.convertToBigDecimal;
import static sk.hudak.prco.utils.JsoupUtils.notExistElement;
import static sk.hudak.prco.utils.PatternUtils.NUMBER_AT_LEAST_ONE;
import static sk.hudak.prco.utils.PatternUtils.createMatcher;

@Slf4j
@Component
public class MallProductParser extends JSoupProductParser {

    private static final int MAX_COUNT_OF_PRODUCT_PRE_PAGE = 48;

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
        Optional<String> scriptContent = documentList.getElementsByTag("script").stream()
                .map(Element::html)
                .filter(content -> content.contains("var CONFIGURATION"))
                .findFirst();

        if (!scriptContent.isPresent()) {
            return 1;
        }
        // "total":107,"products"
        Matcher matcher = createMatcher(scriptContent.get(), "\"total\":", NUMBER_AT_LEAST_ONE, ",\"products\"");
        if (matcher.find()) {
            return new BigDecimal(Optional.ofNullable(matcher.group(2))
                    .filter(NumberUtils::isParsable)
                    .map(Integer::valueOf)
                    .get())
                    .divide(new BigDecimal(MAX_COUNT_OF_PRODUCT_PRE_PAGE), RoundingMode.CEILING)
                    .intValue();
        }
        return 1;
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
        Element img = documentDetailProduct.select("img[class=gall-slide-img]").first();
        if (img == null) {
            img = documentDetailProduct.select("img.gallery-magnifier__normal").first();
        }
        return Optional.ofNullable(img)
                .map(JsoupUtils::srcAttribute);
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
