package sk.hudak.prco.task.ng.ee

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.events.PrcoObservable
import java.util.*

@Component
class AddProductImpl(private val prcoObservable: PrcoObservable) {

    companion object {
        val LOG = LoggerFactory.getLogger(AddProductImpl::class.java)!!
    }

    fun addNewProductsByUrl(productsUrl: List<String>) {
        val hashSet = HashSet<String>()
        hashSet.addAll(productsUrl)
        prcoObservable.notify(NewProductUrlsEvent(hashSet))
    }

    fun addNewProductsByConfiguredKeywordsForAllEshops() {
        // TODO impl
    }


    /**
     * Vyhlada produkty s danym klucovym slovom pre konkretny eshop a ulozi ich do tabulky NEW_PRODUCT.
     *
     * @param eshopUuid     eshop identifikator
     * @param searchKeyWordId use SearchKeyWordId
     */
    fun addNewProductsByKeywordForEshop(eshopUuid: EshopUuid, searchKeyWordId: Long) {
        prcoObservable.notify(NewEshopKeywordIdEvent(eshopUuid, searchKeyWordId))
    }

    fun addNewProductsByKeywordForAllEshops(searchKeyWordId: Long) {
        prcoObservable.notify(NewKeywordIdEvent(searchKeyWordId))
    }

    fun addNewProductsByKeywordsForAllEshops(vararg searchKeyWordIds: Long) {
        searchKeyWordIds.forEach {
            addNewProductsByKeywordForAllEshops(it)
        }
    }


}

