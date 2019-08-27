package sk.hudak.prco.manager.updateprocess

import org.slf4j.LoggerFactory

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
