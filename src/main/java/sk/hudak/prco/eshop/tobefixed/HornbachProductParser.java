package sk.hudak.prco.eshop.tobefixed;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.TextNode;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.api.ProductAction;
import sk.hudak.prco.builder.SearchUrlBuilder;
import sk.hudak.prco.exception.PrcoRuntimeException;
import sk.hudak.prco.parser.UnitParser;
import sk.hudak.prco.parser.WatchDogParser;
import sk.hudak.prco.parser.impl.JSoupProductParser;
import sk.hudak.prco.utils.UserAgentDataHolder;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.util.Collections.emptyList;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static org.apache.http.HttpHeaders.USER_AGENT;
import static sk.hudak.prco.api.EshopUuid.HORNBACH;
import static sk.hudak.prco.utils.ConvertUtils.convertToBigDecimal;

@Slf4j
//@Component
public class HornbachProductParser extends JSoupProductParser implements WatchDogParser {

    public HornbachProductParser(UnitParser unitParser, UserAgentDataHolder userAgentDataHolder, SearchUrlBuilder searchUrlBuilder) {
        super(unitParser, userAgentDataHolder, searchUrlBuilder);
    }

    @Override
    public EshopUuid getEshopUuid() {
        return HORNBACH;
    }

    @Override
    protected int getTimeout() {
        return TIMEOUT_10_SECOND;
    }

    @Override
    protected Document retrieveDocument(String productUrl) {
        BasicCookieStore cookieStore = new BasicCookieStore();
        // 746 -> Kosice predajna
        BasicClientCookie cookie = new BasicClientCookie("hbMarketCookie", "746");
        cookie.setDomain("www.hornbach.sk");
        cookie.setPath("/");
        cookieStore.addCookie(cookie);

        cookieStore.addCookie(new BasicClientCookie(USER_AGENT, getUserAgent()));

        HttpClient httpclient = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).build();

        try {
            return Jsoup.parse(httpclient.execute(new HttpGet(productUrl), new BasicResponseHandler()));

        } catch (Exception e) {
            throw new PrcoRuntimeException("error while downloading/parsing content for url " + productUrl, e);
        }
    }

    @Override
    protected Optional<String> parseProductNameFromDetail(Document documentDetailProduct) {
        Elements select = documentDetailProduct.select("#article-details > h1");
        if (select.isEmpty()) {
            return empty();
        }
        Element first = select.first();
        String text = first.text();
        return Optional.of(text);
    }

    @Override
    protected boolean isProductUnavailable(Document documentDetailProduct) {
        try {
            String jsonString = parseJson(documentDetailProduct);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode actualObj = mapper.readTree(jsonString);
            ArrayNode jsonNode1 = (ArrayNode) actualObj.get("offers");
            return jsonNode1.size() == 0;

        } catch (Exception e) {
            throw new PrcoRuntimeException("error while parsing availability for product url: " + documentDetailProduct.location(), e);
        }
    }

    @Override
    protected Optional<BigDecimal> parseProductPriceForPackage(Document documentDetailProduct) {
        try {
            String jsonString = parseJson(documentDetailProduct);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode actualObj = mapper.readTree(jsonString);
            ArrayNode jsonNode1 = (ArrayNode) actualObj.get("offers");
            JsonNode offerNode = jsonNode1.get(0);
            TextNode priceNode = (TextNode) offerNode.get("price");
            String price = priceNode.textValue();
            return ofNullable(convertToBigDecimal(price));

        } catch (Exception e) {
            log.error("error while parsing price", e);
            return empty();
        }
    }

    private String parseJson(Document document) {
        try {
            return document.select("script[type=application/ld+json]").get(0).childNode(0).toString();

        } catch (Exception e) {
            throw new PrcoRuntimeException("json element not found for url " + document.location(), e);
        }
    }

    @Override
    protected int parseCountOfPages(Document documentList) {
        // TODO impl
        return 0;
    }

    @Override
    protected List<String> parsePageForProductUrls(Document documentList, int pageNumber) {
        // TODO impl
        return emptyList();
    }

    @Override
    protected Optional<ProductAction> parseProductAction(Document documentDetailProduct) {
        // TODO impl
        return empty();
    }

    @Override
    protected Optional<Date> parseProductActionValidity(Document documentDetailProduct) {
        // TODO impl
        return empty();
    }

    @Override
    protected Optional<String> parseProductPictureURL(Document documentDetailProduct) {
        try {
            String jsonString = parseJson(documentDetailProduct);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode actualObj = mapper.readTree(jsonString);
            ArrayNode image = (ArrayNode) actualObj.get("image");
            if (image.size() == 0) {
                return empty();
            }
            return of(image.get(0).get("url").textValue());

        } catch (Exception e) {
            log.error("error while product picture url", e);
            return empty();
        }
    }
}
