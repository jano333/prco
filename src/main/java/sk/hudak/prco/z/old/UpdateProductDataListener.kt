package sk.hudak.prco.z.old

@Deprecated("differ package")
@FunctionalInterface
interface UpdateProductDataListener {

    fun onUpdateStatus(updateStatusInfo: UpdateStatusInfo)
}
