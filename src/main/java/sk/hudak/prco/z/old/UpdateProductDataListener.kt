package sk.hudak.prco.z.old

import sk.hudak.prco.manager.updateprocess.UpdateStatusInfo

@FunctionalInterface
interface UpdateProductDataListener {

    fun onUpdateStatus(updateStatusInfo: UpdateStatusInfo)
}
