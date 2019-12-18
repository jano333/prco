package sk.hudak.prco.manager.add.event.handler

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.builder.SearchUrlBuilder
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.events.handler.EshopLogSupplier
import sk.hudak.prco.manager.add.event.AddProductExecutors
import sk.hudak.prco.manager.add.event.BuildSearchUrlForKeywordErrorEvent
import sk.hudak.prco.manager.add.event.NewKeywordEvent
import sk.hudak.prco.manager.add.event.SearchKeywordUrlEvent
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

/**
 * searchKeyword -> searchKeywordURL
 */
@Component
class NewKeyWordEvent_2_Handler(prcoObservable: PrcoObservable,
                                addProductExecutors: AddProductExecutors,
                                private var searchUrlBuilder: SearchUrlBuilder)

    : AddProcessHandler<NewKeywordEvent>(prcoObservable, addProductExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(NewKeyWordEvent_2_Handler::class.java)!!
    }

    override fun isSpecificType(event: CoreEvent): Boolean = event is NewKeywordEvent
    override fun getEshopUuid(event: NewKeywordEvent): EshopUuid? = event.eshopUuid
    override fun getIdentifier(event: NewKeywordEvent): String = event.identifier


    /**
     * searchKeyword -> searchKeywordRL
     */
    override fun handle(event: NewKeywordEvent) {
        LOG.trace("handle $event")

        buildSearchUrlForKeyword(event.eshopUuid, event.searchKeyword, event.identifier)
                .handle { searchUrl, exception ->
                    if (exception == null) {
                        prcoObservable.notify(SearchKeywordUrlEvent(searchUrl, 1, event.searchKeyword, event.eshopUuid, event.identifier))
                    } else {
                        prcoObservable.notify(BuildSearchUrlForKeywordErrorEvent(event, exception))
                    }
                }
    }

    private fun buildSearchUrlForKeyword(eshopUuid: EshopUuid, searchKeyword: String, identifier: String): CompletableFuture<String> {
        return CompletableFuture.supplyAsync(EshopLogSupplier(eshopUuid, identifier,
                Supplier {
                    LOG.trace("buildSearchUrlForKeyword")
                    val searchUrl = searchUrlBuilder.buildSearchUrl(eshopUuid, searchKeyword)
                    LOG.debug("build url for keyword $searchKeyword : $searchUrl")
                    searchUrl
                }),
                addProductExecutors.searchUrlBuilderExecutor)
    }

}