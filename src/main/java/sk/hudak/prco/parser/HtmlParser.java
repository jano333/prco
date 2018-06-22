package sk.hudak.prco.parser;

import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.dto.newproduct.NewProductInfo;
import sk.hudak.prco.dto.product.ProductForUpdateData;

import java.util.List;

public interface HtmlParser {

    List<String> searchProductUrls(EshopUuid eshopUuid, String searchKeyWord);

    /**
     * Urobi connet na danu URL a vyparsuje html data o produkte.
     *
     * @param productUrl
     * @return
     */
    NewProductInfo parseNewProductInfo(String productUrl);

    ProductForUpdateData parseProductUpdateData(String productUrl);


}
