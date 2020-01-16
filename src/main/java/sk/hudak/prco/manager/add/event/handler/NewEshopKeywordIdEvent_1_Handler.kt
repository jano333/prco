package sk.hudak.prco.manager.add.event.handler

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.events.handler.EshopLogSupplier
import sk.hudak.prco.manager.add.event.AddProductExecutors
import sk.hudak.prco.manager.add.event.NewEshopKeywordIdEvent
import sk.hudak.prco.manager.add.event.NewKeywordEvent
import sk.hudak.prco.manager.add.event.RetrieveKeywordBaseOnKeywordIdErrorEvent
import sk.hudak.prco.service.InternalTxService
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

/**
 * searchKeywordId -> searchKeyword
 */
@Component
class NewEshopKeywordIdEvent_1_Handler(prcoObservable: PrcoObservable,
                                       addProductExecutors: AddProductExecutors,
                                       private val internalTxService: InternalTxService)

    : AddProcessHandler<NewEshopKeywordIdEvent>(prcoObservable, addProductExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(NewEshopKeywordIdEvent_1_Handler::class.java)!!
    }

    override fun isSpecificType(event: CoreEvent): Boolean = event is NewEshopKeywordIdEvent
    override fun getEshopUuid(event: NewEshopKeywordIdEvent): EshopUuid? = event.eshopUuid
    override fun getIdentifier(event: NewEshopKeywordIdEvent): String = event.identifier

    /**
     * searchKeywordId -> searchKeyword
     */
    override fun handle(event: NewEshopKeywordIdEvent) {

        // kontrola ci dane klucove slovo podporovane eshopom
        if (!event.eshopUuid.config.supportedSearchKeywordIds.contains(event.searchKeywordId)) {
            LOG.warn("searchKeyWordId ${event.searchKeywordId} is not supported for eshop ${event.eshopUuid}")
            return
        }

        retrieveKeywordBaseOnKeywordId(event.eshopUuid, event.searchKeywordId, event.identifier)
                .handle { keyword, exception ->
                    if (exception == null) {
                        prcoObservable.notify(NewKeywordEvent(keyword, event.eshopUuid, event.searchKeywordId, event.identifier))
                    } else {
                        prcoObservable.notify(RetrieveKeywordBaseOnKeywordIdErrorEvent(event, exception))
                    }
                }
    }

    private fun retrieveKeywordBaseOnKeywordId(eshopUuid: EshopUuid, searchKeywordId: Long, identifier: String): CompletableFuture<String> {
        return CompletableFuture.supplyAsync(EshopLogSupplier(eshopUuid, identifier,
                Supplier {
                    LOG.trace("retrieveKeywordBaseOnKeywordId")
                    internalTxService.getSearchKeywordById(searchKeywordId)
                }),
                addProductExecutors.internalServiceExecutor)
    }

}