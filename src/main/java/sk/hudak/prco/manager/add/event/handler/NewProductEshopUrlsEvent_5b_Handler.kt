package sk.hudak.prco.manager.add.event.handler

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.events.handler.EshopLogSupplier
import sk.hudak.prco.manager.add.event.*
import sk.hudak.prco.service.InternalTxService
import sk.hudak.prco.z.old.ErrorLogManager
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

@Component
class NewProductEshopUrlsEvent_5b_Handler(prcoObservable: PrcoObservable,
                                          addProductExecutors: AddProductExecutors,
                                          private val errorLogManager: ErrorLogManager,
                                          private val internalTxService: InternalTxService)
    : AddProcessHandler<NewProductEshopUrlsEvent>(prcoObservable, addProductExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(NewProductEshopUrlsEvent_5b_Handler::class.java)!!
    }

    override fun isSpecificType(event: CoreEvent): Boolean = event is NewProductEshopUrlsEvent
    override fun getEshopUuid(event: NewProductEshopUrlsEvent): EshopUuid? = event.eshopUuid
    override fun getIdentifier(event: NewProductEshopUrlsEvent): String = event.identifier

    override fun handle(event: NewProductEshopUrlsEvent) {
        LOG.trace("handle $event")

        LOG.debug("count of products URL before duplicity check  ${event.pageProductURLs.size}")
        filterDuplicityAsync(event.eshopUuid, event.searchKeyWord, event.pageProductURLs, event.identifier)
                .handle { pageProductURLsAfterDuplicity, exception ->
                    if (exception == null) {
                        handleDuplicityCheckResult(pageProductURLsAfterDuplicity, event)
                    } else {
                        prcoObservable.notify(FilterDuplicityErrorEvent(event, exception))
                    }
                }
    }

    private fun filterDuplicityAsync(eshopUuid: EshopUuid, searchKeyWord: String, urlList: List<String>, identifier: String): CompletableFuture<List<String>> {
        return CompletableFuture.supplyAsync(EshopLogSupplier(eshopUuid, identifier,
                Supplier {
                    // key: productUrl, value: count of duplicity
                    var result = mutableMapOf<String, Int>()
                    urlList.forEach {
                        var entry = result[it]
                        if (entry == null) {
                            result[it] = 1
                        } else {
                            entry++
                            result[it] = entry
                        }
                    }

                    result.forEach { (key, value) ->
                        if (value != 1) {
                            LOG.error("product with URL $key is more than one, count: $value")
                            //FIXME prerobit na event
                            errorLogManager.logErrorDuplicityDuringfindinUrlOfProducts(eshopUuid, searchKeyWord, key)
                        }
                    }

                    val toList = result.keys.toList()
                    toList
                }))
        //TODO nechyba tu executor ???? preverit preco som nedal...
    }

    private fun handleDuplicityCheckResult(pageProductURLsAfterDuplicity: List<String>, event: NewProductEshopUrlsEvent) {
        if (pageProductURLsAfterDuplicity.isEmpty()) {
            LOG.debug("count of products URL after duplicity check iz zero")
            return
        }
        LOG.debug("count of products URL after duplicity check ${pageProductURLsAfterDuplicity.size}")

        // filter only non existing
        filterNotExistingAsync(event.eshopUuid, pageProductURLsAfterDuplicity, event.identifier)
                .handle { notExistingProducts, exception ->
                    if (exception == null) {
                        handleFilterNotExistingResult(notExistingProducts, event)
                    } else {
                        prcoObservable.notify(FilterNotExistingErrorEvent(event, exception))
                    }
                }
    }

    private fun filterNotExistingAsync(eshopUuid: EshopUuid, productsUrl: List<String>, identifier: String): CompletableFuture<List<String>> {
        return CompletableFuture.supplyAsync(EshopLogSupplier(eshopUuid, identifier,
                Supplier {
                    val notExistingProducts = productsUrl.filter {
                        val exist = internalTxService.existProductWithURL(it)
                        if (exist) {
                            LOG.info("product $it already existing")
                        }
                        !exist
                    }
                    notExistingProducts
                }),
                addProductExecutors.internalServiceExecutor)
    }

    //FIXME urobit danu metodu ako osobitny handler?
    private fun handleFilterNotExistingResult(notExistingProducts: List<String>, event: NewProductEshopUrlsEvent) {
        if (notExistingProducts.isEmpty()) {
            LOG.info("count of non existing products URL is zero")
            return
        }
        LOG.info("count of non existing products URL  ${notExistingProducts.size}")
        var index = 1
        notExistingProducts.forEach {
            val identifier = event.identifier + "_$index"
            index++
            prcoObservable.notify(NewProductUrlWithEshopEvent(it, event.eshopUuid, identifier))
        }
    }
}


