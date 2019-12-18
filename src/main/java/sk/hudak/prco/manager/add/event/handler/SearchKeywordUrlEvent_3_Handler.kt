package sk.hudak.prco.manager.add.event.handler

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.events.helper.BasicDocumentHelper
import sk.hudak.prco.manager.add.event.AddProductExecutors
import sk.hudak.prco.manager.add.event.RetrieveDocumentForSearchUrlErrorEvent
import sk.hudak.prco.manager.add.event.SearchKeywordUrlEvent
import sk.hudak.prco.manager.add.event.SearchPageDocumentEvent

/**
 * searchKeywordURL -> Document
 */
@Component
class SearchKeywordUrlEvent_3_Handler(prcoObservable: PrcoObservable,
                                      addProductExecutors: AddProductExecutors,
                                      private val documentHelper: BasicDocumentHelper)

    : AddProcessHandler<SearchKeywordUrlEvent>(prcoObservable, addProductExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(SearchKeywordUrlEvent_3_Handler::class.java)!!
    }

    override fun isSpecificType(event: CoreEvent): Boolean = event is SearchKeywordUrlEvent
    override fun getEshopUuid(event: SearchKeywordUrlEvent): EshopUuid? = event.eshopUuid
    override fun getIdentifier(event: SearchKeywordUrlEvent): String = event.identifier

    /**
     * searchKeywordURL -> Document
     */
    override fun handle(event: SearchKeywordUrlEvent) {
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
}