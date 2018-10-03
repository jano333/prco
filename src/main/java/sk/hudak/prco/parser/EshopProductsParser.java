package sk.hudak.prco.parser;

import sk.hudak.prco.api.EshopUuid;
import sk.hudak.prco.dto.internal.NewProductInfo;
import sk.hudak.prco.dto.internal.ProductForUpdateData;
import sk.hudak.prco.exception.ProductNameNotFoundException;

import java.util.List;

public interface EshopProductsParser {

    /**
     * Jednoznacky identifikator eshopu, pre ktory je urceny dany product parser.
     *
     * @return
     */
    EshopUuid getEshopUuid();

    /**
     * Vyhlada URL-cky vsetkych produktov v danom eshope, ktore vyhovuju vyhladavaciemu retazcu <code>searchKeyWord</code>
     *
     * @param searchKeyWord klucove slovo na zaklade ktoreho sa vyhladaju URL produktov pre eshop {@link #getEshopUuid()}
     * @return zoznam URL produktov pre dane klucove slovo. V pride ak nic nenajde vrati prazny zoznam.
     */
    List<String> parseUrlsOfProduct(String searchKeyWord);

    /**
     * @param productUrl url konkretneho produktu
     * @return
     */
    NewProductInfo parseNewProductInfo(String productUrl);

    /**
     * @param productUrl url konkretneho produktu
     * @return
     * @throws ProductNameNotFoundException, ProductPriceNotFoundException
     */
    ProductForUpdateData parseProductUpdateData(String productUrl);
}
