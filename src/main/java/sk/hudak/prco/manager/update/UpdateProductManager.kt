package sk.hudak.prco.manager.update

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.manager.update.event.UpdateAllProductsInEshopEvent
import sk.hudak.prco.manager.update.event.UpdateOneProductInEshopEvent
import sk.hudak.prco.manager.update.event.UpdateProductDataForProductIdEvent

interface UpdateProductManager {

    fun updateProductDataForOneProductInEshop(eshopUuid: EshopUuid)

    fun updateProductDataForEachProductInEshop(eshopUuid: EshopUuid)

    fun updateProductDataForProductWithId(productId: Long)
}

@Component
class UpdateProductManagerImpl(private val prcoObservable: PrcoObservable) : UpdateProductManager {

    companion object {
        private val LOG = LoggerFactory.getLogger(UpdateProductManagerImpl::class.java)!!
    }

    override fun updateProductDataForOneProductInEshop(eshopUuid: EshopUuid) {
        LOG.trace(">> updateProductDataForOneProductInEshop $eshopUuid")
        prcoObservable.notify(UpdateOneProductInEshopEvent(eshopUuid))
        LOG.trace("<< updateProductDataForOneProductInEshop $eshopUuid")
    }

    override fun updateProductDataForEachProductInEshop(eshopUuid: EshopUuid) {
        LOG.trace(">> updateProductDataForEachProductInEshop $eshopUuid")
        prcoObservable.notify(UpdateAllProductsInEshopEvent(eshopUuid))
        LOG.trace("<< updateProductDataForEachProductInEshop $eshopUuid")
    }

    override fun updateProductDataForProductWithId(productId: Long) {
        LOG.trace(">> updateProductDataForProductWithId $productId")
        prcoObservable.notify(UpdateProductDataForProductIdEvent(productId))
        LOG.trace("<< updateProductDataForProductWithId $productId")
    }
}


