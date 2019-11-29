package sk.hudak.prco.task.ng.ee

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.events.PrcoObservable

@Component
class AddProductImpl(private val prcoObservable: PrcoObservable) {

    companion object {
        val LOG = LoggerFactory.getLogger(AddProductImpl::class.java)!!
    }


    /**
     * NewKeyWordIdEvent ->
     * NewKeyWordEvent ->
     * NewKeyWordUrlEvent ->
     * NewKeyWordFirstDocumentEvent ->
     *
     *
     */
    fun addNewProductsByKeywordForAllEshops(eshopUuid: EshopUuid, searchKeyWordId: Long) {
        try {
            prcoObservable.notify(NewKeyWordIdEvent(eshopUuid, searchKeyWordId))

        } catch (e: Exception) {
            LOG.error("tu", e)
        }
    }
}