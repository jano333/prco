package sk.hudak.prco.manager.add.event.handler

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.events.handler.BasicHandler
import sk.hudak.prco.manager.add.event.AddProductExecutors
import sk.hudak.prco.manager.add.event.CountOfPagesEvent
import sk.hudak.prco.manager.add.event.NewProductEshopUrlsEvent
import java.util.*

/**
 * Zatial len vypis do loggov
 */
@Component
class HistoryLogAddProductHandler(prcoObservable: PrcoObservable,
                                  val addProductExecutors: AddProductExecutors)

    : BasicHandler(prcoObservable) {

    companion object {
        private val LOG = LoggerFactory.getLogger(HistoryLogAddProductHandler::class.java)!!
    }

    override fun update(source: Observable?, event: CoreEvent) {
        addProductExecutors.handlerTaskExecutor.submit {
            when (event) {
                is CountOfPagesEvent -> {
                    LOG.debug("${event.eshopUuid}: keyword: ${event.searchKeyWord}, count of pages: ${event.countOfPages}, search URL: ${event.searchUrl}")
                }
                is NewProductEshopUrlsEvent -> {
                    LOG.debug("${event.eshopUuid}: keyword: ${event.searchKeyWord}, page number: ${event.pageNumber} count of products: ${event.pageProductURLs.size}, search URL: ${event.searchPageDocument.location()}")
                }
            }
        }

    }

}
