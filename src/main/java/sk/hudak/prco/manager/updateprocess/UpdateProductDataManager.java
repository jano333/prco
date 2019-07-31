package sk.hudak.prco.manager.updateprocess;

import sk.hudak.prco.api.EshopUuid;

public interface UpdateProductDataManager {

    /**
     * @param productId product id
     */
    void updateProductData(Long productId);

    /**
     * @param groupId group id
     */
    void updateProductDataForEachProductInGroup(Long groupId, UpdateProductDataListener listener);

    /**
     * Update product data vsetkych produktov pre dany eshop.
     *
     * @param eshopUuid eshop unique identification
     * @param listener
     */
    void updateProductDataForEachProductInEshop(EshopUuid eshopUuid, UpdateProductDataListener listener);

    /**
     * @param listener
     */
    void updateProductDataForEachProductInEachEshop(UpdateProductDataListener listener);


    /**
     * Update product data pre vsetky produkty, ktore este nie su v ziadnej grupe
     *
     * @param listener
     */
    void updateProductDataForEachProductNotInAnyGroup(UpdateProductDataListener listener);


}
