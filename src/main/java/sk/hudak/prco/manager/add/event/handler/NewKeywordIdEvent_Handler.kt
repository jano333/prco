package sk.hudak.prco.manager.add.event.handler

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.api.EshopUuid
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.events.helper.EshopProductsParserHelper
import sk.hudak.prco.manager.add.event.AddProductExecutors
import sk.hudak.prco.manager.add.event.NewEshopKeywordIdEvent
import sk.hudak.prco.manager.add.event.NewKeywordIdEvent

@Component
class NewKeywordIdEvent_Handler(prcoObservable: PrcoObservable,
                                addProductExecutors: AddProductExecutors,
                                val eshopProductsParserHelper: EshopProductsParserHelper)

    : AddProcessHandler<NewKeywordIdEvent>(prcoObservable, addProductExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(NewKeywordIdEvent_Handler::class.java)!!
    }

    override fun isSpecificType(event: CoreEvent): Boolean = event is NewKeywordIdEvent
    override fun getEshopUuid(event: NewKeywordIdEvent): EshopUuid? = null
    override fun getIdentifier(event: NewKeywordIdEvent): String = event.identifier

    override fun handle(event: NewKeywordIdEvent) {

        //TODO generovat sub id _1 a _2 ...
        eshopProductsParserHelper.getRegisteredEshopWithParser().forEach {
            prcoObservable.notify(NewEshopKeywordIdEvent(it, event.searchKeyWordId))
        }
    }
}