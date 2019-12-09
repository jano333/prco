package sk.hudak.prco.task.ng.ee.handlers.addprocess

import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Component
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.task.ng.ee.AddProductExecutors
import sk.hudak.prco.task.ng.ee.RetrieveDocumentForSearchUrlErrorEvent
import sk.hudak.prco.task.ng.ee.SearchKeywordUrlEvent
import sk.hudak.prco.task.ng.ee.SearchPageDocumentEvent
import sk.hudak.prco.task.ng.ee.helper.DocumentHelper
import java.util.*

/**
 * searchKeywordURL -> Document
 */
@Component
class SearchKeywordUrlEvent_3_Handler(prcoObservable: PrcoObservable,
                                      addProductExecutors: AddProductExecutors,
                                      private val documentHelper: DocumentHelper)

    : AddProcessHandler(prcoObservable, addProductExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(SearchKeywordUrlEvent_3_Handler::class.java)!!
    }

    /**
     * searchKeywordURL -> Document
     */
    private fun handle(event: SearchKeywordUrlEvent) {
        LOG.trace("handle $event")

        documentHelper.retrieveDocumentForUrl(event.searchUrl, event.eshopUuid, event.identifier)
                .handle { document, exception ->
                    if (exception == null) {
                        prcoObservable.notify(SearchPageDocumentEvent(document, event.pageNumber, event.eshopUuid,
                                event.searchKeyword, event.searchUrl, event.identifier))
                    } else {
                        prcoObservable.notify(RetrieveDocumentForSearchUrlErrorEvent(event, exception))
                    }
                }
    }

    override fun update(source: Observable?, event: CoreEvent) {
        when (event) {
            is SearchKeywordUrlEvent -> {
                LOG.trace(">> update ${event.javaClass.simpleName}")
                addProductExecutors.handlerTaskExecutor.submit {
                    MDC.put("eshop", event.eshopUuid.toString())
                    MDC.put("identifier", event.identifier.toString())
                    handle(event)
                    MDC.remove("eshop")
                    MDC.remove("identifier")
                }
                LOG.trace("<< update ${event.javaClass.simpleName}")
            }
        }
    }


}