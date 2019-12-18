package sk.hudak.prco.manager.updateprocess

import sk.hudak.prco.api.EshopUuid

//TODO data class
class UpdateStatusInfo {

    var eshopUuid: EshopUuid? = null
    var countOfProductsWaitingToBeUpdated: Long = 0
    var countOfProductsAlreadyUpdated: Long = 0

    constructor(eshopUuid: EshopUuid, countOfProductsWaitingToBeUpdated: Long, countOfProductsAlreadyUpdated: Long) {
        this.eshopUuid = eshopUuid
        this.countOfProductsWaitingToBeUpdated = countOfProductsWaitingToBeUpdated
        this.countOfProductsAlreadyUpdated = countOfProductsAlreadyUpdated
    }

    override fun toString(): String {
        return "UpdateStatusInfo(" +
                "eshopUuid=$eshopUuid, " +
                "countOfProductsWaitingToBeUpdated=$countOfProductsWaitingToBeUpdated, " +
                "countOfProductsAlreadyUpdated=$countOfProductsAlreadyUpdated)"
    }


}
