package sk.hudak.prco.task.handler.add

import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import sk.hudak.prco.events.CoreEvent
import sk.hudak.prco.events.PrcoObservable
import sk.hudak.prco.task.add.AddProductExecutors
import sk.hudak.prco.task.add.NewEshopKeywordIdEvent
import sk.hudak.prco.task.add.NewKeywordIdEvent
import sk.hudak.prco.task.helper.EshopProductsParserHelper
import java.util.*

@Component
class NewKeywordIdEvent_Handler(prcoObservable: PrcoObservable,
                                addProductExecutors: AddProductExecutors,
                                val eshopProductsParserHelper: EshopProductsParserHelper)

    : AddProcessHandler(prcoObservable, addProductExecutors) {

    companion object {
        private val LOG = LoggerFactory.getLogger(NewKeywordIdEvent_Handler::class.java)!!
    }

    private fun handle(event: NewKeywordIdEvent) {
        LOG.trace("handle $event")

        eshopProductsParserHelper.getRegisteredEshopWithParser().forEach {
            prcoObservable.notify(NewEshopKeywordIdEvent(it, event.searchKeyWordId))
        }
    }

    override fun update(source: Observable?, event: CoreEvent) {
        when (event) {
            is NewKeywordIdEvent -> {
                LOG.trace(">> update ${event.javaClass.simpleName}")
                addProductExecutors.handlerTaskExecutor.submit {
                    handle(event)
                }
                LOG.trace("<< update ${event.javaClass.simpleName}")
            }
        }
    }

}