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
import sk.hudak.prco.dto.UnitTypeValueCount;
import sk.hudak.prco.parser.UnitParser;
import sk.hudak.prco.parser.impl.JSoupProductParser;
import sk.hudak.prco.utils.ConvertUtils;
import sk.hudak.prco.utils.UserAgentDataHolder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class DrMaxProductParser extends JSoupProductParser {

    @Autowired
    public DrMaxProductParser(UnitParser unitParser, UserAgentDataHolder userAgentDataHolder) {
        super(unitParser, userAgentDataHolder);
    }

    @Override
    public EshopUuid getEshopUuid() {
        return EshopUuid.DR_MAX;
    }

    @Override
    protected int getTimeout() {
        // koli pomalym odozvam davam na 15 sekund
        return 15000;
    }

    @Override
    protected int parseCountOfPages(Document documentList) {
        Element select = documentList.select("h2[class=title-subcategory]").first();
        if (select == null) {
            return 1;
        }
        String text = select.text();
        text = StringUtils.remove(text, "Vyhľadávanie (");
        text = StringUtils.remove(text, ")");
        int allProductsSize = Integer.valueOf(text);
        int maxCountOfNewPages = getEshopUuid().getMaxCountOfProductOnPage();

        int cout = allProductsSize / maxCountOfNewPages;
        int hh = allProductsSize % maxCountOfNewPages;

        if (hh > 0) {
            return cout + 1;
        } else {
            return cout;
        }
    }

    @Override
    protected List<String> parsePageForProductUrls(Document documentList, int pageNumber) {
        List<String> urls = new ArrayList<>();
        for (Element child : documentList.select("#main > div.productList.productListTypeRow.row").first().children()) {
            String attr = child.children().first().attr("data-href");
            if (attr == null) {
                continue;
            }
            urls.add(getEshopUuid().getProductStartUrl() + attr);
        }
        return urls;
    }

    @Override
    protected Optional<String> parseProductNameFromDetail(Document documentDetailProduct) {
        Elements select = documentDetailProduct.select("#product-detail > div.row > div > div > div.col.data > div.redesign_desktop > div.redesign-product-detail-title.bold");
        Element first = select.first().children().first();
        String text = first.text();
        return Optional.of(text);
    }

    @Override
    protected Optional<UnitTypeValueCount> parseUnitValueCount(Document document, String productName) {
        Elements select = document.select("div.redesign-product-detail-slogan");
        if (select.isEmpty()) {
            return Optional.empty();
        }
        String text = select.get(0).child(0).text();
        return unitParser.parseUnitTypeValueCount(text);
    }

    @Override
    protected boolean isProductUnavailable(Document documentDetailProduct) {
        return documentDetailProduct.select("button[class=redesign-button addToCartBtn btn btn-big btn-pink ucase]").isEmpty();
    }

    @Override
    protected Optional<BigDecimal> parseProductPriceForPackage(Document documentDetailProduct) {
        Elements select = documentDetailProduct.select("strong[itemprop=price]");
        if (select.isEmpty()) {
            return Optional.empty();
        }
        String text = select.get(0).text();
        BigDecimal value = ConvertUtils.convertToBigDecimal(text);
        return Optional.ofNullable(value);
    }

    @Override
    protected Optional<ProductAction> parseProductAction(Document documentDetailProduct) {
        //TODO impl
        return Optional.empty();
    }

    @Override
    protected Optional<Date> parseProductActionValidity(Document documentDetailProduct) {
        //TODO impl
        return Optional.empty();
    }

    @Override
    protected Optional<String> parseProductPictureURL(Document documentDetailProduct) {
        Elements select = documentDetailProduct.select("#productImg > img.image");
        Element element = select.get(0);
        String src = element.attr("src");
        String s = getEshopUuid().getProductStartUrl() + src;
        return Optional.of(s);
    }


}
