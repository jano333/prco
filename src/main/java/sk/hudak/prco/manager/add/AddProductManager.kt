package sk.hudak.prco.manager.add

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.manager.add.event.NewEshopKeywordIdEvent
import sk.hudak.prco.manager.add.event.NewKeywordIdEvent
import sk.hudak.prco.manager.add.event.NewProductUrlsEvent
import java.util.*

interface AddProductManager {
    /**
     * @param productsUrl list of new product URL's
     */
    fun addNewProductsByUrl(productsUrl: List<String>)

    /**
     * Vyhlada produkty s danym klucovym slovom pre konkretny eshop a ulozi ich do tabulky NEW_PRODUCT.
     *
     * @param eshopUuid     eshop identifikator
     * @param searchKeyWordId use SearchKeyWordId
     */
    fun addNewProductsByKeywordForEshop(eshopUuid: EshopUuid, searchKeyWordId: Long)

    /**
     * Vyhlada produkty s danym klucovym slovom(id) pre vsetky eshopy a ulozi ich do tabulky NEW_PRODUCT.
     */
    fun addNewProductsByKeywordForAllEshops(searchKeyWordId: Long)

    /**
     * @param searchKeyWords search key words
     */
    fun addNewProductsByKeywordsForAllEshops(vararg searchKeyWordIds: Long)

    fun addNewProductsByConfiguredKeywordsForAllEshops()
}

@Component
class AddProductManagerImpl(private val prcoObservable: PrcoObservable) : AddProductManager {

    companion object {
        private val LOG = LoggerFactory.getLogger(AddProductManagerImpl::class.java)!!
    }

    override fun addNewProductsByConfiguredKeywordsForAllEshops() {
        LOG.trace(">> addNewProductsByConfiguredKeywordsForAllEshops")

//        EnumSet.of(EshopUuid.TESCO)
        EshopUuid.values()
                .forEach { eshopUuid ->
                    eshopUuid.config.supportedSearchKeywordIds.forEach { searchKeyWordId ->
                        prcoObservable.notify(NewEshopKeywordIdEvent(eshopUuid, searchKeyWordId))
                        Thread.sleep(1 * 1000)
                    }
                }

        LOG.trace("<< addNewProductsByConfiguredKeywordsForAllEshops")
    }

    override fun addNewProductsByUrl(productsUrl: List<String>) {
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
    override fun addNewProductsByKeywordForEshop(eshopUuid: EshopUuid, searchKeyWordId: Long) {
        LOG.trace(">> addNewProductsByKeywordForEshop $eshopUuid $searchKeyWordId")

        prcoObservable.notify(NewEshopKeywordIdEvent(eshopUuid, searchKeyWordId))

        LOG.trace("<< addNewProductsByKeywordForEshop $eshopUuid $searchKeyWordId")
    }

    override fun addNewProductsByKeywordForAllEshops(searchKeyWordId: Long) {
        LOG.trace(">> addNewProductsByKeywordForAllEshops $searchKeyWordId")

        prcoObservable.notify(NewKeywordIdEvent(searchKeyWordId))

        LOG.trace("<< addNewProductsByKeywordForAllEshops $searchKeyWordId")
    }

    override fun addNewProductsByKeywordsForAllEshops(vararg searchKeyWordIds: Long) {
        LOG.trace(">> addNewProductsByKeywordsForAllEshops $searchKeyWordIds")

        searchKeyWordIds.forEach {
            prcoObservable.notify(NewKeywordIdEvent(it))
        }

        LOG.trace("<< addNewProductsByKeywordsForAllEshops $searchKeyWordIds")
    }
}
