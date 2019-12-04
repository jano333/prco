package sk.hudak.prco.task.ng.ee.handlers.addprocess

import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Component
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.task.ng.ee.AddProductExecutors
import sk.hudak.prco.task.ng.ee.NewProductDocumentEvent
import sk.hudak.prco.task.ng.ee.NewProductUrlWithEshopEvent
import sk.hudak.prco.task.ng.ee.RetrieveDocumentForUrlErrorEvent
import sk.hudak.prco.task.ng.ee.helper.DocumentHelper
import java.util.*

@Component
class NewProductUrlWithEshopEvent_6b_Handler(prcoObservable: PrcoObservable,
                                             addProductExecutors: AddProductExecutors,
                                             private val documentHelper: DocumentHelper)
    : AddProcessHandler(prcoObservable, addProductExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(NewProductUrlWithEshopEvent_6b_Handler::class.java)!!
    }

    private fun handle(event: NewProductUrlWithEshopEvent) {
        LOG.trace("handle ${event.javaClass.simpleName}")

        // productURL -> Document
        documentHelper.retrieveDocumentForUrl(event.newProductUrl, event.eshopUuid)
                .handle { document, exception ->
                    if (exception == null) {
                        prcoObservable.notify(NewProductDocumentEvent(document, event.newProductUrl, event.eshopUuid))
                    } else {
                        prcoObservable.notify(RetrieveDocumentForUrlErrorEvent(event, exception))
                    }
                }
    }

    override fun update(source: Observable?, event: CoreEvent) {
        when (event) {
            is NewProductUrlWithEshopEvent -> addProductExecutors.handlerTaskExecutor.submit {
                MDC.put("eshop", event.eshopUuid.toString())
                handle(event)
                MDC.remove("eshop")
            }
        }
    }
}