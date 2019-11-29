package sk.hudak.prco.task.ng.ee

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.events.PrcoObserver
import java.util.*

@Component
class AddProductErrorHandler(prcoObservable: PrcoObservable,
                             private val executors: Executors)
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
            is NewKeyWordIdErrorEvent -> handle_NewKeyWordIdErrorEvent(event)
            is NewKeyWordErrorEvent -> handle_NewKeyWordErrorEvent(event)
            is NewKeyWordUrlErrorEvent -> handle_NewKeyWordUrlErrorEvent(event)
            is FirstDocumentCountOfPageErrorEvent -> handle_FirstDocumentCountOfPageErrorEvent(event)
            is FirstDocumentPageProductUrlsErrorEvent -> handle_FirstDocumentPageProductUrlsErrorEvent(event)
        }
    }

    private fun handle_NewKeyWordIdErrorEvent(errorEvent: NewKeyWordIdErrorEvent) {
        LOG.error("error while processing event ${errorEvent.event.javaClass.simpleName}", errorEvent.error)
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        //TODO  stopnut ale co vsetko???
    }

    private fun handle_NewKeyWordErrorEvent(errorEvent: NewKeyWordErrorEvent) {
        LOG.error("error while processing event ${errorEvent.event.javaClass.simpleName}", errorEvent.error)
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun handle_NewKeyWordUrlErrorEvent(errorEvent: NewKeyWordUrlErrorEvent) {
        LOG.error("error while processing event ${errorEvent.event.javaClass.simpleName}", errorEvent.error)
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun handle_FirstDocumentCountOfPageErrorEvent(errorEvent: FirstDocumentCountOfPageErrorEvent) {
        LOG.error("error while processing event ${errorEvent.event.javaClass.simpleName}", errorEvent.error)
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun handle_FirstDocumentPageProductUrlsErrorEvent(errorEvent: FirstDocumentPageProductUrlsErrorEvent) {
        LOG.error("error while processing event ${errorEvent.event.javaClass.simpleName}", errorEvent.error)
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}