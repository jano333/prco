package sk.hudak.prco.task.handler.add

import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Component
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.task.add.*
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
        documentHelper.retrieveDocumentForUrl(event.newProductUrl, event.eshopUuid, event.identifier)
                .handle { document, exception ->
                    if (exception == null) {
                        prcoObservable.notify(NewProductDocumentEvent(document, event.newProductUrl, event.eshopUuid, event.identifier))
                    } else {
                        prcoObservable.notify(RetrieveDocumentForUrlErrorEvent(event, exception))
                    }
                }
    }

    override fun update(source: Observable?, event: CoreEvent) {
        when (event) {
            is NewProductUrlWithEshopEvent -> {
                LOG.trace(">> update ${event.javaClass.simpleName}")
                addProductExecutors.handlerTaskExecutor.submit {
                    MDC.put("eshop", event.eshopUuid.toString())
                    MDC.put("identifier", event.identifier)
                    handle(event)
                    MDC.remove("eshop")
                    MDC.remove("identifier")
                }
                LOG.trace("<< update ${event.javaClass.simpleName}")
            }
        }
    }
}