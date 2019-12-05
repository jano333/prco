package sk.hudak.prco.task.ng.ee.handlers.addprocess

import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.builder.SearchUrlBuilder
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.task.ng.ee.AddProductExecutors
import sk.hudak.prco.task.ng.ee.BuildSearchUrlForKeywordErrorEvent
import sk.hudak.prco.task.ng.ee.NewKeywordEvent
import sk.hudak.prco.task.ng.ee.SearchKeywordUrlEvent
import sk.hudak.prco.task.ng.ee.handlers.EshopLogSupplier
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
    private fun handle(event: NewKeywordEvent) {
        LOG.trace("handle $event")

        buildSearchUrlForKeyword(event.eshopUuid, event.searchKeyword)
                .handle { searchUrl, exception ->
                    if (exception == null) {
                        prcoObservable.notify(SearchKeywordUrlEvent(searchUrl, 1, event.searchKeyword, event.eshopUuid))
                    } else {
                        prcoObservable.notify(BuildSearchUrlForKeywordErrorEvent(event, exception))
                    }
                }
    }

    private fun buildSearchUrlForKeyword(eshopUuid: EshopUuid, searchKeyword: String): CompletableFuture<String> {
        return CompletableFuture.supplyAsync(EshopLogSupplier(eshopUuid,
                Supplier {
                    LOG.trace("buildSearchUrlForKeyword")
                    val searchUrl = searchUrlBuilder.buildSearchUrl(eshopUuid, searchKeyword)
                    LOG.debug("build url for keyword $searchKeyword : $searchUrl")
                    searchUrl
                }),
                addProductExecutors.searchUrlBuilderExecutor)
    }

    override fun update(source: Observable?, event: CoreEvent) {
        when (event) {
            is NewKeywordEvent -> addProductExecutors.handlerTaskExecutor.submit {
                MDC.put("eshop", event.eshopUuid.toString())
                handle(event)
                MDC.remove("eshop")
            }
        }
    }

}