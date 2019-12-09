package sk.hudak.prco.task.ng.ee.handlers.addprocess

import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.builder.SearchUrlBuilder
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.task.ng.ee.AddProductExecutors
import sk.hudak.prco.task.ng.ee.BuildNextPageSearchUrlErrorEvent
import sk.hudak.prco.task.ng.ee.BuildNextSearchPageUrlEvent
import sk.hudak.prco.task.ng.ee.SearchKeywordUrlEvent
import sk.hudak.prco.task.ng.ee.handlers.EshopLogSupplier
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

@Component
class BuildNextSearchPageUrl_6a_Handler(prcoObservable: PrcoObservable,
                                        addProductExecutors: AddProductExecutors,
                                        private var searchUrlBuilder: SearchUrlBuilder)

    : AddProcessHandler(prcoObservable, addProductExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(BuildNextSearchPageUrl_6a_Handler::class.java)!!
    }

    private fun handle(event: BuildNextSearchPageUrlEvent) {
        LOG.trace("handle $event")

        buildNextPageSearchUrlForGivenPageNumber(event.eshopUuid, event.searchKeyWord, event.currentPageNumber, event.identifier)
                .handle { nextPageSearchUrl, exception ->
                    if (exception == null) {
                        prcoObservable.notify(SearchKeywordUrlEvent(nextPageSearchUrl, event.currentPageNumber, event.searchKeyWord, event.eshopUuid, event.identifier))
                    } else {
                        prcoObservable.notify(BuildNextPageSearchUrlErrorEvent(event, exception))
                    }
                }
    }

    //TODO podobna funkcia je v NewKeyWordEvent_2_Handler#buildSearchUrlForKeyword
    private fun buildNextPageSearchUrlForGivenPageNumber(eshopUuid: EshopUuid, searchKeyword: String, currentPageNumber: Int,
                                                         identifier: String): CompletableFuture<String> {
        return CompletableFuture.supplyAsync(EshopLogSupplier(eshopUuid, identifier,
                Supplier {
                    LOG.trace("buildNextPageSearchUrlForGivenPageNumber")
                    val buildSearchUrl = searchUrlBuilder.buildSearchUrl(eshopUuid, searchKeyword, currentPageNumber)
                    LOG.debug("search URL for page $currentPageNumber : $buildSearchUrl")
                    buildSearchUrl
                }),
                addProductExecutors.searchUrlBuilderExecutor)
    }

    override fun update(source: Observable?, event: CoreEvent) {
        when (event) {
            is BuildNextSearchPageUrlEvent -> {
                LOG.trace(">> update BuildNextSearchPageUrlEvent")
                addProductExecutors.handlerTaskExecutor.submit {
                    MDC.put("eshop", event.eshopUuid.toString())
                    MDC.put("identifier", event.identifier)
                    handle(event)
                    MDC.remove("eshop")
                    MDC.remove("identifier")
                }
                LOG.trace("<< update BuildNextSearchPageUrlEvent")
            }
        }
    }
}


