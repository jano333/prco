package sk.hudak.prco.task.ng.ee.handlers.addprocess

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.manager.error.ErrorLogManager
import sk.hudak.prco.service.InternalTxService
import sk.hudak.prco.task.ng.ee.*
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

@Component
class FirstPageProductURLsEvent_5b_Handler(prcoObservable: PrcoObservable,
                                          addProductExecutors: AddProductExecutors,
                                           private val errorLogManager: ErrorLogManager,
                                           private val internalTxService: InternalTxService)
    : AddProcessHandler(prcoObservable, addProductExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(FirstPageProductURLsEvent_5b_Handler::class.java)!!
    }

    private fun handle(event: FirstPageProductURLsEvent) {
        LOG.trace("handle ${event.javaClass.simpleName}")

        LOG.debug("count of products URL before duplicity check  ${event.pageProductURLs.size}")
        filterDuplicityAsync(event.eshopUuid, event.searchKeyWord, event.pageProductURLs)
                .handle { pageProductURLsAfterDuplicity, exception ->
                    if (exception == null) {
                        handleDuplicityCheckResult(pageProductURLsAfterDuplicity, event)
                    } else {
                        prcoObservable.notify(FilterDuplicityErrorEvent(event, exception))
                    }
                }
    }

    private fun filterDuplicityAsync(eshopUuid: EshopUuid, searchKeyWord: String, urlList: List<String>): CompletableFuture<List<String>> {
        return CompletableFuture.supplyAsync {
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
        }
    }

    private fun handleDuplicityCheckResult(pageProductURLsAfterDuplicity: List<String>, event: FirstPageProductURLsEvent) {
        if (pageProductURLsAfterDuplicity.isEmpty()) {
            LOG.debug("count of products URL after duplicity check iz zero")
            return
        }
        LOG.debug("count of products URL after duplicity check ${pageProductURLsAfterDuplicity.size}")

        // filter only non existing
        filterNotExistingAsync(pageProductURLsAfterDuplicity)
                .handle { notExistingProducts, exception ->
                    if (exception == null) {
                        handleFilterNotExistingResult(notExistingProducts, event)
                    } else {
                        prcoObservable.notify(FilterNotExistingErrorEvent(event, exception))
                    }
                }
    }

    private fun filterNotExistingAsync(productsUrl: List<String>): CompletableFuture<List<String>> {
        return CompletableFuture.supplyAsync(
                Supplier {
                    val notExistingProducts = productsUrl.filter {
                        val exist = internalTxService.existProductWithURL(it)
                        if (exist) {
                            LOG.debug("product $it already existing")
                        }
                        !exist
                    }
                    notExistingProducts
                },
                addProductExecutors.internalServiceExecutor)
    }

    private fun handleFilterNotExistingResult(notExistingProducts: List<String>, event: FirstPageProductURLsEvent) {
        if (notExistingProducts.isEmpty()) {
            LOG.debug("count of non existing products URL is zero")
            return
        }
        LOG.debug("count of non existing products URL  ${notExistingProducts.size}")
        notExistingProducts.forEach {
            prcoObservable.notify(NewProductUrlEvent(it, event.eshopUuid))
        }
    }

    override fun update(source: Observable?, event: CoreEvent) {
        when (event) {
            is FirstPageProductURLsEvent -> addProductExecutors.handlerTaskExecutor.submit { handle(event) }
        }
    }
}


