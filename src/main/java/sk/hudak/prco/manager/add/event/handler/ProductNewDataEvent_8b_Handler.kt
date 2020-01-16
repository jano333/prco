package sk.hudak.prco.manager.add.event.handler

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.dto.ProductNewData
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.events.handler.EshopLogSupplier
import sk.hudak.prco.exception.PrcoRuntimeException
import sk.hudak.prco.kotlin.toNewProductCreateDto
import sk.hudak.prco.manager.add.event.AddProductExecutors
import sk.hudak.prco.manager.add.event.ProductNewDataEvent
import sk.hudak.prco.manager.add.event.SaveProductNewDataErrorEvent
import sk.hudak.prco.manager.add.event.SaveProductNewDataEvent
import sk.hudak.prco.service.InternalTxService
import sk.hudak.prco.z.old.ErrorLogManager
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

@Component
class ProductNewDataEvent_8b_Handler(prcoObservable: PrcoObservable,
                                     addProductExecutors: AddProductExecutors,
                                     private val errorLogManager: ErrorLogManager,
                                     private val internalTxService: InternalTxService)

    : AddProcessHandler<ProductNewDataEvent>(prcoObservable, addProductExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(ProductNewDataEvent_8b_Handler::class.java)!!
    }

    override fun isSpecificType(event: CoreEvent): Boolean = event is ProductNewDataEvent
    override fun getEshopUuid(event: ProductNewDataEvent): EshopUuid? = event.productNewData.eshopUuid
    override fun getIdentifier(event: ProductNewDataEvent): String = event.identifier

    override fun handle(event: ProductNewDataEvent) {

        saveProductNewData(event.productNewData, event.identifier)
                .handle { newProductId, exception ->
                    if (exception == null) {
                        LOG.debug("new product add with id $newProductId")
                        prcoObservable.notify(SaveProductNewDataEvent(newProductId, event.productNewData.url, event.productNewData.eshopUuid, event.identifier))
                    } else {
                        prcoObservable.notify(SaveProductNewDataErrorEvent(event, exception))
                    }
                }
    }

    private fun saveProductNewData(productNewData: ProductNewData, identifier: String): CompletableFuture<Long> {
        return CompletableFuture.supplyAsync(EshopLogSupplier(productNewData.eshopUuid, identifier,
                Supplier {
                    LOG.trace("saveProductNewData")
                    if (null == productNewData.name) {
                        //FIXME make as event
                        errorLogManager.logErrorParsingProductNameForNewProduct(productNewData.eshopUuid, productNewData.url)
                        throw PrcoRuntimeException("New product ${productNewData.url} has none name")
                    }

                    // preklopim a pridavam do DB
                    internalTxService.createNewProduct(productNewData.toNewProductCreateDto())
                }),
                addProductExecutors.internalServiceExecutor)
    }

}

