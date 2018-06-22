package sk.hudak.prco.manager;

import sk.hudak.prco.api.EshopUuid;

public interface AddingNewProductManager {

    /**
     * @param productUrl
     */
    void addNewProductByUrl(String productUrl);

    void addNewProductsByUrl(String... productsUrl);

    /**
     * Vyhlada produkty s danym klucovym slovom pre konkretny eshop a ulozi ich do tabulky NEW_PRODUCT.
     *
     * @param eshopUuid     eshop identifikator
     * @param searchKeyWord
     */
    void addNewProductsByKeywordForEshop(EshopUuid eshopUuid, String searchKeyWord);

    /**
     * Vyhlada produkty s danym klucovym slovom pre vsetky eshopy a ulozi ich do tabulky NEW_PRODUCT.
     *
     * @param searchKeyword
     */
    void addNewProductsByKeywordForAllEshops(String searchKeyword);
}
