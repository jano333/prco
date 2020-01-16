package sk.hudak.prco.manager.add.event.handler

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.events.handler.NoEshopLogSupplier
import sk.hudak.prco.manager.add.event.AddProductExecutors
import sk.hudak.prco.manager.add.event.NewProductUrlEvent
import sk.hudak.prco.manager.add.event.NewProductUrlWithEshopEvent
import sk.hudak.prco.manager.add.event.ParseEshopUuidErrorEvent
import sk.hudak.prco.parser.eshopuid.EshopUuidParser
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

@Component
class NewProductUrlEvent_Handler(prcoObservable: PrcoObservable,
                                 addProductExecutors: AddProductExecutors,
                                 private val eshopUuidParser: EshopUuidParser)
    : AddProcessHandler<NewProductUrlEvent>(prcoObservable, addProductExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(NewProductUrlEvent_Handler::class.java)!!
    }

    override fun isSpecificType(event: CoreEvent): Boolean = event is NewProductUrlEvent
    override fun getEshopUuid(event: NewProductUrlEvent): EshopUuid? = null
    override fun getIdentifier(event: NewProductUrlEvent): String = event.identifier


    override fun handle(event: NewProductUrlEvent) {

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

}

