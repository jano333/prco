package sk.hudak.prco.manager.remove

import sk.hudak.prco.api.EshopUuid

interface RemoveEshopManager {

    fun removeAllProductsForEshop(eshopUuid: EshopUuid)

}