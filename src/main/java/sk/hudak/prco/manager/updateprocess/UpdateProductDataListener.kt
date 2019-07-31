package sk.hudak.prco.manager.updateprocess

@FunctionalInterface
interface UpdateProductDataListener {

    fun onUpdateStatus(updateStatusInfo: UpdateStatusInfo)
}
