package sk.hudak.prco.z.old

import sk.hudak.prco.api.EshopUuid

@Deprecated("old")
interface UpdateProductDataManager {

    /**
     * @param productId product id
     */
    fun updateProductData(productId: Long)

    /**
     * @param listener
     */
    fun updateProductDataForEachProductInEachEshop(listener: UpdateProductDataListener)

    /**
     * Update product data vsetkych produktov pre dany eshop.
     *
     * @param eshopUuid eshop unique identification
     * @param listener
     */
    fun updateProductDataForEachProductInEshop(eshopUuid: EshopUuid, listener: UpdateProductDataListener)

    /**
     * @param groupId group id
     */
    fun updateProductDataForEachProductInGroup(groupId: Long, listener: UpdateProductDataListener)


    /**
     * Update product data pre vsetky produkty, ktore este nie su v ziadnej grupe
     *
     * @param listener
     */
    fun updateProductDataForEachProductNotInAnyGroup(listener: UpdateProductDataListener)


}
