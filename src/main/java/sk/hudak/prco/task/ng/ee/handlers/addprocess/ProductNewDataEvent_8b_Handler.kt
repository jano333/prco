package sk.hudak.prco.task.ng.ee.handlers.addprocess

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.dto.ProductNewData
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.exception.PrcoRuntimeException
import sk.hudak.prco.manager.error.ErrorLogManager
import sk.hudak.prco.service.InternalTxService
import sk.hudak.prco.task.ng.ee.AddProductExecutors
import sk.hudak.prco.task.ng.ee.ProductNewDataEvent
import sk.hudak.prco.task.ng.ee.SaveProductNewDataErrorEvent
import sk.hudak.prco.task.ng.toNewProductCreateDto
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

@Component
class ProductNewDataEvent_8b_Handler(prcoObservable: PrcoObservable,
                                     addProductExecutors: AddProductExecutors,
                                     private val errorLogManager: ErrorLogManager,
                                     private val internalTxService: InternalTxService)

    : AddProcessHandler(prcoObservable, addProductExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(ProductNewDataEvent_8b_Handler::class.java)!!
    }

    private fun handle(event: ProductNewDataEvent) {
        LOG.trace("handle ${event.javaClass.simpleName}")

        saveProductNewData(event.productNewData)
                .handle { newProductId, exception ->
                    if (exception == null) {
                        LOG.debug("new product add with id $newProductId")
                    } else {
                        prcoObservable.notify(SaveProductNewDataErrorEvent(event, exception))
                    }
                }
    }

    private fun saveProductNewData(productNewData: ProductNewData): CompletableFuture<Long> {
        return CompletableFuture.supplyAsync(
                Supplier {
                    LOG.trace("saveProductNewData")
                    if (null == productNewData.name) {
                        //FIXME make as event
                        errorLogManager.logErrorParsingProductNameForNewProduct(productNewData.eshopUuid, productNewData.url)
                        throw PrcoRuntimeException("New product ${productNewData.url} has none name")
                    }

                    // preklopim a pridavam do DB
                    internalTxService.createNewProduct(productNewData.toNewProductCreateDto())
                },
                addProductExecutors.internalServiceExecutor)
    }

    override fun update(source: Observable?, event: CoreEvent) {
        when (event) {
            is ProductNewDataEvent -> addProductExecutors.handlerTaskExecutor.submit { handle(event) }
        }
    }

}

