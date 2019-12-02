package sk.hudak.prco.task.ng.ee.handlers.addprocess

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.task.ng.ee.AddProductExecutors
import sk.hudak.prco.task.ng.ee.BuildNextSearchPageUrl
import sk.hudak.prco.task.ng.ee.CountOfPagesEvent
import java.util.*

@Component
class CountOfPagesEvent_5a_Handler(prcoObservable: PrcoObservable,
                                   addProductExecutors: AddProductExecutors)
    : AddProcessHandler(prcoObservable, addProductExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(CountOfPagesEvent_5a_Handler::class.java)!!
    }

    private fun handle(event: CountOfPagesEvent) {
        LOG.trace("handle ${event.javaClass.simpleName}")
        LOG.debug(event.countOfPages.toString())
        //TODO log with processing id? mozno...
        LOG.ahoj("")
        if (event.countOfPages <= 1) {
            return
        }
        for (currentPageNumber in 2..event.countOfPages) {
            // currentPageNumber -> searchUrlWithPageNumber
            prcoObservable.notify(BuildNextSearchPageUrl(currentPageNumber, event.searchKeyWord, event.eshopUuid))
        }
    }

    override fun update(source: Observable?, event: CoreEvent) {
        when (event) {
            is CountOfPagesEvent -> addProductExecutors.handlerTaskExecutor.submit { handle(event) }
        }
    }

}

private fun Any.ahoj(text: String) {
    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.

}
