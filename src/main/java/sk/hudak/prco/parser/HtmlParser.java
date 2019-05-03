package sk.hudak.prco.parser;

import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.dto.internal.ProductNewData;
import sk.hudak.prco.dto.internal.ProductUpdateData;

import java.util.List;

public interface HtmlParser {

    List<String> searchProductUrls(EshopUuid eshopUuid, String searchKeyWord);

    /**
     * Urobi connet na danu URL a vyparsuje html data o produkte.
     *
     * @param productUrl
     * @return
     */
    ProductNewData parseProductNewData(String productUrl);

    ProductUpdateData parseProductUpdateData(String productUrl);


}
