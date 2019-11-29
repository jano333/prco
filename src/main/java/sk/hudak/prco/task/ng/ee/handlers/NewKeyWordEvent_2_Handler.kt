package sk.hudak.prco.task.ng.ee.handlers

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.builder.SearchUrlBuilder
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.events.PrcoObserver
import sk.hudak.prco.task.ng.ee.BuildSearchUrlForKeywordErrorEvent
import sk.hudak.prco.task.ng.ee.Executors
import sk.hudak.prco.task.ng.ee.NewKeyWordEvent
import sk.hudak.prco.task.ng.ee.NewKeyWordUrlEvent
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

/**
 * searchKeyword -> searchKeywordURL
 */
@Component
class NewKeyWordEvent_2_Handler(private val prcoObservable: PrcoObservable,
                                private var searchUrlBuilder: SearchUrlBuilder,
                                private val executors: Executors)
    : PrcoObserver {

    companion object {
        private val LOG = LoggerFactory.getLogger(NewKeyWordEvent_2_Handler::class.java)!!
    }

    // registering itself as observer
    init {
        prcoObservable.addObserver(this)
    }

    /**
     * searchKeyword -> searchKeywordRL
     */
    private fun handle(event: NewKeyWordEvent) {
        LOG.trace("handle ${event.javaClass.simpleName}")
        buildSearchUrlForKeyword(event.eshopUuid, event.searchKeyWord)
                .handle { searchUrl, exception ->
                    if (exception == null) {
                        prcoObservable.notify(NewKeyWordUrlEvent(event.eshopUuid, event.searchKeyWord, searchUrl))
                    } else {
                        prcoObservable.notify(BuildSearchUrlForKeywordErrorEvent(event, exception))
                    }
                }
    }

    private fun buildSearchUrlForKeyword(eshopUuid: EshopUuid, searchKeyword: String): CompletableFuture<String> {
        return CompletableFuture.supplyAsync(Supplier {
            LOG.trace("buildSearchUrlForKeyword")
            val searchUrl = searchUrlBuilder.buildSearchUrl(eshopUuid, searchKeyword)
            LOG.debug("build url for keyword $searchKeyword : $searchUrl")
            searchUrl
        }, executors.searchUrlBuilderExecutor)
    }

    override fun update(source: Observable?, event: CoreEvent) {
        when (event) {
            is NewKeyWordEvent -> executors.handlerTaskExecutor.submit { handle(event) }
        }
    }

}