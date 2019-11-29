package sk.hudak.prco.task.ng.ee

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.events.PrcoObserver
import sk.hudak.prco.service.InternalTxService
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

/**
 * searchKeywordId -> searchKeyword
 */
@Component
class NewKeyWordIdEvent_1_Handler(private val prcoObservable: PrcoObservable,
                                  private val internalTxService: InternalTxService,
                                  private val executors: Executors)
    : PrcoObserver {

    companion object {
        private val LOG = LoggerFactory.getLogger(NewKeyWordIdEvent_1_Handler::class.java)!!
    }

    // registering itself as observer
    init {
        prcoObservable.addObserver(this)
    }

    /**
     * searchKeywordId -> searchKeyword
     */
    private fun handle(event: NewKeyWordIdEvent) {
        LOG.debug("handle ${event.javaClass.simpleName}")
        retrieveKeywordBaseOnKeywordId(event.searchKeyWordId)
                .handle { keyword, exception ->
                    if (exception == null) {
                        prcoObservable.notify(NewKeyWordEvent(event.eshopUuid, event.searchKeyWordId, keyword))
                    } else {
                        prcoObservable.notify(NewKeyWordIdErrorEvent(event, exception))
                    }
                }
    }


    private fun retrieveKeywordBaseOnKeywordId(searchKeyWordId: Long): CompletableFuture<String> {
        return CompletableFuture.supplyAsync(Supplier {
            LOG.debug("retrieveKeywordBaseOnKeywordId")
            internalTxService.getSearchKeywordById(searchKeyWordId)
        }, executors.internalServiceExecutor)
    }

    override fun update(source: Observable?, event: CoreEvent) {
        when (event) {
            is NewKeyWordIdEvent -> handle(event)
        }
    }
}