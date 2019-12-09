package sk.hudak.prco.task.handler.add

import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Component
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.service.InternalTxService
import sk.hudak.prco.task.add.AddProductExecutors
import sk.hudak.prco.task.add.FilterNotExistingProductErrorEvent
import sk.hudak.prco.task.add.NewProductUrlEvent
import sk.hudak.prco.task.add.NewProductUrlsEvent
import sk.hudak.prco.task.handler.NoEshopLogSupplier
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

@Component
class NewProductUrlsEvent_Handler(prcoObservable: PrcoObservable,
                                  addProductExecutors: AddProductExecutors,
                                  private val internalTxService: InternalTxService)
    : AddProcessHandler(prcoObservable, addProductExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(NewProductUrlsEvent_Handler::class.java)!!
    }

    private fun handle(event: NewProductUrlsEvent) {
        LOG.trace("handle $event")

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

    override fun update(source: Observable?, event: CoreEvent) {
        when (event) {
            is NewProductUrlsEvent -> {
                LOG.trace(">> update ${event.javaClass.simpleName}")
                addProductExecutors.handlerTaskExecutor.submit {
                    MDC.put("eshop", "not-defined")
                    MDC.put("identifier", event.identifier)
                    handle(event)
                    MDC.remove("eshop")
                    MDC.remove("identifier")
                }
                LOG.trace("<< update ${event.javaClass.simpleName}")
            }
        }
    }

}

