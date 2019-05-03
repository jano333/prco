package sk.hudak.prco.eshop;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.api.ProductAction;
import sk.hudak.prco.builder.SearchUrlBuilder;
import sk.hudak.prco.dto.UnitTypeValueCount;
import sk.hudak.prco.parser.UnitParser;
import sk.hudak.prco.parser.impl.JSoupProductParser;
import sk.hudak.prco.utils.ConvertUtils;
import sk.hudak.prco.utils.UserAgentDataHolder;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.empty;
import static java.util.Optional.ofNullable;
import static sk.hudak.prco.api.EshopUuid.MAMA_A_JA;

@Slf4j
//@Component
public class MamaAJaProductParser extends JSoupProductParser {

    public MamaAJaProductParser(UnitParser unitParser, UserAgentDataHolder userAgentDataHolder, @NotNull SearchUrlBuilder searchUrlBuilder) {
        super(unitParser, userAgentDataHolder, searchUrlBuilder);
    }

    @Override
    public EshopUuid getEshopUuid() {
        return MAMA_A_JA;
    }

    @Override
    protected int getTimeout() {
        return TIMEOUT_10_SECOND;
    }

    @Override
    protected int parseCountOfPages(Document documentList) {
        Integer countOfProducts = ofNullable(documentList.select("#category-header > div:nth-child(1) > h1").first())
                .map(element -> element.text())
                .filter(text -> text.contains("Vyhľadávanie ("))
                .map(text -> StringUtils.removeStart(text, "Vyhľadávanie ("))
                .map(text -> StringUtils.removeEnd(text, ")"))
                .map(text -> Integer.valueOf(text))
                .orElse(0);

        if (countOfProducts.intValue() == 0) {
            return 0;
        }
        int countOfPages = countOfProducts.intValue() / getEshopUuid().getMaxCountOfProductOnPage();

        if (countOfProducts.intValue() % getEshopUuid().getMaxCountOfProductOnPage() > 0) {
            countOfPages++;
        }
        return countOfPages;
    }

    @Override
    protected List<String> parsePageForProductUrls(Document documentList, int pageNumber) {
        List<String> urls = new ArrayList<>();
        documentList.select("#main > div.productList.row")
                .select("div[class='col'] div[class='product indent text-center']")
                .stream()
                .forEach(element ->
                        urls.add(getEshopUuid().getProductStartUrl() + element.attr("data-href")));
        return urls;
    }

    @Override
    protected Optional<String> parseProductNameFromDetail(Document documentDetailProduct) {
        return ofNullable(documentDetailProduct
                .select("#product-detail > div.row > div > div > div.col.data > div > div:nth-child(1) > h1")
                .first())
                .map(Element::text);
    }

    @Override
    protected Optional<UnitTypeValueCount> parseUnitValueCount(Document document, String productName) {
        Optional<String> text = ofNullable(document.select("div[class='slogan']").first())
                .map(Element::text);
        if (!text.isPresent()) {
            return empty();
        }
        return unitParser.parseUnitTypeValueCount(text.get());
    }


    @Override
    protected Optional<String> parseProductPictureURL(Document documentDetailProduct) {
        return ofNullable(documentDetailProduct.select("#productImg").first())
                .map(element -> element.attr("data-url"))
                .map(atr -> getEshopUuid().getProductStartUrl() + atr);
    }

    @Override
    protected boolean isProductUnavailable(Document documentDetailProduct) {
        return false == ofNullable(documentDetailProduct.select("span[class='ico-btn-cart']").first())
                .isPresent();
    }

    @Override
    protected Optional<BigDecimal> parseProductPriceForPackage(Document documentDetailProduct) {
        return ofNullable(documentDetailProduct.select("strong[itemprop='price']").first())
                .map(Element::text)
                .map(ConvertUtils::convertToBigDecimal);
    }

    @Override
    protected Optional<ProductAction> parseProductAction(Document documentDetailProduct) {
        return Optional.empty();
    }

    @Override
    protected Optional<Date> parseProductActionValidity(Document documentDetailProduct) {
        return Optional.empty();
    }
}
