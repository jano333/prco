package sk.hudak.prco.z.old

import org.slf4j.LoggerFactory
import sk.hudak.prco.manager.updateprocess.UpdateStatusInfo

class UpdateProductDataListenerAdapter : UpdateProductDataListener {

    companion object {
        val log = LoggerFactory.getLogger(UpdateProductDataListenerAdapter::class.java)!!

        val EMPTY_INSTANCE: UpdateProductDataListener = UpdateProductDataListenerAdapter()

        val LOG_INSTANCE: UpdateProductDataListener = object : UpdateProductDataListener {
            override fun onUpdateStatus(updateStatusInfo: UpdateStatusInfo) {
                log.debug(updateStatusInfo.toString())
            }
        }
    }

    override fun onUpdateStatus(updateStatusInfo: UpdateStatusInfo) {
        // do nothing
    }
}
