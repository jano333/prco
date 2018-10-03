package sk.hudak.prco.manager;

import sk.hudak.prco.api.EshopUuid;

public interface UpdateProductDataManager {

    /**
     * Update product data vsetkych produktov pre dany eshop.
     *
     * @param eshopUuid
     * @param listener
     */
    void updateAllProductsDataForEshop(EshopUuid eshopUuid, UpdateProductInfoListener listener);

    void updateAllProductsDataForAllEshops(UpdateProductInfoListener listener);

    void updateAllProductsDataInGroup(Long groupId);

    void updateProductData(Long productId);

    /**
     * Update product data pre vsetky produkty, ktore este nie su v ziadnej grupe
     * @param listener
     */
    void updateAllProductDataNotInAnyGroup(UpdateProductInfoListener listener);
}
