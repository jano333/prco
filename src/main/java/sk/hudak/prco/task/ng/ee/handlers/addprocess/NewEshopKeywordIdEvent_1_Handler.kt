package sk.hudak.prco.task.ng.ee.handlers.addprocess

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.service.InternalTxService
import sk.hudak.prco.task.ng.ee.AddProductExecutors
import sk.hudak.prco.task.ng.ee.NewEshopKeywordIdEvent
import sk.hudak.prco.task.ng.ee.NewKeywordEvent
import sk.hudak.prco.task.ng.ee.RetrieveKeywordBaseOnKeywordIdErrorEvent
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

/**
 * searchKeywordId -> searchKeyword
 */
@Component
class NewEshopKeywordIdEvent_1_Handler(prcoObservable: PrcoObservable,
                                       addProductExecutors: AddProductExecutors,
                                       private val internalTxService: InternalTxService)

    : AddProcessHandler(prcoObservable, addProductExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(NewEshopKeywordIdEvent_1_Handler::class.java)!!
    }

    /**
     * searchKeywordId -> searchKeyword
     */
    private fun handle(event: NewEshopKeywordIdEvent) {
        LOG.trace("handle ${event.javaClass.simpleName}")

        // kontrola ci dane klucove slovo podporovane eshopom
        if (!event.eshopUuid.config.supportedSearchKeywordIds.contains(event.searchKeywordId)) {
            LOG.warn("searchKeyWordId ${event.searchKeywordId} is not supported for eshop $event.eshopUuid")
            return
        }

        retrieveKeywordBaseOnKeywordId(event.searchKeywordId)
                .handle { keyword, exception ->
                    if (exception == null) {
                        prcoObservable.notify(NewKeywordEvent(keyword, event.eshopUuid, event.searchKeywordId))
                    } else {
                        prcoObservable.notify(RetrieveKeywordBaseOnKeywordIdErrorEvent(event, exception))
                    }
                }
    }

    private fun retrieveKeywordBaseOnKeywordId(searchKeyWordId: Long): CompletableFuture<String> {
        return CompletableFuture.supplyAsync(
                Supplier {
                    LOG.trace("retrieveKeywordBaseOnKeywordId")
                    internalTxService.getSearchKeywordById(searchKeyWordId)
                },
                addProductExecutors.internalServiceExecutor)
    }

    override fun update(source: Observable?, event: CoreEvent) {
        when (event) {
            is NewEshopKeywordIdEvent -> addProductExecutors.handlerTaskExecutor.submit { handle(event) }
        }
    }
}