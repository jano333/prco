package sk.hudak.prco.manager.add.event.handler

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.builder.SearchUrlBuilder
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.events.handler.EshopLogSupplier
import sk.hudak.prco.manager.add.event.AddProductExecutors
import sk.hudak.prco.manager.add.event.BuildNextPageSearchUrlErrorEvent
import sk.hudak.prco.manager.add.event.BuildNextSearchPageUrlEvent
import sk.hudak.prco.manager.add.event.SearchKeywordUrlEvent
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

@Component
class BuildNextSearchPageUrl_6a_Handler(prcoObservable: PrcoObservable,
                                        addProductExecutors: AddProductExecutors,
                                        private var searchUrlBuilder: SearchUrlBuilder)

    : AddProcessHandler<BuildNextSearchPageUrlEvent>(prcoObservable, addProductExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(BuildNextSearchPageUrl_6a_Handler::class.java)!!
    }

    override fun isSpecificType(event: CoreEvent): Boolean = event is BuildNextSearchPageUrlEvent
    override fun getEshopUuid(event: BuildNextSearchPageUrlEvent): EshopUuid? = event.eshopUuid
    override fun getIdentifier(event: BuildNextSearchPageUrlEvent): String = event.identifier

    override fun handle(event: BuildNextSearchPageUrlEvent) {
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

}


