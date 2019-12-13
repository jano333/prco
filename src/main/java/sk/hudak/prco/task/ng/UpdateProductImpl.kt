package sk.hudak.prco.task.ng

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.task.update.UpdateProductsInEshopEvent

@Component
class UpdateProductImpl(private val prcoObservable: PrcoObservable) {

    companion object {
        private val LOG = LoggerFactory.getLogger(UpdateProductImpl::class.java)!!
    }

    fun updateProductDataForEachProductInEshop(eshopUuid: EshopUuid) {
        LOG.trace(">> updateProductDataForEachProductInEshop $eshopUuid")
        prcoObservable.notify(UpdateProductsInEshopEvent(eshopUuid))
        LOG.trace("<< updateProductDataForEachProductInEshop $eshopUuid")
    }

}




