package sk.hudak.prco.manager.remove

import sk.hudak.prco.api.EshopUuid

/**
 * TODO doc naco sa to pouziva
 */
@FunctionalInterface
interface RemoveEshopManager {
    fun removeAllForEshop(eshopUuid: EshopUuid)
}