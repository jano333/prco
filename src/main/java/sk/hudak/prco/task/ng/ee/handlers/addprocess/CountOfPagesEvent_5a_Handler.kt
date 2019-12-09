package sk.hudak.prco.task.ng.ee.handlers.addprocess

import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.stereotype.Component
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.task.ng.ee.AddProductExecutors
import sk.hudak.prco.task.ng.ee.BuildNextSearchPageUrlEvent
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
        LOG.trace("handle $event")

        if (event.countOfPages <= 1) {
            //TODO log ze je to tak?
            return
        }
        var index = 1
        for (currentPageNumber in 2..event.countOfPages) {
            val identifier = event.identifier + "_$index"
            index++
            // currentPageNumber -> searchUrlWithPageNumber
            prcoObservable.notify(BuildNextSearchPageUrlEvent(currentPageNumber, event.searchKeyWord, event.eshopUuid, identifier))
        }
    }

    override fun update(source: Observable?, event: CoreEvent) {
        when (event) {
            is CountOfPagesEvent -> {
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
