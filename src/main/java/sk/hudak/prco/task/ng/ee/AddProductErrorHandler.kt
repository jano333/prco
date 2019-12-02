package sk.hudak.prco.task.ng.ee

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.events.PrcoObserver
import java.util.*

@Component
class AddProductErrorHandler(prcoObservable: PrcoObservable,
                             private val addProductExecutors: AddProductExecutors)
    : PrcoObserver {

    companion object {
        private val LOG = LoggerFactory.getLogger(AddProductErrorHandler::class.java)!!
    }

    // registering itself as observer
    init {
        prcoObservable.addObserver(this)
    }

    override fun update(source: Observable?, event: CoreEvent) {
        when (event) {
            is RetrieveKeywordBaseOnKeywordIdErrorEvent -> handle_NewKeyWordIdErrorEvent(event)
            is BuildSearchUrlForKeywordErrorEvent -> handle_NewKeyWordErrorEvent(event)
            is RetrieveDocumentForSearchUrlErrorEvent -> handle_NewKeyWordUrlErrorEvent(event)
            is ParseCountOfPagesErrorEvent -> handle_FirstDocumentCountOfPageErrorEvent(event)
            is ParseProductListURLsErrorEvent -> handle_FirstDocumentPageProductUrlsErrorEvent(event)
            is FilterDuplicityErrorEvent -> handle_DuplicityCheckErrorEvent(event)
            is FilterNotExistingErrorEvent -> handle_FilterNotExistingErrorEvent(event)
            is RetrieveDocumentForUrlErrorEvent -> handle_RetrieveDocumentForUrlErrorEvent(event)
            is ParseProductNewDataErrorEvent -> handle_ParseProductNewDataErrorEvent(event)
            is SaveProductNewDataErrorEvent -> handle_SaveProductNewDataErrorEvent(event)
        }
    }

    private fun handle_NewKeyWordIdErrorEvent(errorEvent: RetrieveKeywordBaseOnKeywordIdErrorEvent) {
        logErrorEvent(errorEvent)
        addProductExecutors.shutdownNowAllExecutors()
    }

    private fun handle_NewKeyWordErrorEvent(errorEvent: BuildSearchUrlForKeywordErrorEvent) {
        logErrorEvent(errorEvent)
        addProductExecutors.shutdownNowAllExecutors()
    }

    private fun logErrorEvent(errorEvent: BasicErrorEvent){
        LOG.error("error while processing event ${errorEvent.event.javaClass.simpleName}")
        LOG.error("source event ${errorEvent.event}")
        LOG.error("${errorEvent.error.message}", errorEvent.error)
    }

    private fun handle_NewKeyWordUrlErrorEvent(errorEvent: RetrieveDocumentForSearchUrlErrorEvent) {
        LOG.error("error while processing event ${errorEvent.event.javaClass.simpleName}", errorEvent.error)
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun handle_SaveProductNewDataErrorEvent(errorEvent: SaveProductNewDataErrorEvent) {
        LOG.error("error while processing event ${errorEvent.event.javaClass.simpleName}", errorEvent.error)
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun handle_RetrieveDocumentForUrlErrorEvent(errorEvent: RetrieveDocumentForUrlErrorEvent) {
        LOG.error("error while processing event ${errorEvent.event.javaClass.simpleName}", errorEvent.error)
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun handle_ParseProductNewDataErrorEvent(errorEvent: ParseProductNewDataErrorEvent) {
        LOG.error("error while processing event ${errorEvent.event.javaClass.simpleName}", errorEvent.error)
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun handle_FilterNotExistingErrorEvent(errorEvent: FilterNotExistingErrorEvent) {
        LOG.error("error while processing event ${errorEvent.event.javaClass.simpleName}", errorEvent.error)
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun handle_DuplicityCheckErrorEvent(errorEvent: FilterDuplicityErrorEvent) {
        LOG.error("error while processing event ${errorEvent.event.javaClass.simpleName}", errorEvent.error)
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun handle_FirstDocumentCountOfPageErrorEvent(errorEvent: ParseCountOfPagesErrorEvent) {
        LOG.error("error while processing event ${errorEvent.event.javaClass.simpleName}", errorEvent.error)
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun handle_FirstDocumentPageProductUrlsErrorEvent(errorEvent: ParseProductListURLsErrorEvent) {
        LOG.error("error while processing event ${errorEvent.event.javaClass.simpleName}", errorEvent.error)
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}