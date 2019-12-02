package sk.hudak.prco.task.ng.ee.handlers.addprocess

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.task.ng.ee.*
import java.util.*

/**
 * searchKeywordURL -> Document
 */
@Component
class NewKeyWordSearchUrlEvent_3_Handler(prcoObservable: PrcoObservable,
                                         addProductExecutors: AddProductExecutors,
                                         private val documentHelper: DocumentHelper)

    : AddProcessHandler(prcoObservable, addProductExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(NewKeyWordSearchUrlEvent_3_Handler::class.java)!!
    }

    /**
     * searchKeywordURL -> Document
     */
    private fun handle(event: NewKeyWordUrlEvent) {
        LOG.trace("handle ${event.javaClass.simpleName}")

        documentHelper.retrieveDocumentForUrl(event.searchUrl, event.eshopUuid)
                .handle { document, exception ->
                    if (exception == null) {
                        prcoObservable.notify(FirstDocumentEvent(document, event.eshopUuid, event.searchKeyWord, event.searchUrl))
                    } else {
                        prcoObservable.notify(RetrieveDocumentForSearchUrlErrorEvent(event, exception))
                    }
                }
    }

    override fun update(source: Observable?, event: CoreEvent) {
        when (event) {
            is NewKeyWordUrlEvent -> addProductExecutors.handlerTaskExecutor.submit { handle(event) }
        }
    }


}