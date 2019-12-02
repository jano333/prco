package sk.hudak.prco.task.ng.ee.handlers.addprocess

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.builder.SearchUrlBuilder
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.events.PrcoObserver
import sk.hudak.prco.task.ng.ee.AddProductExecutors
import sk.hudak.prco.task.ng.ee.BuildNextSearchPageUrl
import java.util.*
import java.util.concurrent.CompletableFuture
import java.util.function.Supplier

@Component
class BuildNextSearchPageUrl_6a_Handler(private val prcoObservable: PrcoObservable,
                                        private val addProductExecutors: AddProductExecutors,
                                        private var searchUrlBuilder: SearchUrlBuilder)
    : PrcoObserver {

    companion object {
        private val LOG = LoggerFactory.getLogger(BuildNextSearchPageUrl_6a_Handler::class.java)!!
    }

    // registering itself as observer
    init {
        prcoObservable.addObserver(this)
    }

    private fun handle(event: BuildNextSearchPageUrl) {
        LOG.trace("handle ${event.javaClass.simpleName}")
        buildNextPageSearchUrlForGivenPageNumber(event.eshopUuid, event.searchKeyWord, event.currentPageNumber)
                .handle { nextPageSearchUrl, exception ->
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
    }

    private fun buildNextPageSearchUrlForGivenPageNumber(eshopUuid: EshopUuid, searchKeyword: String, currentPageNumber: Int): CompletableFuture<String> {
        return CompletableFuture.supplyAsync(
                Supplier {
                    LOG.trace("buildNextPageSearchUrlForGivenPageNumber")
                    val buildSearchUrl = searchUrlBuilder.buildSearchUrl(eshopUuid, searchKeyword, currentPageNumber)
                    LOG.debug("search URL for page $currentPageNumber : $buildSearchUrl")
                    buildSearchUrl
                },
                addProductExecutors.searchUrlBuilderExecutor)
    }

    override fun update(source: Observable?, event: CoreEvent) {
        when (event) {
            is BuildNextSearchPageUrl -> addProductExecutors.handlerTaskExecutor.submit { handle(event) }
        }
    }
}