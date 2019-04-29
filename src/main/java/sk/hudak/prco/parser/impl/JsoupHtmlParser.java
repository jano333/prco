package sk.hudak.prco.parser.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.dto.internal.NewProductInfo;
import sk.hudak.prco.dto.internal.ProductForUpdateData;
import sk.hudak.prco.exception.PrcoRuntimeException;
import sk.hudak.prco.parser.EshopProductsParser;
import sk.hudak.prco.parser.EshopUuidParser;
import sk.hudak.prco.parser.HtmlParser;

import java.util.List;

import static sk.hudak.prco.utils.Validate.notNull;
import static sk.hudak.prco.utils.Validate.notNullNotEmpty;

/**
 * Jsoup implementacia {@link HtmlParser}.
 */
@Slf4j
@Component
public class JsoupHtmlParser implements HtmlParser {

    private EshopUuidParser eshopUuidParser;
    private List<EshopProductsParser> productParsers;

    @Autowired
    public JsoupHtmlParser(EshopUuidParser eshopUuidParser, List<EshopProductsParser> productParsers) {
        this.eshopUuidParser = eshopUuidParser;
        this.productParsers = productParsers;
    }

    @Override
    public List<String> searchProductUrls(EshopUuid eshopUuid, String searchKeyWord) {
        notNull(eshopUuid, "eshopUuid");
        notNullNotEmpty(searchKeyWord, "searchKeyWord");
        log.debug("start searching for keyword '{}'", searchKeyWord);

        List<String> result = findParserForEshop(eshopUuid).parseUrlsOfProduct(searchKeyWord);

        log.info("count of products found for keyword '{}': {}", searchKeyWord, result.size());
        return result;
    }

    @Override
    public NewProductInfo parseProductNewData(String productUrl) {
        notNullNotEmpty(productUrl, "productUrl");

        return findParserForEshop(productUrl).parseNewProductInfo(productUrl);
    }

    @Override
    public ProductForUpdateData parseProductUpdateData(String productUrl) {
        notNullNotEmpty(productUrl, "productUrl");

        return findParserForEshop(productUrl).parseProductUpdateData(productUrl);
    }

    private EshopProductsParser findParserForEshop(String productUrl) {
        // zistim typ eshopu na zaklade url
        EshopUuid eshopUuid = eshopUuidParser.parseEshopUuid(productUrl);

        // vyhladam html parser implementaciu na zaklade eshop uuid
        return findParserForEshop(eshopUuid);
    }

    private EshopProductsParser findParserForEshop(EshopUuid eshopUuid) {
        return productParsers.stream()
                .filter(f -> f.getEshopUuid().equals(eshopUuid))
                .findFirst()
                .orElseThrow(() -> new PrcoRuntimeException("Parser implementation for eshop " + eshopUuid + " not found."));
    }
}
