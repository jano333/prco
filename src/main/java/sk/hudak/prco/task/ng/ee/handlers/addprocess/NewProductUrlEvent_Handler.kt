package sk.hudak.prco.task.ng.ee.handlers.addprocess

import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.parser.eshopuid.EshopUuidParser
import sk.hudak.prco.task.ng.ee.AddProductExecutors
import sk.hudak.prco.task.ng.ee.NewProductUrlEvent
import sk.hudak.prco.task.ng.ee.NewProductUrlWithEshopEvent
import sk.hudak.prco.task.ng.ee.ParseEshopUuidErrorEvent
import sk.hudak.prco.task.ng.ee.handlers.NoEshopLogSupplier
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

@Component
class NewProductUrlEvent_Handler(prcoObservable: PrcoObservable,
                                 addProductExecutors: AddProductExecutors,
                                 private val eshopUuidParser: EshopUuidParser)
    : AddProcessHandler(prcoObservable, addProductExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(NewProductUrlEvent_Handler::class.java)!!
    }

    private fun handle(event: NewProductUrlEvent) {
        LOG.trace("handle ${event.javaClass.simpleName}")

        parseEshopUuid(event.productUrl)
                .handle { eshopUuid, exception ->
                    if (exception == null) {
                        prcoObservable.notify(NewProductUrlWithEshopEvent(event.productUrl, eshopUuid))
                    } else {
                        prcoObservable.notify(ParseEshopUuidErrorEvent(event, exception))
                    }
                }
    }

    private fun parseEshopUuid(productUrl: String): CompletableFuture<EshopUuid> {
        return CompletableFuture.supplyAsync(NoEshopLogSupplier(
                Supplier {
                    eshopUuidParser.parseEshopUuid(productUrl)
                }),
                addProductExecutors.eshopUuidParserExecutor)
    }

    override fun update(source: Observable?, event: CoreEvent) {
        when (event) {
            is NewProductUrlEvent -> addProductExecutors.handlerTaskExecutor.submit {
                MDC.put("eshop", "not-defined")
                handle(event)
                MDC.remove("eshop")
            }
        }
    }
}

