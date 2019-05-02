package sk.hudak.prco.manager;

import sk.hudak.prco.api.EshopUuid;

public interface AddingNewProductManager {

    /**
     * @param productsUrl list of new product URL's
     */
    void addNewProductsByUrl(String... productsUrl);

    /**
     * Vyhlada produkty s danym klucovym slovom pre konkretny eshop a ulozi ich do tabulky NEW_PRODUCT.
     *
     * @param eshopUuid     eshop identifikator
     * @param searchKeyWord search key word
     */
    void addNewProductsByKeywordForEshop(EshopUuid eshopUuid, String searchKeyWord);

    /**
     * Vyhlada produkty s danym klucovym slovom pre vsetky eshopy a ulozi ich do tabulky NEW_PRODUCT.
     *
     * @param searchKeyWord search key word
     */
    void addNewProductsByKeywordForAllEshops(String searchKeyWord);

    /**
     * @param searchKeyWords search key words
     */
    void addNewProductsByKeywordsForAllEshops(String... searchKeyWords);
}
