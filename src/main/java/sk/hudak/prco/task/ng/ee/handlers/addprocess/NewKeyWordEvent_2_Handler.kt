package sk.hudak.prco.task.ng.ee.handlers.addprocess

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.builder.SearchUrlBuilder
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.task.ng.ee.AddProductExecutors
import sk.hudak.prco.task.ng.ee.BuildSearchUrlForKeywordErrorEvent
import sk.hudak.prco.task.ng.ee.NewKeyWordEvent
import sk.hudak.prco.task.ng.ee.NewKeyWordUrlEvent
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

/**
 * searchKeyword -> searchKeywordURL
 */
@Component
class NewKeyWordEvent_2_Handler(prcoObservable: PrcoObservable,
                                addProductExecutors: AddProductExecutors,
                                private var searchUrlBuilder: SearchUrlBuilder)

    : AddProcessHandler(prcoObservable, addProductExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(NewKeyWordEvent_2_Handler::class.java)!!
    }

    /**
     * searchKeyword -> searchKeywordRL
     */
    private fun handle(event: NewKeyWordEvent) {
        LOG.trace("handle ${event.javaClass.simpleName}")
        buildSearchUrlForKeyword(event.eshopUuid, event.searchKeyWord)
                .handle { searchUrl, exception ->
                    if (exception == null) {
                        prcoObservable.notify(NewKeyWordUrlEvent(searchUrl, event.eshopUuid, event.searchKeyWord))
                    } else {
                        prcoObservable.notify(BuildSearchUrlForKeywordErrorEvent(event, exception))
                    }
                }
    }

    private fun buildSearchUrlForKeyword(eshopUuid: EshopUuid, searchKeyword: String): CompletableFuture<String> {
        return CompletableFuture.supplyAsync(
                Supplier {
                    LOG.trace("buildSearchUrlForKeyword")
                    val searchUrl = searchUrlBuilder.buildSearchUrl(eshopUuid, searchKeyword)
                    LOG.debug("build url for keyword $searchKeyword : $searchUrl")
                    searchUrl
                },
                addProductExecutors.searchUrlBuilderExecutor)
    }

    override fun update(source: Observable?, event: CoreEvent) {
        when (event) {
            is NewKeyWordEvent -> addProductExecutors.handlerTaskExecutor.submit { handle(event) }
        }
    }

}