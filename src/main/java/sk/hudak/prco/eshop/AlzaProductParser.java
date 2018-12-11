package sk.hudak.prco.eshop;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.api.ProductAction;
import sk.hudak.prco.builder.SearchUrlBuilder;
import sk.hudak.prco.parser.UnitParser;
import sk.hudak.prco.parser.impl.JSoupProductParser;
import sk.hudak.prco.utils.UserAgentDataHolder;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static sk.hudak.prco.api.EshopUuid.ALZA;
import static sk.hudak.prco.api.ProductAction.IN_ACTION;
import static sk.hudak.prco.api.ProductAction.NON_ACTION;
import static sk.hudak.prco.utils.JsoupUtils.existElement;
import static sk.hudak.prco.utils.JsoupUtils.notExistElement;

@Slf4j
@Component
public class AlzaProductParser extends JSoupProductParser {

    private static final int SECOND_10 = 10000;

    public AlzaProductParser(UnitParser unitParser, UserAgentDataHolder userAgentDataHolder, SearchUrlBuilder searchUrlBuilder) {
        super(unitParser, userAgentDataHolder, searchUrlBuilder);
    }

    @Override
    protected int getTimeout() {
        // koli pomalym odozvam davam na 10 sekund
        return SECOND_10;
    }

    @Override
    public EshopUuid getEshopUuid() {
        return ALZA;
    }

    @Override
    protected int parseCountOfPages(Document documentList) {
        // zaujimaju aj tak maximalne 3 page viac nie...
        Element select = documentList.select("#pagertop").first();
        if (select == null) {
            return 1;
        }
        Elements children = select.children();
        // 2-hy predposledny
        Element element = children.get(children.size() - 3).child(0);
        return Integer.valueOf(element.text());
    }

    @Override
    protected List<String> parsePageForProductUrls(Document documentList, int pageNumber) {
        Elements select1 = documentList.select("a[class='name browsinglink']");
        List<String> urls = new ArrayList<>(select1.size());
        for (Element element : select1) {
            String href = getEshopUuid().getProductStartUrl() + element.attr("href");
            // from
            // https://www.alza.sk/maxi/pampers-active-baby-dry-vel-4-maxi-152-ks-mesacne-balenie-d4842708.htm?o=8
            // urobit
            // https://www.alza.sk/maxi/pampers-active-baby-dry-vel-4-maxi-152-ks-mesacne-balenie-d4842708.htm
            if (href.contains("?")) {
                href = href.substring(0, href.lastIndexOf('?'));
            }
            urls.add(href);
        }
        return urls;
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
        if(elements.isEmpty()){
            elements = documentDetailProduct.select("#prices > tbody > tr.pricenormal > td.c2 > span");
        }

        if (elements.isEmpty()) {
            return Optional.empty();
        }
        String text = elements.get(0).text();
        if (StringUtils.isBlank(text)) {
            return Optional.empty();
        }
        StringBuilder sb = new StringBuilder(text);
        sb = sb.deleteCharAt(0);
        return Optional.of(new BigDecimal(sb.toString().replace(",", ".")));
    }

    @Override
    protected Optional<String> parseProductNameFromDetail(Document documentDetailProduct) {
        Elements first = documentDetailProduct.select("script[type=application/ld+json]");
        if (first.size() < 2) {
            return Optional.empty();
        }
        Element element = first.get(1);
        List<Node> nodes = element.childNodes();
        DataNode node = (DataNode) nodes.get(0);
        String wholeDataJson = node.getWholeData();
        JsonNode actualObj;
        try {
            ObjectMapper mapper = new ObjectMapper();
            actualObj = mapper.readTree(wholeDataJson);
        } catch (IOException e) {
            log.error("error while parsing json for url " + documentDetailProduct.location(), e);
            return Optional.empty();
        }

        TextNode name = (TextNode) actualObj.get("name");
        return Optional.of(name.textValue());
    }


    @Override
    protected Optional<String> parseProductPictureURL(Document documentDetailProduct) {
        Elements select = documentDetailProduct.select("#imgMain");
        if (select.isEmpty()) {
            return Optional.empty();
        }
        Element element = select.get(0);
        String fullUrl = element.attr("data-src");
        return Optional.of(fullUrl);
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
}
