package sk.hudak.prco.eshop;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.api.ProductAction;
import sk.hudak.prco.dto.UnitTypeValueCount;
import sk.hudak.prco.parser.UnitParser;
import sk.hudak.prco.parser.impl.JSoupProductParser;
import sk.hudak.prco.utils.UserAgentDataHolder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Slf4j
//@Component
public class DrMaxProductParser extends JSoupProductParser {

    @Autowired
    public DrMaxProductParser(UnitParser unitParser, UserAgentDataHolder userAgentDataHolder) {
        super(unitParser, userAgentDataHolder);
    }

    @Override
    public EshopUuid getEshopUuid() {
//        return EshopUuid.DR_MAX;
        return null;
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
    protected Optional<String> parseProductNameFromList(Document documentList) {
        return parseProductNameFromDetail(documentList);
    }

    @Override
    protected Optional<String> parseProductNameFromDetail(Document documentDetailProduct) {
        Elements select = documentDetailProduct.select("#product-detail > div.row > div > div > div.col.data > div.redesign_desktop > div.redesign-product-detail-title.bold");
        Element first = select.first().children().first();
        String text = first.text();
        return Optional.of(text);
    }

    @Override
    protected Optional<UnitTypeValueCount> parseUnitValueCount(String productUrl, String productName) {

        //TODO pocet kusov je v osobitnej metode..., skusist zavolat super a ak nenajde tak tu ...
        return Optional.empty();
    }

    @Override
    protected boolean isProductUnavailable(Document documentDetailProduct) {
        return false;
    }

    @Override
    protected Optional<BigDecimal> parseProductPriceForPackage(Document documentDetailProduct) {
        return Optional.empty();
    }

    @Override
    protected Optional<ProductAction> parseProductAction(Document documentDetailProduct) {
        return Optional.empty();
    }

    @Override
    protected Optional<Date> parseProductActionValidity(Document documentDetailProduct) {
        return Optional.empty();
    }

    @Override
    protected Optional<String> parseProductPictureURL(Document documentDetailProduct) {
        return Optional.empty();
    }


}
