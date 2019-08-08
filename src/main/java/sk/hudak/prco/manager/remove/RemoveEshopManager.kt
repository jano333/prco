package sk.hudak.prco.manager.remove

import sk.hudak.prco.api.EshopUuid

@FunctionalInterface
interface RemoveEshopManager {
    fun removeAllForEshop(eshopUuid: EshopUuid)
}