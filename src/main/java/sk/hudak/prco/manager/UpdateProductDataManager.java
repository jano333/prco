package sk.hudak.prco.manager;

import sk.hudak.prco.api.EshopUuid;

public interface UpdateProductDataManager {

    void updateAllProductsDataForEshop(EshopUuid eshopUuid);

    void updateAllProductsDataForEshop(EshopUuid eshopUuid, UpdateProductInfoListener listener);

    void updateAllProductsDataForAllEshops(UpdateProductInfoListener listener);

    void updateAllProductsDataInGroup(Long groupId);

    void updateProductData(Long productId);
}
