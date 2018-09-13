package sk.hudak.prco.eshop;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.api.ProductAction;
import sk.hudak.prco.exception.PrcoRuntimeException;
import sk.hudak.prco.parser.UnitParser;
import sk.hudak.prco.parser.WatchDogParser;
import sk.hudak.prco.parser.impl.JSoupProductParser;
import sk.hudak.prco.utils.UserAgentDataHolder;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static sk.hudak.prco.api.EshopUuid.OBI;
import static sk.hudak.prco.utils.ConvertUtils.convertToBigDecimal;

@Slf4j
@Component
public class ObiProductParser extends JSoupProductParser implements WatchDogParser {
    // TODO doriesit aby bralo len kosicku pobocku? pozor su 2...

    @Autowired
    public ObiProductParser(UnitParser unitParser, UserAgentDataHolder userAgentDataHolder) {
        super(unitParser, userAgentDataHolder);
    }

    @Override
    public EshopUuid getEshopUuid() {
        return OBI;
    }

    @Override
    protected int getTimeout() {
        // koli pomalym odozvam davam na 15 sekund
        return 15000;
    }

    @Override
    protected Optional<String> parseProductNameFromDetail(Document documentDetailProduct) {
        Elements select = documentDetailProduct.select("h1[class=h2 overview__heading]");
        if (select.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(((TextNode) select.get(0).childNode(0)).getWholeText());
    }

    @Override
    protected boolean isProductUnavailable(Document documentDetailProduct) {
        //TODO
        return false;
    }

    @Override
    protected Optional<BigDecimal> parseProductPriceForPackage(Document documentDetailProduct) {
        Elements select = documentDetailProduct.select("strong[itemprop=price]");
        Element first = select.first();
        String cenaZaBalenie = first.text();
        return Optional.of(convertToBigDecimal(cenaZaBalenie));
    }

    @Override
    protected int parseCountOfPages(Document documentList) {
        //TODO
        throw new PrcoRuntimeException("Not yet implemented");
    }

    @Override
    protected List<String> parsePageForProductUrls(Document documentList, int pageNumber) {
        //TODO
        throw new PrcoRuntimeException("Not yet implemented");
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
        //TODO impl
        return Optional.empty();
    }
}
