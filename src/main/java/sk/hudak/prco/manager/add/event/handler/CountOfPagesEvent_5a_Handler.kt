package sk.hudak.prco.manager.add.event.handler

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.manager.add.event.AddProductExecutors
import sk.hudak.prco.manager.add.event.BuildNextSearchPageUrlEvent
import sk.hudak.prco.manager.add.event.CountOfPagesEvent

@Component
class CountOfPagesEvent_5a_Handler(prcoObservable: PrcoObservable,
                                   addProductExecutors: AddProductExecutors)

    : AddProcessHandler<CountOfPagesEvent>(prcoObservable, addProductExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(CountOfPagesEvent_5a_Handler::class.java)!!
    }

    override fun isSpecificType(event: CoreEvent): Boolean = event is CountOfPagesEvent
    override fun getEshopUuid(event: CountOfPagesEvent): EshopUuid? = event.eshopUuid
    override fun getIdentifier(event: CountOfPagesEvent): String = event.identifier

    override fun handle(event: CountOfPagesEvent) {

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
}
