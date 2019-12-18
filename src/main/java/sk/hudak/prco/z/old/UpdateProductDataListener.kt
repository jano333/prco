package sk.hudak.prco.z.old

@FunctionalInterface
interface UpdateProductDataListener {

    fun onUpdateStatus(updateStatusInfo: UpdateStatusInfo)
}
