package sk.hudak.prco.task.ng.ee.handlers.addprocess

import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Component
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.service.InternalTxService
import sk.hudak.prco.task.ng.ee.AddProductExecutors
import sk.hudak.prco.task.ng.ee.FilterNotExistingProductErrorEvent
import sk.hudak.prco.task.ng.ee.NewProductUrlEvent
import sk.hudak.prco.task.ng.ee.NewProductUrlsEvent
import sk.hudak.prco.task.ng.ee.handlers.NoEshopLogSupplier
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
        LOG.trace("handle ${event.javaClass.simpleName}")
        if (event.pageProductURLs.isEmpty()) {
            LOG.info("count of products URL iz zero")
            return
        }
        LOG.debug("count of products URL ${event.pageProductURLs.size}")

        // filter only non existing
        filterNotExistingAsync(event.pageProductURLs)
                .handle { notExistingProducts, exception ->
                    if (exception == null) {
                        handleFilterNotExistingResult(notExistingProducts)
                    } else {
                        prcoObservable.notify(FilterNotExistingProductErrorEvent(event, exception))
                    }
                }
    }

    private fun handleFilterNotExistingResult(notExistingProducts: List<String>) {
        if (notExistingProducts.isEmpty()) {
            LOG.info("count of non existing products URL is zero")
            return
        }
        LOG.info("count of non existing products URL  ${notExistingProducts.size}")
        notExistingProducts.forEach {
            prcoObservable.notify(NewProductUrlEvent(it))
        }
    }

    private fun filterNotExistingAsync(productsUrl: Set<String>): CompletableFuture<List<String>> {
        return CompletableFuture.supplyAsync(NoEshopLogSupplier(
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
            is NewProductUrlsEvent -> addProductExecutors.handlerTaskExecutor.submit {
                MDC.put("eshop", "not-defined")
                handle(event)
                MDC.remove("eshop")
            }
        }
    }

}

