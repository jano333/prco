package sk.hudak.prco.task.ng.ee.handlers.addprocess

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.service.InternalTxService
import sk.hudak.prco.task.ng.ee.AddProductExecutors
import sk.hudak.prco.task.ng.ee.NewKeywordEvent
import sk.hudak.prco.task.ng.ee.NewKeywordIdEvent
import sk.hudak.prco.task.ng.ee.RetrieveKeywordBaseOnKeywordIdErrorEvent
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

/**
 * searchKeywordId -> searchKeyword
 */
@Component
class NewKeyWordIdEvent_1_Handler(prcoObservable: PrcoObservable,
                                  addProductExecutors: AddProductExecutors,
                                  private val internalTxService: InternalTxService)

    : AddProcessHandler(prcoObservable, addProductExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(NewKeyWordIdEvent_1_Handler::class.java)!!
    }

    /**
     * searchKeywordId -> searchKeyword
     */
    private fun handle(event: NewKeywordIdEvent) {
        LOG.trace("handle ${event.javaClass.simpleName}")
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
            is NewKeywordIdEvent -> addProductExecutors.handlerTaskExecutor.submit { handle(event) }
        }
    }
}