package sk.hudak.prco.eshop;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.api.ProductAction;
import sk.hudak.prco.parser.UnitParser;
import sk.hudak.prco.parser.impl.JSoupProductParser;
import sk.hudak.prco.utils.ConvertUtils;
import sk.hudak.prco.utils.JsoupUtils;
import sk.hudak.prco.utils.UserAgentDataHolder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Component
public class MojaLekarenProductParser extends JSoupProductParser {

    @Autowired
    public MojaLekarenProductParser(UnitParser unitParser, UserAgentDataHolder userAgentDataHolder) {
        super(unitParser, userAgentDataHolder);
    }

    @Override
    public EshopUuid getEshopUuid() {
        return EshopUuid.MOJA_LEKAREN;
    }

    @Override
    protected int parseCountOfPages(Document documentList) {
        Element select = documentList.select("p.pagination__part.pagination__part--page").first();
        if (select == null) {
            //FIXME move to parent as constants for one page only
            return 1;
        }
        return Integer.valueOf(select.children().last().previousElementSibling().previousElementSibling().text());
    }

    @Override
    protected List<String> parsePageForProductUrls(Document documentList, int pageNumber) {
        List<String> urls = new ArrayList<>();
        for (Element element : documentList.select("li[class=product]")) {
            Element aHref = element.child(0).child(0);
            String href = aHref.attr("href");
            urls.add(getEshopUuid().getProductStartUrl() + href);
        }
        return urls;
    }

    @Override
    protected Optional<String> parseProductNameFromDetail(Document documentDetailProduct) {
        return Optional.of(documentDetailProduct.select("body > main > div.detail-top.list > div > h1").first())
                .filter(Objects::nonNull)
                .map(Element::text);
    }

    @Override
    protected Optional<String> parseProductPictureURL(Document documentDetailProduct) {
        Elements select = documentDetailProduct.select("div.product__img > a > picture > img");
        Element first = select.first();
        if (first == null) {
            return Optional.empty();
        }
        return Optional.of(getEshopUuid().getProductStartUrl() + first.attr("data-srcset"));
    }

    @Override
    protected boolean isProductUnavailable(Document documentDetailProduct) {
        return JsoupUtils.notExistElement(documentDetailProduct, "#frm-addMultipleToBasket-form > button > meta");
    }

    @Override
    protected Optional<BigDecimal> parseProductPriceForPackage(Document documentDetailProduct) {
        return Optional.of(documentDetailProduct.select("span[itemprop=price]").first())
                .filter(Objects::nonNull)
                .map(Element::text)
                .map(ConvertUtils::convertToBigDecimal);
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
}
