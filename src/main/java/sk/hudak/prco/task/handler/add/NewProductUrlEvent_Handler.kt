package sk.hudak.prco.task.handler.add

import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.parser.eshopuid.EshopUuidParser
import sk.hudak.prco.task.add.AddProductExecutors
import sk.hudak.prco.task.add.NewProductUrlEvent
import sk.hudak.prco.task.add.NewProductUrlWithEshopEvent
import sk.hudak.prco.task.add.ParseEshopUuidErrorEvent
import sk.hudak.prco.task.handler.NoEshopLogSupplier
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
        LOG.trace("handle $event")

        parseEshopUuid(event.productUrl, event.identifier)
                .handle { eshopUuid, exception ->
                    if (exception == null) {
                        prcoObservable.notify(NewProductUrlWithEshopEvent(event.productUrl, eshopUuid, event.identifier))
                    } else {
                        prcoObservable.notify(ParseEshopUuidErrorEvent(event, exception))
                    }
                }
    }

    private fun parseEshopUuid(productUrl: String, identifier: String): CompletableFuture<EshopUuid> {
        return CompletableFuture.supplyAsync(NoEshopLogSupplier(identifier,
                Supplier {
                    eshopUuidParser.parseEshopUuid(productUrl)
                }),
                addProductExecutors.eshopUuidParserExecutor)
    }

    override fun update(source: Observable?, event: CoreEvent) {
        when (event) {
            is NewProductUrlEvent -> {
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

