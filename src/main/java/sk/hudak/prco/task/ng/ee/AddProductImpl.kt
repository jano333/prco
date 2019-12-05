package sk.hudak.prco.task.ng.ee

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.events.PrcoObservable
import java.util.*

@Component
class AddProductImpl(private val prcoObservable: PrcoObservable) {

    companion object {
        private val LOG = LoggerFactory.getLogger(AddProductImpl::class.java)!!
    }

    fun addNewProductsByConfiguredKeywordsForAllEshops() {
        LOG.trace(">> addNewProductsByConfiguredKeywordsForAllEshops")

        EshopUuid.values().forEach { eshopUuid ->
            eshopUuid.config.supportedSearchKeywordIds.forEach { searchKeyWordId ->
                prcoObservable.notify(NewEshopKeywordIdEvent(eshopUuid, searchKeyWordId))
            }
            return
        }

        LOG.trace("<< addNewProductsByConfiguredKeywordsForAllEshops")
    }

    fun addNewProductsByUrl(productsUrl: List<String>) {
        LOG.trace(">> addNewProductsByUrl")

        val hashSet = HashSet<String>()
        hashSet.addAll(productsUrl)
        prcoObservable.notify(NewProductUrlsEvent(hashSet))

        LOG.trace("<< addNewProductsByUrl")
    }

    /**
     * Vyhlada produkty s danym klucovym slovom pre konkretny eshop a ulozi ich do tabulky NEW_PRODUCT.
     *
     * @param eshopUuid     eshop identifikator
     * @param searchKeyWordId use SearchKeyWordId
     */
    fun addNewProductsByKeywordForEshop(eshopUuid: EshopUuid, searchKeyWordId: Long) {
        LOG.trace(">> addNewProductsByKeywordForEshop $eshopUuid $searchKeyWordId")

        prcoObservable.notify(NewEshopKeywordIdEvent(eshopUuid, searchKeyWordId))

        LOG.trace("<< addNewProductsByKeywordForEshop $eshopUuid $searchKeyWordId")
    }

    fun addNewProductsByKeywordForAllEshops(searchKeyWordId: Long) {
        LOG.trace(">> addNewProductsByKeywordForAllEshops $searchKeyWordId")

        prcoObservable.notify(NewKeywordIdEvent(searchKeyWordId))

        LOG.trace("<< addNewProductsByKeywordForAllEshops $searchKeyWordId")
    }

    fun addNewProductsByKeywordsForAllEshops(vararg searchKeyWordIds: Long) {
        LOG.trace(">> addNewProductsByKeywordsForAllEshops $searchKeyWordIds")

        searchKeyWordIds.forEach {
            addNewProductsByKeywordForAllEshops(it)
        }

        LOG.trace("<< addNewProductsByKeywordsForAllEshops $searchKeyWordIds")
    }


}

