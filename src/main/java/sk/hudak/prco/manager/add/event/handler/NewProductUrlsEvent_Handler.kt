package sk.hudak.prco.manager.add.event.handler

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.events.handler.NoEshopLogSupplier
import sk.hudak.prco.manager.add.event.AddProductExecutors
import sk.hudak.prco.manager.add.event.FilterNotExistingProductErrorEvent
import sk.hudak.prco.manager.add.event.NewProductUrlEvent
import sk.hudak.prco.manager.add.event.NewProductUrlsEvent
import sk.hudak.prco.service.InternalTxService
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

@Component
class NewProductUrlsEvent_Handler(prcoObservable: PrcoObservable,
                                  addProductExecutors: AddProductExecutors,
                                  private val internalTxService: InternalTxService)
    : AddProcessHandler<NewProductUrlsEvent>(prcoObservable, addProductExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(NewProductUrlsEvent_Handler::class.java)!!
    }

    override fun isSpecificType(event: CoreEvent): Boolean = event is NewProductUrlsEvent
    override fun getEshopUuid(event: NewProductUrlsEvent): EshopUuid? = null
    override fun getIdentifier(event: NewProductUrlsEvent): String = event.identifier


    override fun handle(event: NewProductUrlsEvent) {

        if (event.pageProductURLs.isEmpty()) {
            LOG.info("count of products URL iz zero")
            return
        }
        LOG.debug("count of products URL ${event.pageProductURLs.size}")

        // filter only non existing
        filterNotExistingAsync(event.pageProductURLs, event.identifier)
                .handle { notExistingProducts, exception ->
                    if (exception == null) {
                        handleFilterNotExistingResult(notExistingProducts, event.identifier)
                    } else {
                        prcoObservable.notify(FilterNotExistingProductErrorEvent(event, exception))
                    }
                }
    }

    private fun handleFilterNotExistingResult(notExistingProducts: List<String>, identifier: String) {
        if (notExistingProducts.isEmpty()) {
            LOG.info("count of non existing products URL is zero")
            return
        }
        LOG.info("count of non existing products URL  ${notExistingProducts.size}")
        var index = 1
        notExistingProducts.forEach {
            val newIdentifier = identifier + "_$index"
            index++
            prcoObservable.notify(NewProductUrlEvent(it, newIdentifier))
        }
    }

    private fun filterNotExistingAsync(productsUrl: Set<String>, identifier: String): CompletableFuture<List<String>> {
        return CompletableFuture.supplyAsync(NoEshopLogSupplier(identifier,
                Supplier {
                    val notExistingProducts = productsUrl.filter {
                        val exist = internalTxService.existProductWithURL(it)
                        if (exist) {
                            LOG.info("product $it already exist")
                        }
                        !exist
                    }
                    notExistingProducts
                }),
                addProductExecutors.internalServiceExecutor)
    }


}

