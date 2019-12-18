package sk.hudak.prco.manager.add.event.handler

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.events.helper.BasicDocumentHelper
import sk.hudak.prco.manager.add.event.AddProductExecutors
import sk.hudak.prco.manager.add.event.NewProductDocumentEvent
import sk.hudak.prco.manager.add.event.NewProductUrlWithEshopEvent
import sk.hudak.prco.manager.add.event.RetrieveDocumentForUrlErrorEvent

@Component
class NewProductUrlWithEshopEvent_6b_Handler(prcoObservable: PrcoObservable,
                                             addProductExecutors: AddProductExecutors,
                                             private val documentHelper: BasicDocumentHelper)
    : AddProcessHandler<NewProductUrlWithEshopEvent>(prcoObservable, addProductExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(NewProductUrlWithEshopEvent_6b_Handler::class.java)!!
    }

    override fun isSpecificType(event: CoreEvent): Boolean = event is NewProductUrlWithEshopEvent
    override fun getEshopUuid(event: NewProductUrlWithEshopEvent): EshopUuid? = event.eshopUuid
    override fun getIdentifier(event: NewProductUrlWithEshopEvent): String = event.identifier

    override fun handle(event: NewProductUrlWithEshopEvent) {
        LOG.trace("handle ${event.javaClass.simpleName}")

        // productURL -> Document
        documentHelper.retrieveDocumentForUrl(event.newProductUrl, event.eshopUuid, event.identifier)
                .handle { document, exception ->
                    if (exception == null) {
                        prcoObservable.notify(NewProductDocumentEvent(document, event.newProductUrl, event.eshopUuid, event.identifier))
                    } else {
                        prcoObservable.notify(RetrieveDocumentForUrlErrorEvent(event, exception))
                    }
                }
    }

}