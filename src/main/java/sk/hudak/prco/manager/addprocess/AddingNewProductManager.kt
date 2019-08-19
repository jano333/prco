package sk.hudak.prco.manager.addprocess

import sk.hudak.prco.api.EshopUuid

interface AddingNewProductManager {

    /**
     * @param productsUrl list of new product URL's
     */
    fun addNewProductsByUrl(productsUrl: List<String>)

    /**
     * Vyhlada produkty s danym klucovym slovom pre konkretny eshop a ulozi ich do tabulky NEW_PRODUCT.
     *
     * @param eshopUuid     eshop identifikator
     * @param searchKeyWord search key word
     */
    fun addNewProductsByKeywordForEshop(eshopUuid: EshopUuid, searchKeyWord: String)

    /**
     * Vyhlada produkty s danym klucovym slovom pre vsetky eshopy a ulozi ich do tabulky NEW_PRODUCT.
     *
     * @param searchKeyWord search key word
     */
    fun addNewProductsByKeywordForAllEshops(searchKeyWord: String)

    /**
     * @param searchKeyWords search key words
     */
    fun addNewProductsByKeywordsForAllEshops(vararg searchKeyWords: String)
}
